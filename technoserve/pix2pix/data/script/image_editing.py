import cv2
import numpy as np
import os

folder_location = './'

folders = ['No Classified red coffee 12.03.21']
folder_raw_renamed = 'No Classified red coffee 12.03.21 _RAW_RENAMED'
if (not os.path.exists(os.path.join(folder_location, folder_raw_renamed))):
		os.mkdir(os.path.join(folder_location, folder_raw_renamed))
# print(os.path.exists(os.path.join(folder_location, folder_raw_renamed)))

for folder in folders:
	# read the names of all the jpg files in folder
	raw_imgs = os.listdir(os.path.join(folder_location, folder))
	raw_imgs = [raw_imgs[i] for i in range(len(raw_imgs)) if raw_imgs[-3:]=='jpeg' or 'jpg' or 'png']
	# NUM_IMGS = len(raw_imgs)

	#check if the save path for resized image exist, if not create it
	save_path = os.path.join(folder_location, folder + ' _CROPPED')
	if (not os.path.exists(save_path)):
		os.mkdir(save_path)

	# read each image
	for i, im in enumerate(raw_imgs):
		# read source image
		image = cv2.imread(os.path.join(folder_location, folder, im))

		# flip the image to regular 1280x720 before resizing
		print(image.shape)

		# resize image to destination size dsize : width,height
		# dsize = (512, 512)
		output = image[0:512,0:512,:]

		print(output.shape, np.max(output), np.min(output), np.all(output[:,:,0] == output[:,:,1]))

		# write the resized image
		cv2.imwrite(os.path.join(folder_location, folder_raw_renamed,'im_'+str(i).zfill(6)+'.jpg'), image)
		cv2.imwrite(os.path.join(folder_location, save_path         ,'im_'+str(i).zfill(6)+'.jpg'), output)




