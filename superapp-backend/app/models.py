import uuid
from datetime import datetime
from sqlalchemy import String, Float, Boolean, DateTime, ForeignKey, Text, Integer
from sqlalchemy.orm import Mapped, mapped_column, relationship
from app.database import Base

def new_id() -> str:
    return str(uuid.uuid4())

def now() -> datetime:
    return datetime.utcnow()


class User(Base):
    __tablename__ = "users"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    email: Mapped[str] = mapped_column(String, unique=True, index=True)
    password_hash: Mapped[str] = mapped_column(String)
    full_name: Mapped[str | None] = mapped_column(String, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=now)

    lists: Mapped[list["ShoppingList"]] = relationship(back_populates="user", cascade="all, delete-orphan")
    favorites: Mapped[list["Favorite"]] = relationship(back_populates="user", cascade="all, delete-orphan")
    alerts: Mapped[list["Alert"]] = relationship(back_populates="user", cascade="all, delete-orphan")
    quotes: Mapped[list["Quote"]] = relationship(back_populates="user", cascade="all, delete-orphan")


class Chain(Base):
    __tablename__ = "chains"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    slug: Mapped[str] = mapped_column(String, unique=True, index=True)
    name: Mapped[str] = mapped_column(String)
    color: Mapped[str | None] = mapped_column(String, nullable=True)
    logo_url: Mapped[str | None] = mapped_column(String, nullable=True)

    stores: Mapped[list["Store"]] = relationship(back_populates="chain")
    prices: Mapped[list["Price"]] = relationship(back_populates="chain")


class Store(Base):
    __tablename__ = "stores"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    chain_id: Mapped[str] = mapped_column(String, ForeignKey("chains.id"))
    name: Mapped[str] = mapped_column(String)
    address: Mapped[str | None] = mapped_column(String, nullable=True)
    latitude: Mapped[float] = mapped_column(Float)
    longitude: Mapped[float] = mapped_column(Float)

    chain: Mapped["Chain"] = relationship(back_populates="stores")


class Product(Base):
    __tablename__ = "products"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    name: Mapped[str] = mapped_column(String, index=True)
    brand: Mapped[str | None] = mapped_column(String, nullable=True)
    category: Mapped[str | None] = mapped_column(String, nullable=True)
    presentation: Mapped[str | None] = mapped_column(String, nullable=True)
    size: Mapped[float | None] = mapped_column(Float, nullable=True)
    unit: Mapped[str | None] = mapped_column(String, nullable=True)
    image_url: Mapped[str | None] = mapped_column(String, nullable=True)

    prices: Mapped[list["Price"]] = relationship(back_populates="product")


class Price(Base):
    __tablename__ = "prices"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    product_id: Mapped[str] = mapped_column(String, ForeignKey("products.id"), index=True)
    chain_id: Mapped[str] = mapped_column(String, ForeignKey("chains.id"), index=True)
    price: Mapped[float] = mapped_column(Float)
    updated_at: Mapped[datetime] = mapped_column(DateTime, default=now)

    product: Mapped["Product"] = relationship(back_populates="prices")
    chain: Mapped["Chain"] = relationship(back_populates="prices")


class ShoppingList(Base):
    __tablename__ = "shopping_lists"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id"))
    name: Mapped[str] = mapped_column(String)
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(DateTime, default=now)

    user: Mapped["User"] = relationship(back_populates="lists")
    items: Mapped[list["ShoppingListItem"]] = relationship(back_populates="list", cascade="all, delete-orphan")


class ShoppingListItem(Base):
    __tablename__ = "shopping_list_items"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    list_id: Mapped[str] = mapped_column(String, ForeignKey("shopping_lists.id"))
    raw_name: Mapped[str] = mapped_column(String)
    product_id: Mapped[str | None] = mapped_column(String, ForeignKey("products.id"), nullable=True)
    quantity: Mapped[float] = mapped_column(Float, default=1.0)
    unit: Mapped[str | None] = mapped_column(String, nullable=True)

    list: Mapped["ShoppingList"] = relationship(back_populates="items")


class Quote(Base):
    __tablename__ = "quotes"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id"))
    list_id: Mapped[str] = mapped_column(String, ForeignKey("shopping_lists.id"))
    strategy: Mapped[str] = mapped_column(String, default="best_balance")
    created_at: Mapped[datetime] = mapped_column(DateTime, default=now)

    user: Mapped["User"] = relationship(back_populates="quotes")
    results: Mapped[list["QuoteResult"]] = relationship(back_populates="quote", cascade="all, delete-orphan")


class QuoteResult(Base):
    __tablename__ = "quote_results"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    quote_id: Mapped[str] = mapped_column(String, ForeignKey("quotes.id"))
    chain_id: Mapped[str] = mapped_column(String, ForeignKey("chains.id"))
    store_id: Mapped[str | None] = mapped_column(String, ForeignKey("stores.id"), nullable=True)
    total: Mapped[float] = mapped_column(Float)
    items_found: Mapped[int] = mapped_column(Integer, default=0)
    items_missing: Mapped[int] = mapped_column(Integer, default=0)
    distance_km: Mapped[float | None] = mapped_column(Float, nullable=True)
    eta_minutes: Mapped[float | None] = mapped_column(Float, nullable=True)
    score: Mapped[float] = mapped_column(Float, default=0.0)
    rank: Mapped[int] = mapped_column(Integer, default=1)
    savings_vs_max: Mapped[float | None] = mapped_column(Float, nullable=True)
    items_json: Mapped[str | None] = mapped_column(Text, nullable=True)  # JSON blob

    quote: Mapped["Quote"] = relationship(back_populates="results")
    chain: Mapped["Chain"] = relationship()
    store: Mapped["Store | None"] = relationship()


class Favorite(Base):
    __tablename__ = "favorites"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id"))
    product_id: Mapped[str] = mapped_column(String, ForeignKey("products.id"))

    user: Mapped["User"] = relationship(back_populates="favorites")


class Alert(Base):
    __tablename__ = "alerts"
    id: Mapped[str] = mapped_column(String, primary_key=True, default=new_id)
    user_id: Mapped[str] = mapped_column(String, ForeignKey("users.id"))
    product_id: Mapped[str] = mapped_column(String, ForeignKey("products.id"))
    target_price: Mapped[float] = mapped_column(Float)
    type: Mapped[str] = mapped_column(String, default="price_drop")
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)

    user: Mapped["User"] = relationship(back_populates="alerts")
