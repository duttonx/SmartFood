import random
from openai import OpenAI
import sys

with open('.secret', 'r') as f:
    token = f.read()

client = OpenAI(
    api_key=token,
)


def fetch_recipes(pstr):
    try:
        response = client.chat.completions.create(
            model='gpt-4o-mini',  # Use the desired model
            messages=[
                {"role": "user", "content": pstr}
            ],
            max_tokens=10000,
            temperature=random.uniform(0.5, 0.8),
        )

        rs = response.choices[0].message.content
        return rs

    except Exception as e:
        print(f"An error occurred: {e}")
        return None


kcal = sys.argv[1]
contraindicated = sys.argv[2]
money = sys.argv[3]

prompt = "Сгенерируй диету на 7 дней и 3 приема пищи. Используй для этого продукты, распространенные в России. \
Рецпеты должны быть полезными или нейтральными для здоровья. Ответ в JSON.\
Формат: [{\"day\": 1, \"name\": \"...\", \"ingredients\": [...], \"recipe\": \"...\"}]\
Без лишних слов Количество ккал в день: " + kcal + (". Не суй в ответ напитки. В день должно быть не менее 3 блюд."
" Также исключи, блюда которые попадют под эти категории: ") + contraindicated + (". "
"При составлении диеты ориентируйся на бюджет в день ") + str(
    money) + " рублей"

recipes = fetch_recipes(prompt)
print(recipes[8:-4])
