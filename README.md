# Hostel Finder FastAPI Backend

A modern REST API for the Hostel Finder application built with FastAPI, SQLAlchemy, and DeepSeek AI integration.

## Features

- User authentication with JWT
- Hostel management
- Booking system
- Review system
- AI-powered smart search
- Role-based access control (Admin/User)

## Tech Stack

- FastAPI
- SQLAlchemy (ORM)
- PyMySQL (Database)
- Pydantic (Data validation)
- JWT (Authentication)
- DeepSeek AI (Smart search)

## Setup

1. Create a virtual environment:
   ```bash
   python -m venv venv
   source venv/bin/activate  # Linux/Mac
   venv\Scripts\activate     # Windows
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Set up the database:
   ```bash
   # Create MySQL database
   mysql -u root -p
   CREATE DATABASE hostelfinder;
   ```

4. Configure environment variables:
   - Copy `.env.example` to `.env`
   - Update the values in `.env`

5. Run the application:
   ```bash
   uvicorn app.main:app --reload
   ```

## API Documentation

Once running, visit:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## API Endpoints

### Authentication
- POST /token - Get access token
- POST /users/ - Create new user

### Hostels
- GET /hostels/ - List all hostels
- GET /hostels/{id} - Get hostel details
- POST /hostels/ - Create hostel (Admin only)
- GET /smart-search/{pincode} - Smart search with AI

### Bookings
- POST /bookings/ - Create booking
- GET /bookings/ - List user's bookings
- PUT /bookings/{id} - Update booking

### Reviews
- POST /reviews/ - Create review
- GET /reviews/hostel/{id} - Get hostel reviews

## Environment Variables

- `DEEPSEEK_API_URL` - DeepSeek API URL
- `DEEPSEEK_API_KEY` - DeepSeek API Key
- `DATABASE_URL` - Database connection URL
- `SECRET_KEY` - JWT secret key
