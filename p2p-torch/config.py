import torch
import albumentations as A
from albumentations.pytorch import ToTensorV2

BASE_PATH = "drive/MyDrive/p2p-torch/"

DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
TRAIN_DIR = BASE_PATH + "cherries/train"
VAL_DIR = BASE_PATH + "cherries/val"
LEARNING_RATE = 2e-4
BATCH_SIZE = 16
NUM_WORKERS = 2
IMAGE_SIZE = 512
CHANNELS_IMG = 3
L1_LAMBDA = 100
LAMBDA_GP = 10
NUM_EPOCHS = 50
LOAD_MODEL = False
SAVE_MODEL = True
SAVE_EXAMPLES = True
CHECKPOINT_DISC = BASE_PATH + "checkpoints/disc.pth.tar"
CHECKPOINT_GEN = BASE_PATH + "checkpoints/gen.pth.tar"
MODEL_PATH = "p2p"

both_transform = A.Compose(
    [A.Resize(width=512, height=512), ], additional_targets={"image0": "image"},
)

transform_only_input = A.Compose(
    [
        A.Normalize(mean=[0.5, 0.5, 0.5], std=[0.5, 0.5, 0.5], max_pixel_value=255.0, ),
        ToTensorV2(),
    ]
)

transform_only_mask = A.Compose(
    [
        A.Normalize(mean=[0.5, 0.5, 0.5], std=[0.5, 0.5, 0.5], max_pixel_value=255.0, ),
        ToTensorV2(),
    ]
)
