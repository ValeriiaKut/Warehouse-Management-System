from pydantic import BaseModel, Field
from typing import Optional


#dane do tworzenia produktu
class ProductCreate(BaseModel):
    name: str
    sku: str
    quantity: int = Field(ge=0)
    price: float = Field(gt=0)
    description: Optional[str] = None



#dane do aktualizacji produktu
class ProductUpdate(BaseModel):
    name: Optional[str] = None
    sku: Optional[str] = None
    quantity: Optional[int] = Field(default=None, ge=0)
    price: Optional[float] = Field(default=None, gt=0)
    description: Optional[str] = None




# dane zwracane do klienta
class ProductResponse(BaseModel):
    id: int
    name: str
    sku: str
    quantity: int
    price: float
    description: Optional[str]

    class Config:
        from_attributes = True  #ważne dla SQLAlchemy