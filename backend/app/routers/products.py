from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.db.database import get_db
from app.models.product import Product
from app.models.user import User
from app.routers.auth import get_current_user
from app.schemas.product import ProductCreate, ProductResponse, ProductUpdate

router = APIRouter(prefix="/products", tags=["products"]) # router dla produktów

#----------------------------------------------------endpoint do dodawania produktu-------------------------------------------------------------
@router.post("", response_model=ProductResponse)
def create_product(
    product: ProductCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    existing_product = db.query(Product).filter(Product.sku == product.sku).first() # sprawdzamy czy SKU już istnieje

    if existing_product:
        raise HTTPException(status_code=400, detail="Product with this SKU already exists")

    new_product = Product( # tworzymy nowy produkt
        name=product.name,
        sku=product.sku,
        quantity=product.quantity,
        price=product.price,
        description=product.description,
    )


#zapisujemy produkt w bazie
    db.add(new_product) 
    db.commit()
    db.refresh(new_product)

    return new_product

#-------------------------------------------------------endpoint do pobierania wszystkich produktów----------------------------------------------------------
@router.get("", response_model=list[ProductResponse])
def get_products(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    return db.query(Product).all() #pobieramy wszystkie produkty z bazy

#--------------------------------------------------endpoint do pobierania produktu po id--------------------------------------------------------------------
@router.get("/{product_id}", response_model=ProductResponse)
def get_product_by_id(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first()
 # szukamy produktu
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    return product

#-------------------------------------------------endpoint do aktualizacji produktu--------------------------------------------------------------------
@router.put("/{product_id}", response_model=ProductResponse)
def update_product(
    product_id: int,
    product_data: ProductUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first() # szukamy produktu

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    if product_data.sku and product_data.sku != product.sku: # sprawdzamy czy nowe SKU nie jest zajęte
        existing_product = db.query(Product).filter(Product.sku == product_data.sku).first()
        if existing_product:
            raise HTTPException(status_code=400, detail="Product with this SKU already exists")

#Aktualizujemy pola jeśli zostały podane
    if product_data.name is not None:
        product.name = product_data.name
    if product_data.sku is not None:
        product.sku = product_data.sku
    if product_data.quantity is not None:
        product.quantity = product_data.quantity
    if product_data.price is not None:
        product.price = product_data.price
    if product_data.description is not None:
        product.description = product_data.description

    db.commit()
    db.refresh(product)

    return product

#--------------------------------------------------------------endpoint do usuwania produktu----------------------------------------------------------
@router.delete("/{product_id}")
def delete_product(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first() # szukamy produktu

    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    db.delete(product)  # usuwamy produkt
    db.commit()

    return {"message": "Product deleted successfully"}