from fastapi import FastAPI
from sqlalchemy import inspect, text

from app.database import Base, engine
from app.models import User
from app import auth
from slowapi import Limiter
from slowapi.middleware import SlowAPIMiddleware
from slowapi.util import get_remote_address

app = FastAPI(title="Auth Service")

limiter = Limiter(key_func=get_remote_address)
app.state.limiter = limiter
app.add_middleware(SlowAPIMiddleware)


@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)


@app.get("/")
def root():
    return {"message": "Auth Service is running"}


@app.get("/db-check")
def db_check():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))

    return {"message": "Database connection is OK"}


@app.get("/tables")
def get_tables():
    inspector = inspect(engine)
    return {"tables": inspector.get_table_names()}


app.include_router(auth.router)