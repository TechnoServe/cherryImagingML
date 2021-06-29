from __future__ import print_function
from keras.preprocessing.image import ImageDataGenerator
import numpy as np 
import os
import glob
import skimage.io as io
import skimage.transform as trans
import cv2

# BGR colors
one = [0, 0, 0]  # black
two = [128, 128, 128]  # gray
three = [192, 192, 128]  # teal
four = [255, 69, 0]  # blue
COLOR_DICT = np.array([one, two, three, four])

def adjustData(img, mask, flag_multi_class, num_class):
    if(flag_multi_class):
        img = img / 255.
        if (len(mask.shape) > 2):
            mask = mask[:, :, :, 0] if (len(mask.shape) == 4) else mask[:, :, 0]
        new_mask = np.zeros(mask.shape + (num_class,))
        for i in range(num_class):
            new_mask[mask == i, i] = 1
        mask = new_mask
    elif (np.max(img) > 1):
        img = img / 255.
        mask = mask / 255.
        mask[mask > 0.5] = 1
        mask[mask <= 0.5] = 0
    return (img, mask)

def trainGenerator(batch_size, train_path, image_folder, mask_folder,
    aug_dict, image_color_mode="rgb", mask_color_mode="grayscale",
    image_save_prefix="image", mask_save_prefix="mask", flag_multi_class=True,
    num_class=4, save_to_dir=None, target_size=(512, 512), seed=1):
    '''
    can generate image and mask at the same time
    use the same seed for image_datagen and mask_datagen to ensure the transformation for image and mask is the same
    if you want to visualize the results of generator, set save_to_dir = "your path"
    '''
    image_datagen = ImageDataGenerator(**aug_dict)
    mask_datagen = ImageDataGenerator(**aug_dict)
    image_generator = image_datagen.flow_from_directory(
        train_path,
        classes = [image_folder],
        class_mode = None,
        color_mode = image_color_mode,
        target_size = target_size,
        batch_size = batch_size,
        save_to_dir = save_to_dir,
        save_prefix  = image_save_prefix,
        seed = seed)
    mask_generator = mask_datagen.flow_from_directory(
        train_path,
        classes = [mask_folder],
        class_mode = None,
        color_mode = mask_color_mode,
        target_size = target_size,
        batch_size = batch_size,
        save_to_dir = save_to_dir,
        save_prefix  = mask_save_prefix,
        seed = seed)
    train_generator = zip(image_generator, mask_generator)
    for (img, mask) in train_generator:
        img, mask = adjustData(img, mask, flag_multi_class, num_class)
        yield (img, mask)


def testGenerator(test_path, num_image=4, target_size=(512, 512, 3), flag_multi_class=True, as_gray=False):
    for i in range(num_image):
        img = io.imread(os.path.join(test_path, "%d.jpg" % i), as_gray = as_gray)
        img = img / 255.
        img = trans.resize(img, target_size, mode='constant')
        img = np.reshape(img,img.shape+(1,)) if (not flag_multi_class) else img
        img = np.reshape(img,(1,)+img.shape)
        yield img


def saveResult(save_path, npyfile, flag_multi_class=True, num_class=4):
    for i, item in enumerate(npyfile):
        img = item
        img_std = np.zeros((img.shape[0], img.shape[1], 3), dtype=np.uint8)
        for row in range(len(img)):
            for col in range(len(img[row])):
                num = np.argmax(img[row][col])
                img_std[row][col] = COLOR_DICT[num]
        cv2.imwrite(os.path.join(save_path, ("%s_predict.png") % i), img_std)
