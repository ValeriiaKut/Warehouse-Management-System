#Schematy do walidacji danych
from pydantic import BaseModel, EmailStr


class UserCreate(BaseModel): # dane do rejestracji
    username: str
    email: EmailStr
    password: str


class UserLogin(BaseModel): # dane do logowania
    email: EmailStr
    password: str


class UserResponse(BaseModel): # odpowiedź z API
    id: int
    username: str
    email: EmailStr

    class Config:
        from_attributes = True


class TokenResponse(BaseModel): # token JWT
    access_token: str
    token_type: str