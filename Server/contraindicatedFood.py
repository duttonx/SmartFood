import json
import sys
from g4f.client import Client


arg = sys.argv[1]
def gpt(ask):
    prompt = f"Скажи мне общее название заболевания {ask}. Ответ дай в формате: Сердечно-Сосудистые заболевания или Диабет или Заболевание ЖКТ или Заболевание почек или Заболевание Печени. Если в этом списке нет этого названия, то ответ Отсутствует. Не добавляй ничего лишнего кроме этих фраз, не добавляй что ты был рад мне помочь и так далее. В своём ответе не используй слово ответ и символ точки."
    client = Client()
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[{"role": "user", "content": prompt}],
    )
    return response.choices[0].message.content

with open('food.json', 'r') as file:
    data = json.load(file)
a = 0
res = []
while True:
    try:
        a +=1
        res = data[gpt(arg).lower()]
        break
    except:
        if a >= 5:
            break
        pass

print(res)