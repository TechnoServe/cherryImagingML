import cv2
import numpy as np
import os

COLOR = {'blue':[255,0,0], 'green' :[0,255,0], 'red' : [0,0,255], 'black': [0,0,0]}
COLOR_DICT = {0: COLOR['blue'], 1: COLOR['green'], 2: COLOR['red'], 3: COLOR['black']}

SRC = './data/images'

THRESHOLD = 50

# Delete previously generated files
rounded_color_files = [x for x in os.listdir(SRC) if 'rounded_color' in x]
for file in rounded_color_files:
	os.remove(os.path.join(SRC, file))

# Output new files
im_files = [x for x in os.listdir(SRC) if 'output' in x]

for im_file in im_files:

	img = cv2.imread(os.path.join(SRC, im_file))
	rounded_colored_img = np.zeros(img.shape)

	pixels = [0., 0., 0.,]
	all_pixels = 0.

	for i in range(img.shape[0]):
		for j in range(img.shape[1]):
			rounded_color_idx = np.argmax(list(img[i,j,:]))
			if img[i,j,rounded_color_idx] < THRESHOLD:
				rounded_colored_img[i,j,:] = COLOR_DICT[3]
			else:
				rounded_colored_img[i,j,:] = COLOR_DICT[rounded_color_idx]
				all_pixels += 1
				pixels[rounded_color_idx] += 1

	# Output percent of cherries being ripe
	# CV2 uses BGR color mode
	print(im_file + ":")
	print("Underripe: " + str(pixels[1]*100/all_pixels) + "%")
	print("Ripe: " + str(pixels[2]*100/all_pixels) + "%")
	print("Overripe: " + str(pixels[0]*100/all_pixels) + "%")
	print("")

	cv2.imwrite(os.path.join(SRC,im_file[:-4] + '_rounded_color.png'), rounded_colored_img)

