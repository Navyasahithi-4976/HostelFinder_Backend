from pydantic import BaseModel, EmailStr, constr, validator, Field
from typing import List, Optional
from datetime import datetime, date
from decimal import Decimal
from .models import UserType, BookingStatus

class UserBase(BaseModel):
    name: str
    email: EmailStr
    phone: constr(regex=r"^[0-9]{10}$")

class UserCreate(UserBase):
    password: constr(min_length=8)
    user_type: UserType = UserType.SEEKER

class User(UserBase):
    user_id: int
    user_type: UserType
    created_at: datetime

    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: str | None = None

class AmenityBase(BaseModel):
    name: str

class Amenity(AmenityBase):
    amenity_id: int

    class Config:
        from_attributes = True

class HostelBase(BaseModel):
    name: constr(min_length=3, max_length=100)
    location: str
    city: str
    rent: Decimal = Field(gt=0)
    description: Optional[str] = None
    available_rooms: int = Field(ge=0)
    pincode: Optional[str] = None

class HostelCreate(HostelBase):
    owner_id: int

class Hostel(HostelBase):
    hostel_id: int
    owner_id: int
    created_at: datetime
    amenities: List[Amenity] = []

    class Config:
        from_attributes = True

class BookingBase(BaseModel):
    hostel_id: int
    check_in: date
    check_out: date

    @validator('check_out')
    def check_dates(cls, v, values):
        if 'check_in' in values and v <= values['check_in']:
            raise ValueError('check_out must be after check_in')
        return v

class BookingCreate(BookingBase):
    pass

class Booking(BookingBase):
    booking_id: int
    user_id: int
    booking_date: date
    status: BookingStatus
    hostel: Hostel
    user: User

    class Config:
        from_attributes = True

class ReviewBase(BaseModel):
    hostel_id: int
    rating: int = Field(ge=1, le=5)
    comment: str

class ReviewCreate(ReviewBase):
    pass

class Review(ReviewBase):
    review_id: int
    user_id: int
    created_at: datetime
    user: User

    class Config:
        from_attributes = True

class HostelImageBase(BaseModel):
    hostel_id: int
    image_url: str

class HostelImageCreate(HostelImageBase):
    pass

class HostelImage(HostelImageBase):
    image_id: int

    class Config:
        from_attributes = True

class SmartSearchResult(BaseModel):
    exact_match: bool
    searched_pincode: str
    direct_results: List[Hostel]
    suggested_results: List[Hostel]
    suggested_locations: List[str]
    ai_suggestion: Optional[str] = None
