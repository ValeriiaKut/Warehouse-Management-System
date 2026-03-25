# Konfiguracja bazy danych
import os
from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import declarative_base, sessionmaker

load_dotenv() # ładujemy .env

DATABASE_URL = os.getenv("DATABASE_URL")

engine = create_engine(DATABASE_URL) # tworzymy silnik bazy
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine) #sesja

Base = declarative_base() # baza dla modeli

#funkcja do pobrania sesji bazy
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()