from datetime import date
from typing import Optional

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from AutoCare.models.models import User, ServiceEvent, ServiceHistory, Car
from AutoCare.schemas.schemas import ServiceEventCreate, ServiceHistoryCreate, ServiceEventResponse,UpdateEventSchema, ServiceHistoryResponse
from database import get_db
from dependencies import get_current_user




router = APIRouter(prefix="/services", tags=["services"])

# POST - Create a new service event
@router.post("/", response_model=ServiceEventResponse)
def add_service_event(data: ServiceEventCreate, db: Session = Depends(get_db), user=Depends(get_current_user)):
    car = db.query(Car).filter(Car.car_id == data.car_id, Car.user_id == user.user_id).first()
    if not car:
        raise HTTPException(status_code=404, detail="Car not found")

    # Assurer que 'is_completed' a une valeur par défaut (False) si non fournie
    service_event_data = data.dict()
    service_event_data["is_completed"] = service_event_data.get("is_completed", False)

    service_event = ServiceEvent(**service_event_data)
    db.add(service_event)
    db.commit()
    db.refresh(service_event)
    return service_event

# GET http://192.168.1.84:8080/services/?due_date=2025-05-30&is_completed=false
# Content-Type: application/json
# email: dorzeaizzy@gmail.com
@router.get("/", response_model=list[ServiceEventResponse])
def get_service_events(
        due_date: Optional[date] = None,
        is_completed: Optional[bool] = None,
        db: Session = Depends(get_db),
        current_user: User = Depends(get_current_user)
):
    query = db.query(ServiceEvent).join(Car).filter(Car.user_id == current_user.user_id)

    if due_date is not None:
        print(f"Applying filter for due_date: {due_date}")
        query = query.filter(ServiceEvent.due_date == due_date)

    if is_completed is not None:
        print(f"Applying filter for is_completed: {is_completed}")
        query = query.filter(ServiceEvent.is_completed == is_completed)

    return query.all()



# GET - Retrieve all service events for the current user
@router.get("/", response_model=list[ServiceEventResponse])
def get_service_events(db: Session = Depends(get_db), user=Depends(get_current_user)):
    return db.query(ServiceEvent).join(Car).filter(Car.user_id == user.user_id).all()

# DELETE - Delete a service event by ID
@router.delete("/{service_id}")
def delete_service_event(service_id: int, db: Session = Depends(get_db), user=Depends(get_current_user)):
    event = db.query(ServiceEvent).join(Car).filter(ServiceEvent.event_id == service_id,
                                                    Car.user_id == user.user_id).first()
    if not event:
        raise HTTPException(status_code=404, detail="Service event not found")

    # Supprimer l'événement de service, même s'il n'est pas complet ou associé
    db.delete(event)
    db.commit()
    return {"message": "Service event deleted"}



@router.put("/{service_id}", response_model=ServiceEventResponse)
def update_service_event(service_id: int, data: UpdateEventSchema, db: Session = Depends(get_db), user=Depends(get_current_user)):
    # Rechercher l'événement de service
    event = db.query(ServiceEvent).join(Car).filter(ServiceEvent.event_id == service_id, Car.user_id == user.user_id).first()
    if not event:
        raise HTTPException(status_code=404, detail="Service event not found")

    was_completed = event.is_completed

    # Appliquer les modifications
    for key, value in data.dict(exclude_unset=True).items():
        setattr(event, key, value)

    db.commit()

    # Si le service est marqué comme terminé, ajouter un historique
    if data.is_completed and not was_completed:
        print("Service marked as completed, adding history...")  # Debugging print
        history = ServiceHistory(
            car_id=event.car_id,
            service_type=event.service_type,
            service_date=date.today(),
            mileage=event.due_mileage,
            created_at=datetime.utcnow(),
            service_event_id=event.event_id  # Lier à l'événement de service
        )
        db.add(history)
        db.commit()
        print("History added successfully")  # Debugging print

    db.refresh(event)
    return event




@router.get("/history", response_model=list[ServiceHistoryResponse])
def get_service_history(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    # Utiliser current_user.user_id, car l'attribut de l'utilisateur est user_id
    history = db.query(ServiceHistory).join(Car).filter(Car.user_id == current_user.user_id).all()

    if not history:
        raise HTTPException(status_code=404, detail="No completed service events found")

    return history


@router.delete("/history/{event_id}")
def delete_history(event_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    history = db.query(ServiceHistory).filter_by(event_id=event_id).first()
    if not history:
        raise HTTPException(status_code=404, detail="Not found")

    # Vérification que le service appartient bien à l’utilisateur
    car = db.query(Car).filter_by(car_id=history.car_id, user_id=current_user.user_id).first()
    if not car:
        raise HTTPException(status_code=403, detail="Not authorized")

    db.delete(history)
    db.commit()
    return {"message": "Service history deleted"}
