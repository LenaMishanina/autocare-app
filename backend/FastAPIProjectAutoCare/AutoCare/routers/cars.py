# app/routers/cars.py
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from AutoCare.models.models import Car, ServiceEvent
from AutoCare.schemas.schemas import CarCreate, CarResponse
from dependencies import get_current_user
from database import get_db
from fastapi import Body



router = APIRouter(prefix="/cars", tags=["cars"])

@router.post("/", response_model=CarResponse)
def add_car(car: CarCreate, db: Session = Depends(get_db), user=Depends(get_current_user)):
    db_car = Car(**car.dict(), user_id=user.user_id)
    db.add(db_car)
    db.commit()
    db.refresh(db_car)
    return db_car

@router.get("/", response_model=list[CarResponse])
def get_cars(db: Session = Depends(get_db), user=Depends(get_current_user)):
    return db.query(Car).filter(Car.user_id == user.user_id).all()

@router.put("/{car_id}", response_model=CarResponse)
def update_car(car_id: int, updated_car: CarCreate, db: Session = Depends(get_db), user=Depends(get_current_user)):
    car = db.query(Car).filter(Car.car_id == car_id, Car.user_id == user.user_id).first()
    if not car:
        return {"error": "Car not found"}
    for key, value in updated_car.dict().items():
        setattr(car, key, value)
    db.commit()
    db.refresh(car)
    return car

@router.delete("/{car_id}")
def delete_car(car_id: int, db: Session = Depends(get_db), user=Depends(get_current_user)):
    # Vérifie que la voiture appartient à l'utilisateur
    car = db.query(Car).filter(Car.car_id == car_id, Car.user_id == user.user_id).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")

    # Vérifie si la voiture est liée à des événements de service
    service_events = db.query(ServiceEvent).filter(ServiceEvent.car_id == car.car_id).all()
    db.delete(car)
    db.commit()

    if service_events:
        # Si la voiture a des événements de service, supprime la voiture et les événements associés
        # La suppression en cascade va supprimer les événements de service
        return {"message": "Car and associated service events deleted"}
    else:
        # Si la voiture n'a pas d'événements de service, supprime simplement la voiture
        return {"message": "Car deleted"}

