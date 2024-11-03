from flask import Flask, request, jsonify
from db_manager import create_connection, fetch_transactions

__author__ = "Georgy_Cherkasov"

app = Flask(__name__)

@app.route('/transactions', methods=['GET'])
def get_transactions() -> jsonify:
    """
    Handle GET requests to fetch transactions based on user_id and year.

    :return: JSON response containing the list of transactions.
    :rtype: jsonify
    """
    user_id = request.args.get('user_id')
    year = int(request.args.get('year'))

    conn = create_connection()
    transactions = fetch_transactions(conn, user_id, year)
    conn.close()
    return jsonify(transactions)

if __name__ == '__main__':
    app.run(port=5001)