#FROM ubuntu:latest
#LABEL authors="lysedorzea"
#
#ENTRYPOINT ["top", "-b"]



# Dockerfile
FROM python:3.11-slim

# Installer les dépendances système
RUN apt-get update && apt-get install -y build-essential libpq-dev

# Créer le répertoire de travail
WORKDIR /app

# Copier les fichiers
#COPY ./app /app/app
#COPY main.py /app

RUN pip install passlib && \
	pip install email-validator

COPY requirements.txt /app
RUN pip3 install -r requirements.txt



# Installer les dépendances Python
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt && \
	pip install uvicorn

COPY . .
# Lancer l'application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8080"]

