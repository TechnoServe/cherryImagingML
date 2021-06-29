import numpy as np 
from PIL import Image
import cv2
import os
SCALING = 60
SRC = 'data/images/'

output_imgs = [x for x in os.listdir(SRC) if 'output' in x]
target_imgs = [x for x in os.listdir(SRC) if 'target' in x]


for img1, img2 in zip(output_imgs, target_imgs):

	p1 = cv2.imread(os.path.join(SRC, img1)) *SCALING
	p2 = cv2.imread(os.path.join(SRC, img2)) *SCALING



	# print(p1.shape, np.all(p1[:,:,0]==p1[:,:,1])) 
	# prints (256,256,3), False

	cv2.imwrite(os.path.join(SRC, img1[:-4] + '.png'), p1)
	cv2.imwrite(os.path.join(SRC, img2[:-4] + '.png'), p2)

# print(p2[90:95,90:95,:])
