import React, { useEffect } from "react";
import { StackScreenProps } from "@react-navigation/stack";
import { StyleSheet, Image, NativeModules } from "react-native";
import * as FileSystem from "expo-file-system";
import * as ImageManipulator from "expo-image-manipulator";

const { Pix2PixModule } = NativeModules;

import { View, Text, TouchableOpacity } from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { InferenceParamList } from "../types";

export function SandboxScreen({
  navigation,
}: StackScreenProps<InferenceParamList, "Inference">) {
  const runSegmentation = async () => {
    try {
      const result = await Pix2PixModule.execute("Cherry Images", "image_url");
      console.log(result);
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
