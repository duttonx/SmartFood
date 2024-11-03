"""
Author: Georgy_Cherkasov
"""

import requests
from geopy.distance import geodesic
from itertools import combinations
from typing import List, Tuple, Dict, Any
import sys

DGIS_API_KEY = ''

def geocode_address(address: str) -> Tuple[float, float]:
    """
    Geocode an address using 2GIS API.

    :param address: The address to geocode.
    :return: A tuple containing latitude and longitude.
    :rtype: Tuple[float, float]
    """
    url = f"https://catalog.api.2gis.ru/3.0/items?q={address}&fields=items.point&key={DGIS_API_KEY}"
    response = requests.get(url)
    response.raise_for_status()
    data = response.json()
    coords = data['result']['items'][0]['point']
    return coords['lat'], coords['lon']


def get_nearby_supermarkets(user_coords: Tuple[float, float], search_radius: int) -> List[Dict[str, Any]]:
    """
    Retrieve nearby supermarkets within a specified search radius using 2GIS API.

    :param user_coords: A tuple containing the user's latitude and longitude.
    :param search_radius: The search radius in meters.
    :return: A list of dictionaries containing supermarket details.
    :rtype: List[Dict[str, Any]]
    """
    url = (f"https://catalog.api.2gis.ru/3.0/items?type=branch&q=супермаркеты&point={user_coords[1]},{user_coords[0]}"
           f"&radius={search_radius}&fields=items.point,items.name,items.url&key={DGIS_API_KEY}")
    response = requests.get(url)
    response.raise_for_status()
    data = response.json()
    
    supermarkets = []
    for item in data['result']['items']:
        name = item['name'].split(',')[0]
        if 'супермаркет' in item['name'].lower():
            coords = item['point']
            url = item.get('url', '')
            distance = geodesic(user_coords, (coords['lat'], coords['lon'])).km
            if distance <= search_radius / 1000:
                supermarkets.append({"name": name, "coords": (coords['lat'], coords['lon']), "url": url})
    
    return supermarkets


def parse_supermarket_catalog(store_name: str, product_name: str) -> Dict[str, float]:
    """
    Fetch product details from our local server.

    :param store_name: The name of the store.
    :param product_name: The name of the product.
    :return: A dictionary with product names as keys and their prices as values.
    :rtype: Dict[str, float]
    """
    url = f"http://localhost:5003/products"
    params = {'store': store_name, 'product_name': product_name}
    response = requests.get(url, params=params)
    response.raise_for_status()
    data = response.json()
    
    products = {item['name']: item['price'] for item in data}
    return products


def find_shops(user_coords: Tuple[float, float], product_list: List[str], search_radius: int, 
               daily_budget: float, priority_shops: List[str]) -> List[Tuple[List[Dict[str, Any]], float]]:
    """
    Find optimal shop combinations to purchase products within a budget.

    :param user_coords: A tuple containing the user's latitude and longitude.
    :param product_list: A list of product names to search for.
    :param search_radius: The search radius in meters.
    :param daily_budget: The daily budget in rubles.
    :param priority_shops: A list of priority shop names.
    :return: A list of tuples, each containing a list of supermarkets and the total cost.
    :rtype: List[Tuple[List[Dict[str, Any]], float]]
    """
    if search_radius > 2000:
        raise ValueError("Радиус поиска не должен превышать 2000 метров.")
    
    nearby_supermarkets = get_nearby_supermarkets(user_coords, search_radius)
    
    # Sort supermarkets to prioritize preferred shops
    nearby_supermarkets.sort(key=lambda x: x['name'] not in priority_shops)
    
    # Generate all possible combinations of supermarkets
    all_combinations = []
    for r in range(1, len(nearby_supermarkets) + 1):
        all_combinations.extend(combinations(nearby_supermarkets, r))
    
    # Find the best combination within the budget
    best_combinations = []
    for combo in all_combinations:
        total_cost = 0
        products_found = set()
        for shop in combo:
            for product in product_list:
                product_details = parse_supermarket_catalog(shop['name'], product)
                if product in product_details:
                    total_cost += product_details[product]
                    products_found.add(product)
        if total_cost <= daily_budget and len(products_found) == len(product_list):
            best_combinations.append((combo, total_cost))
    
    return best_combinations


def main():
    address = sys.argv[1]
    user_cords = geocode_address(address)
    product_list = sys.argv[2].split(',')
    search_radius = int(sys.argv[3]) # не более 2000м
    daily_budget = float(sys.argv[4])
    priority_shops = sys.argv[5].split(',')
    
    best_combinations = find_shops(user_cords, product_list, search_radius, daily_budget, priority_shops)
    
    for combo, cost in best_combinations:
        print(f"Комбинации магазинов: {[shop['name'] for shop in combo]}, Общая стоимость: {cost} рублей.")


if __name__ == '__main__':
    main()
