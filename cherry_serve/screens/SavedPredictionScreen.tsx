import React from "react";
import { RouteProp } from "@react-navigation/native";
import { StyleSheet, ScrollView } from "react-native";

import {
  View,
  Text,
  TouchableOpacity,
  ColoredButton,
  ThemedImage,
} from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { SavedPredictionsParamList } from "../types";

function parseDate(date: Date) {
  const [year, other] = new Date(date).toISOString().split("T");
  const [time, secs] = other.split(".");
  let sumTime = time.split(":").reduce((acc, x) => acc + Number(x), 0);
  sumTime += Number(secs.substr(0, 3));
  return `${year}:${sumTime}:`;
}

export function SavedPredictionScreen({
  params,
}: {
  params: RouteProp<SavedPredictionsParamList, "SavedPrediction">;
}) {
  return (
    <View style={styles.container}>
      <ScrollView style={styles.scrollViewContainer}>
        <MonoText>{parseDate(new Date()) + [200]}</MonoText>
        <View style={styles.imageWrapper}>
          <ThemedImage source={0} style={styles.image} name="cherry" />
          <ThemedImage source={0} style={styles.image} name="cherry" />
        </View>
        <Text>{JSON.stringify(params)}</Text>
        <View style={styles.buttonWrapper}>
          <ColoredButton
            color="red"
            label="Delete"
            style={[styles.button, { marginRight: 8 }]}
          />
          <TouchableOpacity style={[styles.button, { marginLeft: 8 }]}>
            <Text>Sync</Text>
          </TouchableOpacity>
        </View>
      </ScrollView>
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
});
