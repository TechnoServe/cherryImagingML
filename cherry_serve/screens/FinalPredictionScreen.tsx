/* eslint-disable @typescript-eslint/no-var-requires */
import React, {
  useState,
  useEffect,
  useCallback,
  useMemo,
  useRef,
} from "react";
import { RouteProp } from "@react-navigation/native";
import {
  Image,
  StyleSheet,
  ScrollView,
  ActivityIndicator,
  View as DefaultView,
} from "react-native";
import BottomSheet from "@gorhom/bottom-sheet";
import { StatusBar } from "expo-status-bar";
import * as FileSystem from "expo-file-system";
import * as jpeg from "jpeg-js";
import * as tf from "@tensorflow/tfjs";
import {
  fetch,
  decodeJpeg,
  bundleResourceIO,
} from "@tensorflow/tfjs-react-native";
import * as ImageManipulator from "expo-image-manipulator";
import { loadLayersModel, model } from "@tensorflow/tfjs";

import { InferenceParamList } from "../types";
import { View, Text, TouchableOpacity } from "../components/Themed";
import { MonoText } from "../components/StyledText";

type PredictionListItemProps = {
  color: string;
  name: string;
  score: number;
};

const PredictionListItem = ({
  color,
  name,
  score,
}: PredictionListItemProps) => {
  return (
    <DefaultView
      style={{
        flex: 1,
        width: "100%",
        paddingVertical: 12,
        paddingHorizontal: 24,
        marginBottom: -1,
        flexDirection: "row",
        alignItems: "center",
        borderWidth: 1,
        borderColor: "#EEE",
        borderStyle: "solid",
      }}
    >
      <Text style={{ color: "black", width: 72 }}>{name}</Text>
      <DefaultView
        style={{
          flex: 1,
          marginHorizontal: 32,
          height: 6,
          borderRadius: 50,
          backgroundColor: color,
        }}
      />
      <Text style={{ width: 48, color: "black" }}>{score}%</Text>
    </DefaultView>
  );
};

const results = [
  { color: "red", score: 90, name: "Ripe" },
  { color: "green", score: 8, name: "Underripe" },
  { color: "blue", score: 2, name: "Overripe" },
];

export function FinalPredictionScreen({
  route,
}: {
  route: RouteProp<InferenceParamList, "FinalPrediction">;
}) {
  // ref
  const bottomSheetRef = useRef<BottomSheet>(null);

  // variables
  const snapPoints = useMemo(() => ["3%", "35%"], []);

  // callbacks
  const handleSheetChanges = useCallback((index: number) => {
    console.log("handleSheetChanges", index);
  }, []);

  const [predictionState, setPredictionState] = useState<boolean | null>(null);

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

    return tf.tensor3d(buffer, [height, width, 3]);
  };

  const resizeImage = async (inputImage, size = 512) => {
    const result = await ImageManipulator.manipulateAsync(
      inputImage,
      [{ resize: { width: size, height: size } }],
      { compress: 0.8, format: ImageManipulator.SaveFormat.PNG }
    );
    return result;
  };

  const helper = async () => {
    try {
      const base64Image = await FileSystem.readAsStringAsync(image, {
        encoding: FileSystem.EncodingType.Base64,
      });
      const imageBuffer = tf.util.encodeString(base64Image, "base64").buffer;
      const raw = new Uint8Array(imageBuffer);
      const imageTensor = imageToTensor(raw);
      console.log("ImageTensor: ", imageTensor);

      // const prediction = await model.performSegmentation(imageTensor);
      // Handle result / Save or display prediction
    } catch (error) {
      console.log("Exeption Error: ", error);
    }
  };

  const runInference = async (inputImage) => {
    // const modelJson = require("../assets/models/model.json");
    // const modelWeights = require("../assets/models/weights.bin");

    try {
      // const model = await loadLayersModel(
      //   bundleResourceIO(modelJson, modelWeights)
      // );
      // console.log(model);
      // const base64Image = await FileSystem.readAsStringAsync(inputImage, {
      //   encoding: FileSystem.EncodingType.Base64,
      // });
      // const imageBuffer = tf.util.encodeString(base64Image, "base64").buffer;
      // const raw = new Uint8Array(imageBuffer);
      // const imageTensor = imageToTensor(raw);
      // const [prediction] = await model.predict(imageTensor);
      // console.log(prediction);
      // const model = await loadLayersModel(
      //   require("../assets/model/model.tflite")
      // );
      // console.log(model);
      // const imageAssetPath = Image.resolveAssetSource(source);
      // const response = await fetch(imageAssetPath, {}, { isBinary: true });
      // const imageData = await response.arrayBuffer();
      // const imageTensor = decodeJpeg(imageData);
    } catch (error) {
      console.log("Error: ", error);
    } finally {
      setTimeout(() => setPredictionState(true), 2000);
      setTimeout(() => bottomSheetRef.current?.snapTo(1), 3000);
    }
  };

  useEffect(() => {
    runInference(route.params.data);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (predictionState == null) {
    return (
      <View style={{ justifyContent: "center", alignItems: "center", flex: 1 }}>
        <StatusBar style="auto" />
        <MonoText style={{ marginBottom: 32 }}>Generating Results</MonoText>
        <ActivityIndicator size="large" color="red" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <StatusBar style="auto" />
      <ScrollView style={styles.scrollViewContainer}>
        <View style={styles.imageWrapper}>
          <Image style={styles.image} source={{ uri: route.params.data }} />
          <Image style={styles.image} source={{ uri: route.params.data }} />
        </View>
        <View style={styles.buttonWrapper}>
          <TouchableOpacity style={styles.button}>
            <Text>Save</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>

      <BottomSheet
        ref={bottomSheetRef}
        index={0}
        snapPoints={snapPoints}
        onChange={handleSheetChanges}
      >
        <View style={styles.bottomSheetContentContainer}>
          {results.map(({ color, name, score }: PredictionListItemProps) => (
            <PredictionListItem key={name} {...{ color, name, score }} />
          ))}
        </View>
      </BottomSheet>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "flex-start",
    alignItems: "flex-start",
  },
  scrollViewContainer: {
    width: "100%",
    padding: 20,
  },
  imageWrapper: {
    width: "100%",
    alignItems: "center",
  },
  image: {
    width: 232,
    height: 232,
    resizeMode: "center",
    marginBottom: 32,
  },
  buttonWrapper: {
    flexDirection: "row",
    width: "100%",
    justifyContent: "center",
  },
  button: {
    paddingHorizontal: 32,
    paddingVertical: 8,
    width: 120,
    alignItems: "center",
  },
  bottomSheetContentContainer: {
    flex: 1,
    alignItems: "center",
    paddingVertical: 20,
    backgroundColor: "#FFF",
  },
});
