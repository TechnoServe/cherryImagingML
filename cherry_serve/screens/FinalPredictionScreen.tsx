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
import * as ImageManipulator from "expo-image-manipulator";

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

  const runInference = async (inputImage) => {
    try {
      console.log(inputImage)
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
