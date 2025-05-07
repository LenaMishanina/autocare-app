import os

# Configuration JWT
SECRET_KEY = "Lysy!@_super_secret_key"  # À remplacer par une vraie clé secrète en prod
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30


APP_WNV = os.getenv('APP_WNV', 'development')
DATABASE_USERNAME = os.getenv('DATABASE_USERNAME', 'lysedorzea')
DATABASE_PASSWORD = os.getenv('DATABASE_PASSWORD', '1234')
DATABASE_HOST = os.getenv('DATABASE_HOST', 'localhost')
DATABASE_NAME = os.getenv('DATABASE_NAME', 'AutoCare')