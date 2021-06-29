# All paths are relative, so only run in current directory.

import os
import re
import cv2
import numpy as np 
from PIL import Image

IMAGE_DIR = '../image/'
LABEL_DIR = '../label/'
TEST_DIR = '../test/'

THRESHOLD = 170  # 255 * 0.7 ~= 178
SCALING = 60

TEST_NUMS = [21, 26, 58, 59]  # saved for testing
COLOR_DICT = {'red': [255,0,0], 'blue': [0,0,255], "green": [0,255,0], "black": [0, 0, 0]}

e1 = cv2.getStructuringElement(cv2.MORPH_RECT, (3, 3))
e2 = cv2.getStructuringElement(cv2.MORPH_RECT, (2, 2))

# Denoising: get rid of the black dots in masks
def processMask(mask):
	mask = 255*(cv2.cvtColor(mask, cv2.COLOR_BGR2GRAY) > THRESHOLD).astype('uint8')
	mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, e1)
	mask = cv2.morphologyEx(mask, cv2.MORPH_OPEN, e2)
	return mask

# Collate masks of different categories into a single mask
# Input parameters use arrays to account for multiple masks for the same category
def collateMasks(ripe_paths, underripe_paths, overripe_paths, save_path):
	ripe_masks, underripe_masks, overripe_masks = [], [], []
	for path in ripe_paths:
		p = cv2.imread(path)
		ripe_masks.append(np.array(processMask(p)))
	for path in underripe_paths:
		p = cv2.imread(path)
		underripe_masks.append(np.array(processMask(p)))
	for path in overripe_paths:
		p = cv2.imread(path)
		overripe_masks.append(np.array(processMask(p)))

	[h, w] = np.shape(ripe_masks[0])
	mask = np.zeros((h, w), dtype=np.uint8)
	for x in range(h):
		for y in range(w):
			marked = False
			for ripe_mask in ripe_masks:
				if ripe_mask[x, y] > THRESHOLD:
					mask[x, y] = 2 * SCALING
					marked = True
					break
			for underripe_mask in underripe_masks:
				if not marked and underripe_mask[x, y] > THRESHOLD:
					mask[x, y] = 3 * SCALING
					marked = True
					break
			for overripe_mask in overripe_masks:
				if not marked and overripe_mask[x, y] > THRESHOLD:
					mask[x, y] = 1 * SCALING
					marked = True
					break
			if not marked:
				mask[x, y] = 0

	im = Image.fromarray(mask)
	im.save(save_path)

# Collate masks of different categories into a single mask
# Input parameters use arrays to account for multiple masks for the same category
def collateMasksRgb(ripe_paths, underripe_paths, overripe_paths, save_path):
	ripe_masks, underripe_masks, overripe_masks = [], [], []
	for path in ripe_paths:
		p = cv2.imread(path)
		ripe_masks.append(np.array(processMask(p)))
	for path in underripe_paths:
		p = cv2.imread(path)
		underripe_masks.append(np.array(processMask(p)))
	for path in overripe_paths:
		p = cv2.imread(path)
		overripe_masks.append(np.array(processMask(p)))

	[h, w] = np.shape(ripe_masks[0])
	mask = np.zeros((h, w, 3), dtype=np.uint8)
	for x in range(h):
		for y in range(w):
			marked = False
			for ripe_mask in ripe_masks:
				if ripe_mask[x, y] > THRESHOLD:
					mask[x, y, :] = COLOR_DICT['red'] # 2 * SCALING
					marked = True
					break
			for underripe_mask in underripe_masks:
				if not marked and underripe_mask[x, y] > THRESHOLD:
					mask[x, y, :] = COLOR_DICT['blue'] # 3 * SCALING
					marked = True
					break
			for overripe_mask in overripe_masks:
				if not marked and overripe_mask[x, y] > THRESHOLD:
					mask[x, y, :] = COLOR_DICT['green'] # 1 * SCALING
					marked = True
					break
			if not marked:
				mask[x, y, :] = [0, 0, 0]

	im = Image.fromarray(mask)
	im.save(save_path)

count = 0
for x in os.listdir('./'):
	if os.path.isdir(x):
		for y in os.listdir(x):
			path = os.path.join(x, y)
			if os.path.isdir(path):
				# im_0000(\d\d)
				num = int(y[7:9])
				if num in TEST_NUMS:
					continue
				ripe_paths, underripe_paths, overripe_paths = [], [], []
				for z in os.listdir(path):
					if re.match(r'.*-tag-ripe-0.png', z):
						ripe_paths.append(os.path.join(path, z))
					if re.match(r'.*-tag-un.*ripe-0.png', z):
						underripe_paths.append(os.path.join(path, z))
					if re.match(r'.*-tag-overripe-0.png', z):
						overripe_paths.append(os.path.join(path, z))
				save_path = os.path.join(LABEL_DIR, str(num) + '.jpg')
				collateMasksRgb(ripe_paths, underripe_paths, overripe_paths, save_path)
			else:
				m = re.search(r'im_0000(\d+).jpg', y)
				if m:
					num = int(m.group(1))
					img = Image.open(path)
					if num in TEST_NUMS:
						save_path = os.path.join(TEST_DIR, str(count) + '.jpg')
						count += 1
						img.save(save_path)
					else:
						save_path = os.path.join(IMAGE_DIR, str(num) + '.jpg')
						img.save(save_path)

