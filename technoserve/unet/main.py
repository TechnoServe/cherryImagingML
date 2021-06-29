from model import *
from data import *

data_gen_args = dict(rotation_range=0.2,
                    width_shift_range=0.05,
                    height_shift_range=0.05,
                    zoom_range=0.05,
                    horizontal_flip=True,
                    fill_mode='reflect')
train_genenator = trainGenerator(14,'data', 'image', 'label', data_gen_args, flag_multi_class=True, num_class=4)

model = unet(num_class=4)
model_checkpoint = ModelCheckpoint('unet.hdf5', monitor='loss',verbose=1, save_best_only=True)
model.fit_generator(train_genenator, steps_per_epoch=4, epochs=10, callbacks=[model_checkpoint])

# model = tf.keras.models.load_model('unet.hdf5')

test_generator = testGenerator("data/test")
results = model.predict_generator(test_generator, 4, verbose=1)
saveResult("data/predict", results)
