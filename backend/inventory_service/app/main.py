from fastapi import FastAPI
from sqlalchemy import inspect, text

from app.database import Base, engine
from app.models import Product
from app import products

app = FastAPI(title="Inventory Service")


@app.on_event("startup")
def on_startup():
    Base.metadata.create_all(bind=engine)


@app.get("/")
def root():
    return {"message": "Inventory Service is running"}


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


app.include_router(products.router)