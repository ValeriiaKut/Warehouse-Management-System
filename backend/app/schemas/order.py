from datetime import datetime
from typing import List

from pydantic import BaseModel, Field


# produkt w zamówieniu
class OrderItemCreate(BaseModel):
    product_id: int
    quantity: int = Field(gt=0)


# dane do tworzenia zamówienia
class OrderCreate(BaseModel):
    items: List[OrderItemCreate]


# odpowiedź dla produktu w zamówieniu
class OrderItemResponse(BaseModel):
    id: int
    product_id: int
    quantity: int

    class Config:
        from_attributes = True


# odpowiedź dla zamówienia
class OrderResponse(BaseModel):
    id: int
    user_id: int
    status: str
    created_at: datetime
    items: List[OrderItemResponse]

    class Config:
        from_attributes = True