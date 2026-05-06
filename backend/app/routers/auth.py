from fastapi import APIRouter, Depends, HTTPException
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from sqlalchemy.orm import Session

from app.core.security import (
    create_access_token,
    decode_access_token,
    hash_password,
    verify_password,
)
from app.db.database import get_db
from app.models.user import User
from app.schemas.user import TokenResponse, UserCreate, UserLogin, UserResponse

router = APIRouter(prefix="/auth", tags=["auth"]) # tworzymy router dla auth
security = HTTPBearer() #Bearer token


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
        raise HTTPException(status_code=400, detail="Username or email already exists")

    new_user = User( #tworzymy nowego użytkownika
        username=user.username,
        email=user.email,
        hashed_password=hash_password(user.password),
    )
# zapis do db
    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return new_user

#---------------------------------------------------------endpoint do logowania---------------------------------------------------------------------------
@router.post("/login", response_model=TokenResponse)
def login(user: UserLogin, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.email == user.email).first() #szukamy użytkownika po emailu

    if not db_user:
        raise HTTPException(status_code=401, detail="Invalid email or password")

    if not verify_password(user.password, db_user.hashed_password):  #sprawdzamy hasło
        raise HTTPException(status_code=401, detail="Invalid email or password")

    access_token = create_access_token(  #tworzymy token JWT
        data={"sub": db_user.email, "user_id": db_user.id}
    )

    return {
        "access_token": access_token,
        "token_type": "bearer",
    }

#-----------------------------------------------------endpoint chroniony – zwraca dane aktualnego użytkownika-----------------------------------------------------
@router.get("/me")
def read_current_user(current_user: User = Depends(get_current_user)):
    return {
        "id": current_user.id,
        "username": current_user.username,
        "email": current_user.email,
    }