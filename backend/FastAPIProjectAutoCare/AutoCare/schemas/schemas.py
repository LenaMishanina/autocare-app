from  pydantic import BaseModel, EmailStr
from typing import Optional
from datetime import datetime, date
from pydantic import BaseModel, EmailStr


# === USERS ===
class UserBase(BaseModel):
    email: EmailStr
    full_name: str

class UserCreate(UserBase):
    password: str

class UserResponse(UserBase):
    user_id: int
    email: EmailStr
    full_name: str
    notify_on: Optional[bool] = None
    created_at: datetime

    class Config:
        from_attributes = True

class UserRenameRequest(BaseModel):
    full_name: str


# === CARS ===
class CarBase(BaseModel):
    brand: str
    model: str
    year: int
    mileage: int

# class CarCreate(CarBase): pass


class CarCreate(BaseModel):
    brand: str
    model: str
    year: int
    mileage: int


class CarResponse(CarBase):
    # id: int
    car_id: int  # Match the model's field name
    brand: str
    model: str
    year: int
    mileage: int
    user_id: int

    class Config:
        from_attributes = True

# === SERVICE HISTORY ===
class ServiceHistoryBase(BaseModel):
    car_id: int
    service_type: str
    service_date: date
    mileage: float
    created_at: datetime

class ServiceHistoryCreate(ServiceHistoryBase): pass


class ServiceHistoryResponse(ServiceHistoryBase):
    event_id: int

    class Config:
        from_attributes = True


# === SERVICE EVENT ===
class ServiceEventBase(BaseModel):
    car_id: int  # The car associated with the service event
    service_type: str  # Renamed from event_name to match database
    due_date: date  # Renamed from scheduled_date to match database
    due_mileage: float  # Renamed to match database (previously mileage_target)

    class Config:
        from_attributes = True # Enable Pydantic to work with SQLAlchemy models

class ServiceEventCreate(ServiceEventBase):
    pass  # Used for creating new service events

class ServiceEventResponse(ServiceEventBase):
    event_id: int
    is_completed: bool  # Добавляем это поле, так как оно есть в модели
    due_date: date
    # service_history: list[ServiceHistoryResponse]

    # Added event_id to match response format

    class Config:
        from_attributes = True  # Convert SQLAlchemy model attributes to Pydantic models

class UpdateEventSchema(BaseModel):
    service_type: Optional[str] = None  # Renamed from event_name
    due_date: Optional[datetime] = None  # Renamed from scheduled_date
    due_mileage: Optional[float] = None
    is_completed: Optional[bool] = None
    created_at: Optional[datetime] = None


    class Config:
        from_attributes = True  # Enable Pydantic to work with SQLAlchemy models



# === NOTIFICATIONS ===
class NotificationBase(BaseModel):
    event_id: int
    message: str
    notify_time: datetime
    is_sent: Optional[bool] = None

# class NotificationCreate(NotificationBase): pass

class NotificationCreate(BaseModel):
    event_id: int
    message: str
    notify_time: datetime
    is_sent: Optional[bool] = None

    class Config:
        from_attributes = True

class NotificationResponse(NotificationBase):
    event_id: int
    message: str
    notify_time: datetime
    is_sent: Optional[bool] = None
    created_at: Optional[datetime] = None


    class Config:
      from_attributes = True


# === TOKEN / AUTH ===
class TokenResponse(BaseModel):
    access_token: str
    token_type: str


class LoginRequest(BaseModel):
    email: EmailStr
    password: str


class RegisterRequest(BaseModel):
    full_name: str
    email: EmailStr
    password: str


class ForgotPasswordRequest(BaseModel):
    email: EmailStr


class ResetPasswordRequest(BaseModel):
    token: str
    new_password: str

class TokenPayload(BaseModel):
    sub: str  # user ID as string
    exp: int

