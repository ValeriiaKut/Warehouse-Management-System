from fastapi import FastAPI
from sqlalchemy import inspect, text

from app.db.database import Base, engine
from app.models.order import Order, OrderItem
from app.models.product import Product
from app.models.user import User
from app.routers import auth, products
from app.routers.orders import router as orders_router

app = FastAPI()


# tworzymy tabele przy starcie
@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)


# test endpoint
@app.get("/")
def root():
    return {"message": "Backend is running"}


# test połączenia z bazą
@app.get("/db-check")
def db_check():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))

    return {"message": "Database connection is OK"}


# lista tabel
@app.get("/tables")
def get_tables():
    inspector = inspect(engine)

    return {
        "tables": inspector.get_table_names()
    }


# routery
app.include_router(auth.router)
app.include_router(products.router)
app.include_router(orders_router)