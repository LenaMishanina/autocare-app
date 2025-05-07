from datetime import datetime
from sqlalchemy import Column, Integer, Float, String, DateTime, Boolean, ForeignKey, TIMESTAMP, Date
from sqlalchemy.orm import relationship
from database import Base
from sqlalchemy import func  # Ajoutez cette ligne

class User(Base):
    __tablename__ = 'users'
    user_id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String, nullable=False)
    email = Column(String, nullable=False, unique=True)
    password_hash = Column("password", String, nullable=False)
    # password = Column(String, nullable=False)
    notify_on = Column(Boolean, default=True)
    created_at = Column(DateTime, server_default=func.now())


    cars = relationship('Car', back_populates='owner', cascade="all, delete")

    reset_tokens = relationship("ResetToken", back_populates="user", cascade="all, delete-orphan", foreign_keys="[ResetToken.user_id]")


class Car(Base):
    __tablename__ = 'cars'
    car_id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey('users.user_id'))
    brand = Column(String, nullable=False)
    model = Column(String, nullable=False)
    year = Column(Integer, nullable=False)
    mileage = Column(Integer, nullable=True)

    owner = relationship('User', back_populates='cars')
    service_events = relationship('ServiceEvent', back_populates='car', cascade="all, delete-orphan")  # Suppression en cascade



class ServiceEvent(Base):
     __tablename__ = 'service_events'
     event_id = Column(Integer, primary_key=True, index=True)
     car_id = Column(Integer, ForeignKey('cars.car_id'))
     # service_event_id = Column(Integer, ForeignKey('service_events.event_id', ondelete='SET NULL'))
     service_type = Column(Integer, nullable=False)
     due_date = Column(Date, nullable=False)
     due_mileage = Column(Float, nullable=False)
     is_completed = Column(Boolean, default=False, nullable=False)

     car = relationship('Car', back_populates='service_events')
     notifications = relationship('Notification', back_populates='service_events', cascade="all, delete")  # Changé 'service' en 'event'


class Notification(Base):
    __tablename__ = 'notifications'
    notification_id = Column(Integer, primary_key=True, index=True)
    event_id = Column(Integer, ForeignKey("service_events.event_id", ondelete="CASCADE"), nullable=False)
    message = Column(String, nullable=False)
    notify_time = Column(TIMESTAMP, nullable=False)
    is_sent = Column(Boolean, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    service_events = relationship('ServiceEvent', back_populates='notifications')



class ServiceHistory(Base):
    __tablename__ = 'service_history'
    event_id = Column(Integer, primary_key=True, index=True)
    car_id = Column(Integer, ForeignKey('cars.car_id'))
    service_type = Column(Integer, nullable=False)
    service_date = Column(Integer, nullable=False)
    mileage = Column(Integer, nullable=False)


class ResetToken(Base):
    __tablename__ = "reset_tokens"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.user_id", ondelete="CASCADE"), nullable=False)
    token = Column(String, unique=True, nullable=False)
    expires_at = Column(DateTime, nullable=False)
    created_at = Column(DateTime, server_default=func.now())

    user = relationship("User", back_populates="reset_tokens")

