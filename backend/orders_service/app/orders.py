from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Order, OrderItem
from app.product_model import Product
from app.security import get_current_admin, get_current_user
from app.schemas import OrderCreate, OrderResponse

router = APIRouter(prefix="/orders", tags=["orders"])


# tworzenie zamówienia
@router.post("", response_model=OrderResponse)
def create_order(
    order_data: OrderCreate,
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_user),
):
    if not order_data.items:
        raise HTTPException(
            status_code=400,
            detail="Order must contain at least one item",
        )

    # tworzymy zamówienie
    new_order = Order(
        user_id=current_user["user_id"],
        status="PENDING",
    )

    db.add(new_order)
    db.flush()

    # dodajemy produkty
    for item in order_data.items:

        if item.quantity <= 0:
            raise HTTPException(
                status_code=400,
                detail="Quantity must be greater than 0",
            )

        product = db.query(Product).filter(
            Product.id == item.product_id
        ).first()

        if not product:
            raise HTTPException(
                status_code=404,
                detail=f"Product {item.product_id} not found",
            )

        # sprawdzamy stock
        if product.quantity < item.quantity:
            raise HTTPException(
                status_code=400,
                detail=f"Not enough stock for product {product.name}",
            )

        # zmniejszamy ilość produktu
        product.quantity -= item.quantity

        order_item = OrderItem(
            order_id=new_order.id,
            product_id=item.product_id,
            quantity=item.quantity,
        )

        db.add(order_item)

    db.commit()
    db.refresh(new_order)

    return new_order


# lista zamówień
@router.get("", response_model=list[OrderResponse])
def get_orders(
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_user),
):
    return db.query(Order).all()


# pobranie zamówienia po id
@router.get("/{order_id}", response_model=OrderResponse)
def get_order_by_id(
    order_id: int,
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_user),
):
    order = db.query(Order).filter(Order.id == order_id).first()

    if not order:
        raise HTTPException(
            status_code=404,
            detail="Order not found",
        )

    return order


# zmiana statusu zamówienia
@router.put("/{order_id}/status", response_model=OrderResponse)
def update_order_status(
    order_id: int,
    status: str,
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_admin),
):
    allowed_statuses = [
        "PENDING",
        "PACKING",
        "SHIPPED",
        "DELIVERED",
        "CANCELLED",
    ]

    if status not in allowed_statuses:
        raise HTTPException(
            status_code=400,
            detail="Invalid order status",
        )

    order = db.query(Order).filter(Order.id == order_id).first()

    if not order:
        raise HTTPException(
            status_code=404,
            detail="Order not found",
        )

    order.status = status

    db.commit()
    db.refresh(order)

    return order


# usuwanie zamówienia
@router.delete("/{order_id}")
def delete_order(
    order_id: int,
    db: Session = Depends(get_db),
    current_user: dict = Depends(get_current_admin),
):
    order = db.query(Order).filter(Order.id == order_id).first()

    if not order:
        raise HTTPException(
            status_code=404,
            detail="Order not found",
        )

    db.delete(order)
    db.commit()

    return {"message": "Order deleted successfully"}