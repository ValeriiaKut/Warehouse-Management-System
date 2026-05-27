from fastapi import FastAPI
from sqlalchemy import inspect, text

from app.database import Base, engine
from app.models import Order, OrderItem
from app.product_model import Product
from app.user_model import User
from app import orders

app = FastAPI(title="Orders Service")


@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)


@app.get("/")
def root():
    return {"message": "Orders Service is running"}


@app.get("/db-check")
def db_check():
    with engine.connect() as connection:
        connection.execute(text("SELECT 1"))

    return {"message": "Database connection is OK"}


@app.get("/tables")
def get_tables():
    inspector = inspect(engine)

    return {
        "tables": inspector.get_table_names()
    }


app.include_router(orders.router)