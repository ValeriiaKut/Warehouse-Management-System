from sqlalchemy import Column, Integer, String, Float
from app.db.database import Base



# model produktu w db
class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True)

    name = Column(String, nullable=False)  # nazwa produktu

    sku = Column(String, unique=True, index=True) # unikalny kod produktu 

    quantity = Column(Integer, default=0)  # ilość 

    price = Column(Float, nullable=False) # cena 

    description = Column(String, nullable=True) # opis produktu