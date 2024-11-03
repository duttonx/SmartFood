"""
Author: Georgy_Cherkasov
"""

from flask import Flask, jsonify, request
from dbFind import (create_table, get_products, generate_data)

app = Flask(__name__)

@app.route('/products', methods=['GET'])
def get_products_endpoint() -> jsonify:
    """
    Endpoint to retrieve products by store name and product name.

    This endpoint accepts query parameters 'store' and 'product_name' and returns a JSON response
    containing the names and prices of products available in the specified store and matching the product name.

    :return: JSON response containing product names and prices.
    :rtype: jsonify
    """
    store = request.args.get('store')
    product_name = request.args.get('product_name')
    products = get_products(store, product_name)
    return jsonify(products)

if __name__ == '__main__':
    # create_table()
    # generate_data()
    app.run(port=5003)