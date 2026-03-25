from sqlalchemy import Column, Integer, String #model użytkownika
from app.db.database import Base

class User(Base):
    __tablename__ = "users" #nazwa tabeli
# kolumny w bazie
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=False)