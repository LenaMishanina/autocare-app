from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from sqlalchemy.exc import SQLAlchemyError

# from config import DATABASE_NAME

import config
import contextlib

import os

SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL")
import os

SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL") or \
    f"postgresql://{config.DATABASE_USERNAME}:{config.DATABASE_PASSWORD}@{config.DATABASE_HOST}/{config.DATABASE_NAME}"


DATABASE_USERNAME = config.DATABASE_USERNAME
DATABASE_PASSWORD = config.DATABASE_PASSWORD
DATABASE_HOST = config.DATABASE_HOST
DATABASE_NAME = config.DATABASE_NAME

# URL_DATABASE = 'postgresql://lysedorzea:1234@localhost:5434/AutoCare'
# SQLALCHEMY_DATABASE_URL = f"postgresql://{DATABASE_USERNAME}:{DATABASE_PASSWORD}@{DATABASE_HOST}/{DATABASE_NAME}"
#
# SQLALCHEMY_DATABASE_URL = "postgresql://lysedorzea:1234@localhost:5434/AutoCare"

# URL pour le rГ©seau Docker (le service s'appelle "db")
SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL") or \
    "postgresql://postgres:Fa4bixe@localhost:5432/AutoCare"


# SQLALCHEMY_DATABASE_URL = "postgresql://lysedorzea:1234@AutoCare-db:5434/AutoCare"

engine = create_engine(SQLALCHEMY_DATABASE_URL)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

# engine = create_engine(
#     DATABASE_URL,
#     pool_size=20,        # Taille de base du pool
#     max_overflow=30,     # Connexions temporaires en plus
#     pool_timeout=30,     # Temps d'attente avant erreur
#     pool_recycle=1800    # Recyclage des connexions toutes les 30 minutes
# )

# SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
