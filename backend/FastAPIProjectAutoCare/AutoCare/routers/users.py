from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from AutoCare.models.models import User
from database import get_db
from AutoCare.schemas.schemas import UserResponse
from dependencies import get_current_user

from fastapi import APIRouter, Depends, HTTPException, Header
from sqlalchemy.orm import Session
from AutoCare.models.models import User
from database import get_db
from AutoCare.schemas.schemas import UserResponse, UserRenameRequest
from dependencies import get_current_user

router = APIRouter(prefix="/users", tags=["Users"])

@router.get("/{user_id}", response_model=UserResponse)
def get_user_by_id(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.user_id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user


@router.delete("/{user_id}")
def delete_user_by_id(user_id: int, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.user_id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    db.delete(user)
    db.commit()
    return {"message": f"User with ID {user_id} deleted"}




@router.put("/{user_id}/rename", response_model=UserResponse)
def rename_user(user_id: int, data: UserRenameRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.user_id == user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.full_name = data.full_name
    db.commit()
    db.refresh(user)
    return user





