from fastapi import Depends, FastAPI, HTTPException # tworzymy aplikację FastAPI
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer #potrzebne do obsługi tokena
from sqlalchemy import inspect, text #sqlalchemy do pracy z bazą
from sqlalchemy.orm import Session

from app.core.security import ( # import funkcje security
    create_access_token,
    decode_access_token,
    hash_password,
    verify_password,
)
from app.db.database import Base, engine, get_db #podłączamy bd
from app.models.user import User
from app.schemas.user import TokenResponse, UserCreate, UserLogin, UserResponse
# tworzymy apk
app = FastAPI()
security = HTTPBearer() # mechanizm autoryzacji Bearer


@app.on_event("startup") #przy starcie aplikacji tworzymy tabele w bazie
def on_startup():
    Base.metadata.create_all(bind=engine)


def get_current_user( #funkcja do pobrania aktualnego użytkownika z tokena
    credentials: HTTPAuthorizationCredentials = Depends(security),
    db: Session = Depends(get_db),
):
    token = credentials.credentials # pobieramy
    payload = decode_access_token(token)  # dekodujemy 

    if not payload: #sprawdzamy czy token jest poprawny
        raise HTTPException(status_code=401, detail="Invalid or expired token")

    user_id = payload.get("user_id") #pobieramy id user z tokena
    if not user_id:
        raise HTTPException(status_code=401, detail="Invalid token payload")

    user = db.query(User).filter(User.id == user_id).first() # szukamy user w db
    if not user:
        raise HTTPException(status_code=401, detail="User not found")

    return user


@app.get("/") #  test endpoint
def root():
    return {"message": "Backend is running"}


@app.get("/db-check") #check db
def db_check():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))
    return {"message": "Database connection is OK"}


@app.get("/tables") #check tables
def get_tables():
    inspector = inspect(engine)
    return {"tables": inspector.get_table_names()}

#endpoint do rejestracji użytkownika
@app.post("/auth/register", response_model=UserResponse)
def register(user: UserCreate, db: Session = Depends(get_db)):
    existing_user = db.query(User).filter( #sprawdzamy czy użytkownik już istnieje
        (User.username == user.username) | (User.email == user.email)
    ).first()

    if existing_user:
        raise HTTPException(status_code=400, detail="Username or email already exists")

    new_user = User( # tworzymy new user,haslo,email
        username=user.username,
        email=user.email,
        hashed_password=hash_password(user.password),
    )

    db.add(new_user) #zapis info do db
    db.commit()
    db.refresh(new_user)

    return new_user

# endpoint log
@app.post("/auth/login", response_model=TokenResponse)
def login(user: UserLogin, db: Session = Depends(get_db)): # szukamy użytkownika po emailu
    db_user = db.query(User).filter(User.email == user.email).first()

    if not db_user:
        raise HTTPException(status_code=401, detail="Invalid email or password")

    if not verify_password(user.password, db_user.hashed_password): # check hasło
        raise HTTPException(status_code=401, detail="Invalid email or password")

    access_token = create_access_token( # tworzymy token JWT
        data={"sub": db_user.email, "user_id": db_user.id}
    )

    return {
        "access_token": access_token,
        "token_type": "bearer",
    }

# endpoint chroniony, dostęp tylko z token!!!!!
@app.get("/auth/me")
def read_current_user(current_user: User = Depends(get_current_user)):
    return {
        "id": current_user.id,
        "username": current_user.username,
        "email": current_user.email,
    }