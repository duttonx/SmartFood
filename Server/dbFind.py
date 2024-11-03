"""
Author: Georgy_Cherkasov
"""

import random
import sqlite3
from typing import List, Dict, Any

DATABASE_NAME = 'products.db'

def create_connection() -> sqlite3.Connection:
    """
    Create a database connection to the SQLite database.

    :return: Connection object to the SQLite database.
    :rtype: sqlite3.Connection
    """
    conn = sqlite3.connect(DATABASE_NAME)
    return conn

def create_table() -> None:
    """
    Create the products table in the SQLite database.

    The table will have the following columns:
    - id: INTEGER, primary key
    - name: TEXT, name of the product
    - price: REAL, price of the product
    - store: TEXT, name of the store

    :return: None
    """
    conn = create_connection()
    with conn:
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS Product (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                store TEXT NOT NULL
            )
        ''')
    conn.close()

def insert_product(name: str, price: float, store: str) -> None:
    """
    Insert a new product into the products table.

    :param name: Name of the product.
    :param price: Price of the product.
    :param store: Name of the store.
    :return: None
    """
    conn = create_connection()
    with conn:
        cursor = conn.cursor()
        cursor.execute('''
            INSERT INTO Product (name, price, store)
            VALUES (?, ?, ?)
        ''', (name, price, store))
    conn.close()

def get_products(store: str, product_name: str) -> List[Dict[str, Any]]:
    """
    Retrieve products from the products table by store name and product name.

    :param store: Name of the store.
    :param product_name: Name of the product.
    :return: List of dictionaries containing product names and prices.
    :rtype: List[Dict[str, Any]]
    """
    conn = create_connection()
    with conn:
        cursor = conn.cursor()
        cursor.execute('''
            SELECT name, price, store FROM Product WHERE store = ? AND name = ?
        ''', (store, product_name))
        rows = cursor.fetchall()
    conn.close()
    return [{'name': row[0], 'price': row[1], 'store': row[2]} for row in rows]

def generate_data() -> None:
    """
    Generate sample data for the products table.

    This function populates the table with sample data for testing purposes.
    It creates entries for ten stores and fifty products with random prices.

    :return: None
    """
    stores = ['Ашан', 'Азбука Вкуса', 'Дикси', 'Супер Лента', 'Магнит', 'МЕТРО', 'О’Кей', 'Перекресток', 'Пятёрочка', 'СПАР', 'Ярче!']
    products = [
        'Молоко', 'Хлеб', 'Яйца', 'Масло', 'Сыр', 'Курица', 'Говядина', 'Свинина', 'Рыба', 'Рис', 'Макароны', 'Помидоры', 
        'Огурцы', 'Картофель', 'Лук', 'Чеснок', 'Морковь', 'Яблоки', 'Бананы', 'Апельсины', 'Виноград', 'Клубника', 
        'Черника', 'Йогурт', 'Мороженое', 'Шоколад', 'Кофе', 'Чай', 'Сок', 'Вода', 'Газировка', 'Пиво', 'Вино', 
        'Водка', 'Виски', 'Джин', 'Ром', 'Шампанское', 'Мука', 'Сахар', 'Соль', 'Перец', 'Оливковое масло', 'Подсолнечное масло', 
        'Маргарин', 'Кетчуп', 'Майонез', 'Горчица'
    ]
    conn = create_connection()
    with conn:
        cursor = conn.cursor()
        for store in stores:
            for product in products:
                cursor.execute('''
                    SELECT COUNT(*) FROM Product WHERE name = ? AND store = ?
                ''', (product, store))
                count = cursor.fetchone()[0]
                if count == 0:
                    price = round(random.uniform(1.0, 100.0), 2)
                    insert_product(product, price, store)
    conn.close()