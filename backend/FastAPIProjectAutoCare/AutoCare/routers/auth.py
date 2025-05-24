from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from AutoCare.models.models import User, ResetToken
from database import get_db
from AutoCare.auth.utils import (
    verify_password,
    hash_password,
    create_access_token,
    generate_reset_token,
    verify_reset_token
)
from AutoCare.schemas.schemas import (
    LoginRequest,
    TokenResponse,
    RegisterRequest,
    ForgotPasswordRequest,
    ResetPasswordRequest
)
from datetime import datetime, timedelta

router = APIRouter(prefix="/auth", tags=["Auth"])


@router.post("/login", response_model=TokenResponse)
def login(data: LoginRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == data.email).first()
    if not user or not verify_password(data.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    # token = create_access_token(user_id)
    # Use user.user_id instead of undefined user_id variable
    token = create_access_token({"sub": str(user.user_id)})
    return {"access_token": token, "token_type": "bearer"}


@router.post("/register", response_model=TokenResponse)
def register(data: RegisterRequest, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter(User.email == data.email).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    user = User(
        email=data.email,
        full_name=data.full_name,  # Utilise le champ login au lieu de full_name
        password_hash=hash_password(data.password)
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    token = create_access_token(user.user_id)
    return {"access_token": token, "token_type": "bearer"}


@router.post("/forgot-password")
def forgot_password(data: ForgotPasswordRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == data.email).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    token = generate_reset_token(user.email)
    print(token)

    reset_token = ResetToken(
        user_id=user.user_id,
        token=token,
        expires_at=datetime.utcnow() + timedelta(hours=1)
    )
    db.add(reset_token)
    db.commit()

    # TODO: envoyer email ici
    print(f"[DEBUG] Reset token for {user.email}: {token}")
    return {"message": "Password reset token has been sent.","reset_token": token}


@router.post("/reset-password")
def reset_password(data: ResetPasswordRequest, db: Session = Depends(get_db)):
    reset_token = db.query(ResetToken).filter(ResetToken.token == data.token).first()
    if not reset_token or reset_token.expires_at < datetime.utcnow():
        raise HTTPException(status_code=400, detail="Invalid or expired token")

    user = db.query(User).filter(User.user_id == reset_token.user_id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.password_hash = hash_password(data.new_password)
    db.delete(reset_token)  # Supprimer le token après usage
    db.commit()

    return {"message": "Password has been reset successfully"}
