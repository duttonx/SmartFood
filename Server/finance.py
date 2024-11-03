import sys

import requests
from collections import Counter

author = "Georgy_Cherkasov"

# Constants
API_URL = "http://127.0.0.1:5001/transactions"
AUTH_TOKEN = "7wxm97k6qd7vnaxjic993nhvzsyn13fxqe960nu1oqcxb4ijmf2fdkl93r3wnlvw"


def get_transactions(user_id: str, auth_token: str, year: int) -> list:
    """
    Fetch transactions from the API for a specific user and year.

    :param user_id: The ID of the user.
    :type user_id: str
    :param auth_token: The authentication token.
    :type auth_token: str
    :param year: The year for the transactions.
    :type year: int
    :return: A list of transactions.
    :rtype: list
    """
    headers = {
        "Authorization": f"Bearer {auth_token}"
    }
    params = {
        "user_id": user_id,
        "year": year
    }
    response = requests.get(API_URL, headers=headers, params=params)
    response.raise_for_status()
    return response.json()


def calculate_average_spending(transactions: list) -> float:
    """
    Calculate the average monthly spending from a list of transactions.

    :param transactions: A list of transactions.
    :type transactions: list
    :return: The average monthly spending.
    :rtype: float
    """
    total_spent = sum(transaction['expense'] for transaction in transactions)

    average_monthly_spending = total_spent / 12
    return average_monthly_spending / 30  # Average daily spending


def get_top_stores(transactions: list, top_n: int = 3) -> list:
    """
    Get the top N stores where purchases are most frequently made.

    :param transactions: A list of transactions.
    :type transactions: list
    :param top_n: The number of top stores to return.
    :type top_n: int
    :return: A list of top N stores.
    :rtype: list
    """
    store_counter = Counter(transaction['store'] for transaction in transactions)
    top_stores = store_counter.most_common(top_n)
    return [store for store, _ in top_stores]


def main() -> None:
    """
    Main function to fetch transactions, calculate the average daily spending, and find top stores.

    :return: None
    """

    user_id = sys.argv[1]
    year = int(sys.argv[2])
    transactions = get_transactions(user_id, AUTH_TOKEN, year)
    average_spending = calculate_average_spending(transactions)
    top_stores = get_top_stores(transactions)

    print(f"{average_spending} || {top_stores}")

    # print(f"Average daily spending: {average_spending:.2f} RUB")
    # print("Top 3 stores where purchases are most frequently made:")
    # for store in top_stores:
    #     print(store)


if __name__ == "__main__":
    main()
