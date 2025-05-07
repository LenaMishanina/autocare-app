from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from fastapi.security import OAuth2PasswordRequestForm

from database import get_db
from AutoCare.models.models import User
from AutoCare.schemas.schemas import UserCreate, TokenResponse
from AutoCare.auth.utils import hash_password, verify_password, create_access_token

router = APIRouter(prefix="/auth", tags=["auth"])


@router.post("/register", response_model=TokenResponse)
def register(user_data: UserCreate, db: Session = Depends(get_db)):
    # Vérifie si l'email existe déjà
    if db.query(User).filter(User.email == user_data.email).first():
        raise HTTPException(status_code=400, detail="Email already registered")

    # Crée un nouvel utilisateur avec les champs corrects
    new_user = User(
        email=user_data.email,
        full_name=user_data.full_name,  # Utilise le champ login au lieu de full_name
        password_hash=hash_password(user_data.password)
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    # Génère le token
    token = create_access_token({"sub": str(new_user.user_id)})  # Utilise user_id au lieu de id
    return {"access_token": token, "token_type": "bearer"}


@router.post("/login", response_model=TokenResponse)
def login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    # Correction du typo dans OAuth2PasswordRequestForm
    user = db.query(User).filter(User.email == form_data.username).first()

    if not user or not verify_password(form_data.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")

    #token = create_access_token({"sub": str(user.user_id)})  # Utilise user_id au lieu de id
    # APRÈS (correct)
    token = create_access_token(str(user.user_id))  # Ou assurez-vous que le payload est {"sub": "2"} directement
    return {"access_token": token, "token_type": "bearer"}



