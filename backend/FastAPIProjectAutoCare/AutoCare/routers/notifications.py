from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy.orm import Session
from AutoCare.models.models import Notification, Car, ServiceEvent
from database import get_db
from AutoCare.schemas.schemas import NotificationCreate, NotificationResponse
from dependencies import get_current_user

router = APIRouter(prefix="/notifications", tags=["notifications"])

@router.post("/", response_model=NotificationResponse)
def create_notification(
        data: NotificationCreate,
        db: Session = Depends(get_db),
        user=Depends(get_current_user)
):
    # Verify the service event exists AND belongs to the user
    service_event = (
        db.query(ServiceEvent)
        .join(Car)
        .filter(
            ServiceEvent.event_id == data.event_id,
            Car.user_id == user.user_id
        )
        .first()
    )

    if not service_event:
        raise HTTPException(
            status_code=404,
            detail="Service event not found or you don't have permission"
        )

    # Create the notification
    notification = Notification(
        event_id=data.event_id,
        # Include other fields from data as needed
        **data.dict(exclude={"event_id"})  # In case event_id is already in the dict
    )

    db.add(notification)
    db.commit()
    db.refresh(notification)
    return notification

# Récupérer les notifications
# @router.get("/", response_model=list[NotificationResponse])
# def get_notifications(db: Session = Depends(get_db), user=Depends(get_current_user)):
#     return db.query(Notification).join(ServiceEvent).join(Car).filter(Car.user_id == user.user_id).all()


@router.get("/", response_model=list[NotificationResponse])
def get_notifications(db: Session = Depends(get_db), user=Depends(get_current_user)):
    return (
        db.query(Notification)
        .join(ServiceEvent, Notification.event_id == ServiceEvent.event_id)  # Corrected join
        .join(Car, ServiceEvent.car_id == Car.car_id)
        .filter(Car.user_id == user.user_id)
        .all()
    )


# Supprimer une notification
@router.delete("/{notification_id}")
def delete_notification(
        notification_id: int,
        db: Session = Depends(get_db),
        user=Depends(get_current_user)
):
    # Verify notification exists AND belongs to the user
    notification = (
        db.query(Notification)
        .join(ServiceEvent)
        .join(Car)
        .filter(
            Notification.notification_id == notification_id,
            Car.user_id == user.user_id
        )
        .first()
    )

    if not notification:
        raise HTTPException(
            status_code=404,
            detail="Notification not found or you don't have permission"
        )

    db.delete(notification)
    db.commit()
    return {"message": "Notification deleted successfully"}

# Mettre à jour une notification
@router.put("/{notification_id}", response_model=NotificationResponse)
def update_notification(
        notification_id: int,
        data: NotificationCreate,  # Should be a Pydantic model for updates
        db: Session = Depends(get_db),
        user=Depends(get_current_user)
):
    # Verify notification exists AND belongs to the user
    notification = (
        db.query(Notification)
        .join(ServiceEvent)
        .join(Car)
        .filter(
            Notification.notification_id == notification_id,
            Car.user_id == user.user_id
        )
        .first()
    )

    if not notification:
        raise HTTPException(
            status_code=404,
            detail="Notification not found or you don't have permission"
        )

    # Update only the provided fields
    update_data = data.dict(exclude_unset=True)
    for field, value in update_data.items():
        setattr(notification, field, value)

    db.commit()
    db.refresh(notification)
    return notification

