import sqlite3
from typing import List, Dict, Any

author = "Georgy_Cherkasov"


def create_connection() -> sqlite3.Connection:
    """
    Create a database connection to the SQLite database specified by db_file.

    :return: Connection object or None.
    :rtype: sqlite3.Connection
    """
    conn = None
    try:
        conn = sqlite3.connect('transactions.db')
        return conn
    except sqlite3.Error as e:
        print(e)
    return conn


def create_table(conn: sqlite3.Connection) -> None:
    """
    Create the transactions table if it doesn't exist.

    :param conn: The Connection object.
    :type conn: sqlite3.Connection
    :return: None
    """
    create_table_sql = '''
    CREATE TABLE IF NOT EXISTS transactions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id TEXT NOT NULL,
        expense REAL NOT NULL,
        date TEXT NOT NULL,
        category TEXT NOT NULL,
        store TEXT NOT NULL,
        UNIQUE(user_id, date, category, store)  -- Ensure unique transactions per user per day per category per store
    );
    '''
    try:
        cursor = conn.cursor()
        cursor.execute(create_table_sql)
        conn.commit()
    except sqlite3.Error as e:
        print(e)


def fetch_transactions(conn: sqlite3.Connection, user_id: str, year: int) -> List[Dict[str, Any]]:
    """
    Fetch all transactions from the transactions table.

    :param conn: The Connection object.
    :type conn: sqlite3.Connection
    :param user_id: The user id.
    :type user_id: str
    :param year: The year of the transactions.
    :type year: int
    :return: List of transactions.
    :rtype: List[Dict[str, Any]]
    """
    fetch_sql = "SELECT * FROM transactions WHERE user_id = ? AND strftime('%Y', date) = ?"
    try:
        cursor = conn.cursor()
        cursor.execute(fetch_sql, (user_id, str(year)))
        rows = cursor.fetchall()
        return [{"id": t[0], "user_id": t[1], "expense": t[2], "date": t[3], "category": t[4], "store": t[5]} for t in
                rows]
    except sqlite3.Error as e:
        print(e)
        return []


def insert_transactions(conn: sqlite3.Connection, transactions: list[
    tuple[str, float, str, str, Any]]) -> None:
    """
    Insert multiple transactions into the transactions table, ignoring duplicates.

    :param conn: The Connection object.
    :type conn: sqlite3.Connection
    :param transactions: List of transactions to insert.
    :type transactions: list
    :return: None
    """
    insert_sql = '''
    INSERT OR IGNORE INTO transactions (user_id, expense, date, category, store)
    VALUES (?, ?, ?, ?, ?)
    '''
    try:
        cursor = conn.cursor()
        cursor.executemany(insert_sql, transactions)
        conn.commit()
    except sqlite3.Error as e:
        print(e)
