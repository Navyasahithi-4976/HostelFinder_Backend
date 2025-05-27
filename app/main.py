from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from typing import List
from datetime import timedelta
from . import models, schemas, security
from .database import engine, get_db
from .ai_service import AIService

# Create database tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="Hostel Finder API",
    description="API for finding and booking hostels with AI-powered recommendations",
    version="1.0.0"
)
ai_service = AIService()

@app.get("/", tags=["Root"])
async def read_root():
    return {
        "message": "Welcome to Hostel Finder API",
        "version": "1.0.0",
        "documentation": "/docs",
        "endpoints": {
            "hostels": "/hostels/",
            "smart_search": "/smart-search/{pincode}",
            "users": "/users/",
            "bookings": "/bookings/",
            "reviews": "/reviews/"
        }
    }

# Auth endpoints
@app.post("/token", response_model=schemas.Token)
async def login_for_access_token(
    form_data: OAuth2PasswordRequestForm = Depends(),
    db: Session = Depends(get_db)
):
    user = db.query(models.User).filter(models.User.email == form_data.username).first()
    if not user or not security.verify_password(form_data.password, user.password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token_expires = timedelta(minutes=security.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = security.create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

# User endpoints
@app.post("/users/", response_model=schemas.User)
async def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    db_user = db.query(models.User).filter(models.User.email == user.email).first()
    if db_user:
        raise HTTPException(status_code=400, detail="Email already registered")
    db_user = models.User(
        email=user.email,
        full_name=user.full_name,
        hashed_password=security.get_password_hash(user.password)
    )
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

# Hostel endpoints
@app.post("/hostels/", response_model=schemas.Hostel)
async def create_hostel(
    hostel: schemas.HostelCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(security.check_admin_access)
):
    db_hostel = models.Hostel(**hostel.dict())
    db.add(db_hostel)
    db.commit()
    db.refresh(db_hostel)
    return db_hostel

@app.get("/hostels/", response_model=List[schemas.Hostel])
async def get_hostels(
    skip: int = 0,
    limit: int = 100,
    db: Session = Depends(get_db)
):
    hostels = db.query(models.Hostel).offset(skip).limit(limit).all()
    return hostels

@app.get("/hostels/{hostel_id}", response_model=schemas.Hostel)
async def get_hostel(hostel_id: int, db: Session = Depends(get_db)):
    hostel = db.query(models.Hostel).filter(models.Hostel.id == hostel_id).first()
    if hostel is None:
        raise HTTPException(status_code=404, detail="Hostel not found")
    return hostel

# Smart Search endpoint
@app.get("/smart-search/{pincode}", response_model=schemas.SmartSearchResult)
async def smart_search(
    pincode: str,
    db: Session = Depends(get_db)
):
    # Direct search
    direct_results = db.query(models.Hostel).filter(models.Hostel.pincode == pincode).all()
    
    if direct_results:
        return schemas.SmartSearchResult(
            exact_match=True,
            searched_pincode=pincode,
            direct_results=direct_results,
            suggested_results=[],
            suggested_locations=[]
        )
    
    # AI-powered search
    similar_locations = await ai_service.get_similar_locations(pincode)
    suggested_results = []
    for location in similar_locations:
        hostels = db.query(models.Hostel).filter(models.Hostel.city == location).all()
        suggested_results.extend(hostels)
    
    ai_suggestion = f"No hostels found in pincode {pincode}. Here are some suggestions from nearby areas: {', '.join(similar_locations)}"
    
    return schemas.SmartSearchResult(
        exact_match=False,
        searched_pincode=pincode,
        direct_results=[],
        suggested_results=suggested_results,
        suggested_locations=similar_locations,
        ai_suggestion=ai_suggestion
    )

# Booking endpoints
@app.post("/bookings/", response_model=schemas.Booking)
async def create_booking(
    booking: schemas.BookingCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(security.get_current_active_user)
):
    # Check hostel availability
    hostel = db.query(models.Hostel).filter(models.Hostel.id == booking.hostel_id).first()
    if not hostel:
        raise HTTPException(status_code=404, detail="Hostel not found")
    
    if hostel.available_rooms < 1:
        raise HTTPException(status_code=400, detail="No rooms available")
    
    # Calculate total price
    days = (booking.check_out_date - booking.check_in_date).days
    total_price = days * hostel.price_per_night * booking.number_of_beds
    
    db_booking = models.Booking(
        **booking.dict(),
        user_id=current_user.id,
        total_price=total_price
    )
    
    # Update hostel availability
    hostel.available_rooms -= 1
    
    db.add(db_booking)
    db.commit()
    db.refresh(db_booking)
    return db_booking

# Review endpoints
@app.post("/reviews/", response_model=schemas.Review)
async def create_review(
    review: schemas.ReviewCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(security.get_current_active_user)
):
    # Check if hostel exists
    hostel = db.query(models.Hostel).filter(models.Hostel.id == review.hostel_id).first()
    if not hostel:
        raise HTTPException(status_code=404, detail="Hostel not found")
    
    # Check if user has already reviewed this hostel
    existing_review = db.query(models.Review).filter(
        models.Review.hostel_id == review.hostel_id,
        models.Review.user_id == current_user.id
    ).first()
    
    if existing_review:
        raise HTTPException(status_code=400, detail="You have already reviewed this hostel")
    
    db_review = models.Review(
        **review.dict(),
        user_id=current_user.id
    )
    
    # Update hostel rating
    all_reviews = db.query(models.Review).filter(models.Review.hostel_id == review.hostel_id).all()
    total_rating = sum(r.rating for r in all_reviews) + review.rating
    hostel.rating = total_rating / (len(all_reviews) + 1)
    
    db.add(db_review)
    db.commit()
    db.refresh(db_review)
    return db_review
