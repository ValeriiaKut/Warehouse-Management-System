from fastapi import APIRouter, Depends, HTTPException, Request
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from sqlalchemy.orm import Session
from slowapi import Limiter
from slowapi.util import get_remote_address

import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("auth_service")

from app.security import (
    create_access_token,
    decode_access_token,
    hash_password,
    verify_password,
)
from app.database import get_db
from app.models import User
from app.schemas import TokenResponse, UserCreate, UserLogin, UserResponse

router = APIRouter(prefix="/auth", tags=["auth"]) # tworzymy router dla auth
security = HTTPBearer() #Bearer token
limiter = Limiter(key_func=get_remote_address)

def get_current_user( # funkcja do pobrania aktualnego użytkownika z tokena
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db),
):
    token = credentials.credentials #pobieramy token z nagłówka
    payload = decode_access_token(token) # dekodujemy token JWT
 #sprawdzamy czy token jest poprawny
    if not payload:
        raise HTTPException(status_code=401, detail="Invalid or expired token")

    user_id = payload.get("user_id")  # pobieramy id użytkownika z tokena
    if not user_id:
        raise HTTPException(status_code=401, detail="Invalid token payload")

    user = db.query(User).filter(User.id == user_id).first() #szukamy użytkownika w bazie
    if not user:
        raise HTTPException(status_code=401, detail="User not found")

    return user

# -----------------------------------------------endpoint do rejestracji użytkownika----------------------------------------------------------------
@router.post("/register", response_model=UserResponse)
def register(user: UserCreate, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter( #sprawdzamy czy użytkownik już istnieje
        (User.username == user.username) | (User.email == user.email)
    ).first()

    if existing_user:
        logger.warning(f"Registration failed, user already exists: {user.email}")
        raise HTTPException(status_code=400, detail="Username or email already exists")

    new_user = User( #tworzymy nowego użytkownika
        username=user.username,
        email=user.email,
        hashed_password=hash_password(user.password),
        role=user.role,
    )
# zapis do db
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    logger.info(f"New user registered: {new_user.email}, role={new_user.role}")

    return new_user

#---------------------------------------------------------endpoint do logowania---------------------------------------------------------------------------
@router.post("/login", response_model=TokenResponse)
@limiter.limit("5/minute")
def login(
    request: Request,
    user: UserLogin,
    db: Session = Depends(get_db)
):
    # szukamy użytkownika po emailu
    db_user = db.query(User).filter(User.email == user.email).first()

    if not db_user:
        logger.warning(f"Failed login attempt for email: {user.email}")
        raise HTTPException(
            status_code=401,
            detail="Invalid email or password"
        )

    # sprawdzamy hasło
    if not verify_password(user.password, db_user.hashed_password):
        raise HTTPException(
            status_code=401,
            detail="Invalid email or password"
        )

    access_token = create_access_token(
        data={
            "sub": db_user.email,
            "user_id": db_user.id,
            "role": db_user.role,
        }
    )

    logger.info(f"User logged in: {db_user.email}, role={db_user.role}")

    return {
        "access_token": access_token,
        "token_type": "bearer",
    }
#-----------------------------------------------------endpoint chroniony/zwraca dane aktualnego użytkownika-----------------------------------------------------
@router.get("/me")
def read_current_user(current_user: User = Depends(get_current_user)):
    return {
        "id": current_user.id,
        "username": current_user.username,
        "email": current_user.email,
        "role": current_user.role,
    }
# funkcja sprawdzająca czy użytkownik jest adminem
def get_current_admin(current_user: User = Depends(get_current_user)):
    if current_user.role != "admin":
        raise HTTPException(
            status_code=403,
            detail="Admin access required",
        )

    return current_user