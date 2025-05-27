from sqlalchemy import Column, Integer, String, DECIMAL, ForeignKey, DateTime, Enum, Table, Text, Date
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from .database import Base
import enum

class UserType(str, enum.Enum):
    SEEKER = "seeker"
    OWNER = "owner"

class BookingStatus(str, enum.Enum):
    PENDING = "pending"
    CONFIRMED = "confirmed"
    CANCELLED = "cancelled"

class User(Base):
    __tablename__ = "Users"

    user_id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(100))
    email = Column(String(100), unique=True)
    password = Column(String(255))
    phone = Column(String(15))
    #user_type = Column(Enum(UserType), default=UserType.SEEKER)
    user_type = Column(String(20), default=UserType.SEEKER)
    created_at = Column(DateTime, server_default=func.now())

    hostels = relationship("Hostel", back_populates="owner")
    bookings = relationship("Booking", back_populates="user")
    reviews = relationship("Review", back_populates="user")

class Hostel(Base):
    __tablename__ = "Hostels"

    hostel_id = Column(Integer, primary_key=True, autoincrement=True)
    owner_id = Column(Integer, ForeignKey("Users.user_id"))
    name = Column(String(100))
    location = Column(String(255))
    city = Column(String(100))
    rent = Column(DECIMAL(10,2))
    description = Column(Text)
    available_rooms = Column(Integer)
    pincode = Column(String(10))
    created_at = Column(DateTime, server_default=func.now())

    owner = relationship("User", back_populates="hostels")
    amenities = relationship("Amenity", secondary="Hostel_Amenities")
    bookings = relationship("Booking", back_populates="hostel")
    reviews = relationship("Review", back_populates="hostel")
    images = relationship("HostelImage", back_populates="hostel")

class Amenity(Base):
    __tablename__ = "Amenities"

    amenity_id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String(100))

    hostels = relationship("Hostel", secondary="Hostel_Amenities")

class HostelAmenity(Base):
    __tablename__ = "Hostel_Amenities"

    hostel_id = Column(Integer, ForeignKey("Hostels.hostel_id"), primary_key=True)
    amenity_id = Column(Integer, ForeignKey("Amenities.amenity_id"), primary_key=True)

class Booking(Base):
    __tablename__ = "Bookings"

    booking_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey("Users.user_id"))
    hostel_id = Column(Integer, ForeignKey("Hostels.hostel_id"))
    booking_date = Column(Date)
    check_in = Column(Date)
    check_out = Column(Date)
    status = Column(Enum(BookingStatus), default=BookingStatus.PENDING)

    user = relationship("User", back_populates="bookings")
    hostel = relationship("Hostel", back_populates="bookings")

class Review(Base):
    __tablename__ = "Reviews"

    review_id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey("Users.user_id"))
    hostel_id = Column(Integer, ForeignKey("Hostels.hostel_id"))
    rating = Column(Integer)
    comment = Column(Text)
    created_at = Column(DateTime, server_default=func.now())

    user = relationship("User", back_populates="reviews")
    hostel = relationship("Hostel", back_populates="reviews")

class HostelImage(Base):
    __tablename__ = "Hostel_Images"

    image_id = Column(Integer, primary_key=True, autoincrement=True)
    hostel_id = Column(Integer, ForeignKey("Hostels.hostel_id"))
    image_url = Column(String(255))

    hostel = relationship("Hostel", back_populates="images")
