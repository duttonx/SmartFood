import sys

from PIL import Image
from ultralytics import YOLO

model = YOLO("yolov8x-oiv7.pt")


img_path = sys.argv[1]
img = Image.open(img_path)

results = model.predict(source=img, classes=[
    10, 12, 16, 21, 26, 39, 65, 67, 76, 89, 92, 105, 119, 125, 126, 140, 146,
    178, 210, 213, 226, 227, 229, 233, 306, 323, 347, 356, 365, 372, 374, 375,
    389, 399, 404, 414, 459, 496, 507, 540, 566, 579, 592, 600, 589
])

# results[0].show()

# detected_items = [model.names[int(cls)] for cls in results[0].boxes.cls.cpu().numpy()]

# items_dict = {}
# for item in set(detected_items):
#     items_dict.update({item:0})
#
# for i in detected_items:
#     items_dict[i] += 1
#
# print(items_dict)
