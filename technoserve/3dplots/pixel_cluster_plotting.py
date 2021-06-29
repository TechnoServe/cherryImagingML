import os
import cv2
import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

def plot3d(pixels, colors_rgb,
        axis_labels=list("RGB"), axis_limits=[(0, 255), (0, 255), (0, 255)]):
    """Plot pixels in 3D. Source: https://gist.github.com/jychstar/0daa6ea1a8a759a279092042f396049b"""

    # Create figure and 3D axes
    fig = plt.figure(figsize=(8, 8))
    ax = Axes3D(fig)

    # Set axis limits
    ax.set_xlim(*axis_limits[0])
    ax.set_ylim(*axis_limits[1])
    ax.set_zlim(*axis_limits[2])

    # Set axis labels and sizes
    ax.tick_params(axis='both', which='major', labelsize=14, pad=8)
    ax.set_xlabel(axis_labels[0], fontsize=16, labelpad=16)
    ax.set_ylabel(axis_labels[1], fontsize=16, labelpad=16)
    ax.set_zlabel(axis_labels[2], fontsize=16, labelpad=16)

    # Plot pixel values with colors given in colors_rgb
    ax.scatter(
        pixels[:, :, 0].ravel(),
        pixels[:, :, 1].ravel(),
        pixels[:, :, 2].ravel(),
        c=colors_rgb.reshape((-1, 3)), edgecolors='none')

    return ax  # return Axes3D object for further manipulation


# image categories
categories = ['ripe', 'underripe', 'overripe']

# Read a color image
file_location = '.' # path of directory
files = [file for file in os.listdir(file_location) if file.endswith(".jpg")]
print(files)

for i, curr_file in enumerate(files):
	img = cv2.imread(curr_file)

	# Select a small fraction of pixels to plot by subsampling it
	scale = max(img.shape[0], img.shape[1], 64) / 64  # at most 64 rows and columns
	img_small = cv2.resize(img, (int(img.shape[1] / scale), int(img.shape[0] / scale)),\
					 interpolation=cv2.INTER_NEAREST)

	# Convert subsampled image to desired color space(s)
	img_small_RGB = cv2.cvtColor(img_small, cv2.COLOR_BGR2RGB)  # OpenCV uses BGR, matplotlib likes RGB
	img_small_HSV = cv2.cvtColor(img_small, cv2.COLOR_BGR2HSV)
	img_small_rgb = img_small_RGB / 255.  # scaled to [0, 1], only for plotting

	if i==0:
		master_img_RGB = img_small_RGB
		master_img_HSV = img_small_HSV
		master_img_rgb = img_small_rgb
	else:
		master_img_RGB = np.vstack((master_img_RGB, img_small_RGB))
		master_img_HSV = np.vstack((master_img_HSV, img_small_HSV))
		master_img_rgb = np.vstack((master_img_rgb, img_small_rgb))

# Plot and show
plot3d(master_img_RGB, master_img_rgb)
plt.show()
plt.savefig('3Images_RGBscatterPlot.png')

plot3d(master_img_HSV, master_img_rgb, axis_labels=list("HSV"))
plt.show()
plt.savefig('3Images_HSVscatterPlot.png')
