import React from "react";
import { StackScreenProps } from "@react-navigation/stack";
import { StyleSheet, TouchableOpacity } from "react-native";

import { View, Text } from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { InferenceParamList } from "../types";

export function InferenceScreen({
  navigation,
}: StackScreenProps<InferenceParamList, "Inference">) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Inference Screen</Text>
      <MonoText style={styles.description}>
        Where we load an image and generate a mask with a GAN
      </MonoText>
      <TouchableOpacity
        onPress={() => navigation.push("Auth")}
        style={styles.link}
      >
        <Text style={styles.linkText}>Go to Auth</Text>
      </TouchableOpacity>
      <TouchableOpacity
        onPress={() => navigation.push("SavedPredictions")}
        style={styles.link}
      >
        <Text style={styles.linkText}>Go to Saved Predictions</Text>
      </TouchableOpacity>
      <TouchableOpacity
        onPress={() => navigation.push("MainCamera")}
        style={styles.link}
      >
        <Text style={styles.linkText}>Open Main Screen</Text>
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
  link: {
    marginTop: 15,
    paddingVertical: 15,
  },
  linkText: {
    fontSize: 14,
    color: "#2e78b7",
  },
});
