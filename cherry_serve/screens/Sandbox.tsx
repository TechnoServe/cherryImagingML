import React, { useEffect } from "react";
import { StackScreenProps } from "@react-navigation/stack";
import { StyleSheet, Image } from "react-native";
import {
  loadTFLiteModel,
  ImageSegmenter,
  ImageSegmenterOptions,
} from "@tensorflow/tfjs-tflite";
import { bundleResourceIO, fetch } from "@tensorflow/tfjs-react-native";
import { loadLayersModel } from "@tensorflow/tfjs-layers";
import { browserFiles } from "@tensorflow/tfjs-core/dist/io/browser_files";
import * as FileSystem from "expo-file-system";
import * as jpeg from "jpeg-js";
import * as ImageManipulator from "expo-image-manipulator";
import { getBackend, loadGraphModel, tensor3d } from "@tensorflow/tfjs";

import { View, Text, TouchableOpacity } from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { InferenceParamList } from "../types";
import weights from "../assets/models/weights2.bin";

export function SandboxScreen({
  navigation,
}: StackScreenProps<InferenceParamList, "Inference">) {
  const imageToTensor = (rawImageData) => {
    const TO_UINT8ARRAY = true;
    const { width, height, data } = jpeg.decode(rawImageData, TO_UINT8ARRAY);

    const buffer = new Uint8Array(width * height * 3);
    let offset = 0; // offset into original data
    for (let i = 0; i < buffer.length; i += 3) {
      buffer[i] = data[offset];
      buffer[i + 1] = data[offset + 1];
      buffer[i + 2] = data[offset + 2];

      offset += 4;
    }

    return tensor3d(buffer, [height, width, 3]);
  };
  const BASE64_MARKER = ";base64,";

  function atob(input = "") {
    const chars =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
    const str = input.replace(/[=]+$/, "");
    let output = "";

    if (str.length % 4 == 1) {
      throw new Error(
        "'atob' failed: The string to be decoded is not correctly encoded."
      );
    }
    for (
      let bc = 0, bs = 0, buffer, i = 0;
      (buffer = str.charAt(i++));
      ~buffer && ((bs = bc % 4 ? bs * 64 + buffer : buffer), bc++ % 4)
        ? (output += String.fromCharCode(255 & (bs >> ((-2 * bc) & 6))))
        : 0
    ) {
      buffer = chars.indexOf(buffer);
    }

    return output;
  }

  function convertDataURIToBinary(dataURI) {
    const base64Index = dataURI.indexOf(BASE64_MARKER) + BASE64_MARKER.length;
    const base64 = dataURI.substring(base64Index);
    const raw = atob(base64);
    const rawLength = raw.length;
    const array = new Uint8Array(new ArrayBuffer(rawLength));

    for (let i = 0; i < rawLength; i++) {
      array[i] = raw.charCodeAt(i);
    }
    return array;
  }

  const getRawImage = async (imgURI) => {
    const str = await FileSystem.readAsStringAsync(imgURI.uri, {
      encoding: FileSystem.EncodingType.Base64,
    });
    return convertDataURIToBinary(str);
  };

  const resizeImage = async (inputImage, size = 512) => {
    const result = await ImageManipulator.manipulateAsync(
      inputImage.uri,
      [{ resize: { width: size, height: size } }],
      { compress: 0.8, format: ImageManipulator.SaveFormat.PNG }
    );
    return result;
  };

  const runSegmentation = async () => {
    try {
      const modelJSON = await require("../assets/models/model2.json");
      const weights = await require("../assets/models/weights2.bin");
      const bundled = await bundleResourceIO(modelJSON, weights);
      console.log(1);
      console.log(getBackend());
      const segmenter = await loadGraphModel(bundled);
      // const segmenter = await loadLayersModel(bundled);
      // console.log(model);
      const imageExample = await resizeImage(
        Image.resolveAssetSource(require("../assets/images/cherry.png"))
      );
      const rawImage = await getRawImage(imageExample);
      // const tensorExample = imageToTensor(imageExample.uri);
      console.log(rawImage);
      // const result = await segmenter.predict(tensorExample).data();

      // const resource = await FileSystem.readAsStringAsync(model.uriString);
      // console.log("Output Tensor: ", result);
      // const model = loadTFLiteModel({
      //   model:
      //     "https://tfhub.dev/tensorflow/lite-model/deeplabv3/1/metadata/2?lite-format=tflite",
      // });
      // const resource = await fetch(
      //   "http://127.0.0.1:5500/assets/model/model.json"
      // );
      // const model = await loadLayersModel(resource);
      // console.log(model);
    } catch (e) {
      console.log(e);
    }
  };

  useEffect(() => {
    (async () => {
      runSegmentation();
    })();
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Sandbox Screen</Text>
      <MonoText style={styles.description}>
        Load an image and generate a mask
      </MonoText>
      <TouchableOpacity style={styles.button} onPress={runSegmentation}>
        <Text>Run Segmentation</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 20,
  },
  description: {
    textAlign: "center",
    marginHorizontal: 48,
  },
  button: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    margin: 24,
  },
});
