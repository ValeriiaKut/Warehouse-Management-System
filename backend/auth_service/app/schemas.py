#Schematy do walidacji danych
from pydantic import BaseModel, EmailStr, field_validator


class UserCreate(BaseModel): # dane do rejestracji
    username: str
    email: EmailStr
    password: str
    role: str = "worker"
    @field_validator("password")
    @classmethod
    def validate_password(cls, value):
        if len(value) < 8:
            raise ValueError("Password must have at least 8 characters")

        if not any(char.isdigit() for char in value):
            raise ValueError("Password must contain at least one number")

        return value

class UserLogin(BaseModel): # dane do logowania
    email: EmailStr
    password: str


class UserResponse(BaseModel): # odpowiedź z API
    id: int
    username: str
    email: EmailStr
    role: str
    
    class Config:
        from_attributes = True


class TokenResponse(BaseModel): # token JWT
    access_token: str
    token_type: str