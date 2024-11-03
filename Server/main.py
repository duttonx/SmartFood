import os
import re
import subprocess
from flask import Flask, request, jsonify
from deep_translator import GoogleTranslator

translator = GoogleTranslator(source='en', target='ru')

app = Flask(__name__)


@app.route('/api/contraindicatedFood', methods=['POST'])
def contraindicated_food():
    data = request.json
    arg = data['sickness']
    process = subprocess.Popen(
        ['python', 'contraindicatedFood.py', arg],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    stdout, stderr = process.communicate()
    return jsonify({'status': 'success', 'answer': stdout}), 200


@app.route('/api/food', methods=['POST'])
def food():
    if 'image' not in request.files:
        return "No image part in the request", 400

    image_file = request.files['image']
    temp_image_path = 'temp_image.jpg'
    image_file.save(temp_image_path)

    if image_file.filename == '':
        return "No selected file", 400
    process = subprocess.Popen(
        ['python', 'foodRecognizer.py', temp_image_path],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    stdout, stderr = process.communicate()

    os.remove(temp_image_path)

    pattern = r"(\d+ [A-Za-z\s]+)"

    matches = re.findall(pattern, stdout)

    result = ', '.join(matches)
    trans = translator.translate(result)
    return trans, 200

@app.route('/api/finance', methods=['POST'])
def finance():
    data = request.json
    process = subprocess.Popen(
        ['python', 'finance.py', str(data["user_id"]), str(data["year"])],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    stdout, stderr = process.communicate()
    return stdout, 200

@app.route('/api/find', methods=['POST'])
def find_products():
    data = request.json
    process = subprocess.Popen(
        ['python', 'findProducts.py', str(data["address"]), str(data["products"]), str(data["radius"]),
         str(data["budget"]), str(data["shops"])],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    stdout, stderr = process.communicate()
    return stdout, 200

@app.route('/api/diet', methods=['POST'])
def diet():
    data = request.json
    process = subprocess.Popen(
        ['python', 'ai.py', str(data["kcal"]), str(data["food"]), str(data["money"])],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )
    stdout, stderr = process.communicate()
    return stdout, 200


if __name__ == '__main__':
    app.run(debug=True)
