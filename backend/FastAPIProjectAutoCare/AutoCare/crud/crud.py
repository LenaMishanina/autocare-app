# from pyexpat.errors import messages
#
# from sqlalchemy.orm import Session
# from AutoCare.models.models import User, Car, ServiceEvent , ServiceHistory, Notification
# from AutoCare.auth.utils import hash_password, verify_password
#
# # +++ USERS +++
# def get_user_by_email(db: Session, email: str):
#     return db.query(User).filter(User.email == email).first()
#
# def create_user(db: Session, email: str, full_name: str, password: str):
#     hashed = hash_password(password)
#     user = User(email=email, full_name=full_name, password_hash=hashed)
#     db.add(user)
#     db.commit()
#     db.refresh(user)
#     return user
#
# # +++ CARS +++
# def create_car(db: Session, user_id: int, brand: str, model: str, year: int ):
#     car = Car(user_id=user_id, brand=brand, model=model, year=year)
#     db.add(car)
#     db.commit()
#     db.refresh(car)
#     return car
#
# def get_user_cars(db: Session, user_id: int):
#     return db.query(Car).filter_by(user_id=user_id).all()
#
# # +++ SERVICE EVENTS +++
# def create_service_event(db: Session, car_id: int, event_name: str, date):
#     event = ServiceEvent( car_id=car_id, event_name=event_name, date=date)
#     db.add(event)
#     db.commit()
#     db.refresh(event)
#     return event
#
# # +++ SERVICE HISTORY +++
# def create_service_history(db: Session, car_id: int, service_type: str, date, mileage: float):
#     service = ServiceHistory(car_id=car_id, service_type=service_type, service_date=date, mileage=mileage)
#     db.add(service)
#     db.commit()
#     db.refresh(service)
#     return service
#
# # +++ NOTIFICATIONS +++
# def create_notification(db: Session, event_id: int, message: str, scheduled_at):
#     notif = Notification(event_id=event_id, message=message, scheduled_at=scheduled_at)
#     db.add(notif)
#     db.commit()
#     db.refresh(notif)
#     return notif
#
#
#
