from fastapi import FastAPI, HTTPException, Depends
from pydantic import BaseModel
from typing import List, Annotated

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from AutoCare.routers import auth, users, cars, services, notifications
from database import Base, engine

# Crée les tables dans la base si elles n'existent pas encore
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="AutoCare Backend",
    description="Backend API pour l'application AutoCare (gestion des véhicules, services, notifications, utilisateurs, etc.)",
    version="1.0.0"
)

# Middleware CORS (important pour appli Flutter Web ou Android)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # à restreindre en prod : ["http://localhost:5173"]
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Inclusion des routers
app.include_router(auth.router)
app.include_router(users.router)
app.include_router(cars.router)
app.include_router(services.router)
app.include_router(notifications.router)

# Route racine pour test
@app.get("/")
def read_root():
    return {"message": "Welcome to the AutoCare API 🚗"}

if __name__ == "__main__":
    import uvicorn
    # locust
    uvicorn.run("main:app", host="192.168.1.84", port=8080, reload=True)
    # dass
    # uvicorn.run("main:app", host="192.168.0.73", port=8080, reload=True)
    # Lyse
    # uvicorn.run("main:app", host="YOUR_IP_ADDRESS", port=8080, reload=True)

# host=
# <cmd ->
#   ipconfig + ENTER ->
#       Адаптер беспроводной локальной сети Беспроводная сеть: IPv4-адрес>

# Если этот адрес не работает, можно попробовать другие адреса (из ipconfig)


# Vérifiez les logs complets du serveur :
# uvicorn main:app --reload --log-level debug


#  je veux utiliser jwt token pour login/forgot_pasword/ reset_password, je veux post, put, get, delete : car/ serviceevent/ notification/ servicehistory  en utlissant le login Table User