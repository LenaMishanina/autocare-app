from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session
from jose import JWTError, jwt
from typing import Optional
from fastapi import Body

# Import your database and models
from database import get_db
from AutoCare.models.models import User
from AutoCare.auth.utils import decode_access_token, SECRET_KEY, ALGORITHM

# Define the OAuth2 scheme
# oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")

# app/dependencies.py

# dependencies.py
from fastapi import Depends, HTTPException, Header
from sqlalchemy.orm import Session
from pydantic import EmailStr
from database import get_db
from AutoCare.models.models import User

def get_current_user(email: EmailStr = Header(...), db: Session = Depends(get_db)) -> User:
    user = db.query(User).filter(User.email == email).first()
    if not user:
        raise HTTPException(status_code=401, detail="Invalid email")
    return user

