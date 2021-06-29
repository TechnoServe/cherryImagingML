python3 tools/process.py --input_dir ../24.03.21_images --b_dir ../24.03.21_images --operation combine --output_dir facades_24.03.21/

python pix2pix.py  --mode train  --output_dir facades_train  --max_epochs 10   --input_dir facades/train --which_direction AtoB

python pix2pix.py  --mode test  --output_dir facades_test_24.03.21  --input_dir facades_24.03.21/val  --checkpoint facades_train

### make sure to replace `import tensorflow as tf` in `.py` files if you get "no attribute" erros for tf latest 2.4
import tensorflow.compat.v1 as tf
tf.disable_v2_behavior()