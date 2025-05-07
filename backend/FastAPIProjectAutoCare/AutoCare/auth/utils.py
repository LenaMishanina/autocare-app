from passlib.context import CryptContext
from jose import jwt, JWTError
from datetime import datetime, timedelta
import secrets
from typing import Optional
from fastapi.security import OAuth2PasswordBearer

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")

# Mot de passe hash
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Configuration JWT
SECRET_KEY = "Lysy!@_super_secret_key"  # À remplacer par une vraie clé secrète en prod
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30

# === Password Hashing ===
def hash_password(password: str) -> str:
    return pwd_context.hash(password)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)


def create_access_token(user_id: int) -> str:
    # Ensure we're only putting the user ID string in 'sub'
    payload = {
        "sub": str(user_id),  # Just the user ID as string
        "exp": datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    }
    return jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)


def decode_access_token(token: str) -> Optional[dict]:
    try:
        return jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
    except JWTError:
        return None

# === Password Reset Token ===
def generate_reset_token(email: str) -> str:
    """Crée un token pseudo-aléatoire lié à une adresse email (pas encodé JWT)."""
    return secrets.token_urlsafe(32)

def verify_reset_token(token: str, stored_token: str, expires_at: datetime) -> bool:
    """Vérifie que le token est encore valide (non expiré et correspond)."""
    return token == stored_token and datetime.utcnow() < expires_at



