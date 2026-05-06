from fastapi import FastAPI
from sqlalchemy import inspect, text

from app.db.database import Base, engine
from app.models.product import Product
from app.models.user import User
from app.routers import auth, products

app = FastAPI() #tworzymy aplikację FastAPI

# przy starcie aplikacji tworzymy tabele w bazie
@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)

#-----------------------------------------------------------test endpoint-------------------------------------------------------------
@app.get("/")
def root():
    return {"message": "Backend is running"}

# sprawdzamy jakie są tabele w bazie
@app.get("/db-check")
def db_check():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))
    return {"message": "Database connection is OK"}


@app.get("/tables") #podłączamy routery
def get_tables():
    inspector = inspect(engine)
    return {"tables": inspector.get_table_names()}


app.include_router(auth.router)
app.include_router(products.router)