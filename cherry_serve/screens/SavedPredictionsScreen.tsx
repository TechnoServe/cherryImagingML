import React, { useState } from "react";
import { StackNavigationProp } from "@react-navigation/stack";
import {
  useNavigation,
  CompositeNavigationProp,
} from "@react-navigation/native";
import { StyleSheet, Image, FlatList } from "react-native";
import { StatusBar } from "expo-status-bar";
import Checkbox from "expo-checkbox";

import {
  View,
  Text,
  TouchableOpacity,
  ColoredButton,
  ThemedImage,
} from "../components/Themed";
import {
  SavedPredictionsParamList,
  SavedPredictionParamList,
  PredictionResult,
} from "../types";
import { MonoText } from "../components/StyledText";

function parseDate(date: Date) {
  const [year, other] = new Date(date).toISOString().split("T");
  const [time, secs] = other.split(".");
  let sumTime = time.split(":").reduce((acc, x) => acc + Number(x), 0);
  sumTime += Number(secs.substr(0, 3));
  return `${year}:${sumTime}:`;
}

const NoSavedPredictions = ({ populateList }: { populateList: () => void }) => (
  <View style={styles.container}>
    <StatusBar style="auto" />
    <View style={styles.center}>
      <ThemedImage source={0} style={styles.cherryIcon} name="cherry" />
      <Text style={styles.tagline}>No saved predictions</Text>

      <TouchableOpacity
        style={{
          paddingHorizontal: 32,
          paddingVertical: 12,
          minWidth: 120,
        }}
        onPress={populateList}
      >
        <Text>Create test predictions</Text>
      </TouchableOpacity>
    </View>
  </View>
);

const SavedpredictionsHeader = ({ selectAll, setSelectAll }) => {
  return (
    <View style={styles.row}>
      <TouchableOpacity
        onPress={() => setSelectAll(!selectAll)}
        style={styles.checkboxWrapper}
      >
        <Checkbox
          accessibilityLabel="Select All"
          style={[styles.checkbox, { marginRight: 16 }]}
          value={selectAll}
          onValueChange={setSelectAll}
          color={selectAll ? "#4630EB" : "#ccc"}
        />
        <Text>Select All</Text>
      </TouchableOpacity>
      <View style={styles.buttonWrapper}>
        <TouchableOpacity style={styles.button}>
          <Text>Sync All</Text>
        </TouchableOpacity>
        <ColoredButton color="red" label="Delete All" style={styles.button} />
      </View>
    </View>
  );
};

const SavedPredictionListItem = ({ data }) => {
  const [checked, setChecked] = useState<boolean>(false);

  type SavedPredictionScreenNavigationProp = CompositeNavigationProp<
    StackNavigationProp<SavedPredictionsParamList, "SavedPrediction">,
    StackNavigationProp<SavedPredictionParamList>
  >;

  const navigation = useNavigation<SavedPredictionScreenNavigationProp>();

  const navigateToDetailScreen = () => {
    navigation.push("SavedPrediction", { data });
  };

  return (
    <View>
      <MonoText>{parseDate(data.createdAt) + data.scores[0]}</MonoText>
      <View style={styles.row}>
        <View style={{ flexDirection: "row" }}>
          <TouchableOpacity style={styles.checkboxWrapper}>
            <Checkbox
              accessibilityLabel="Select All"
              style={styles.checkbox}
              value={checked}
              onValueChange={setChecked}
              color={checked ? "#4630EB" : "#ccc"}
            />
          </TouchableOpacity>
          <TouchableOpacity
            onPress={navigateToDetailScreen}
            style={styles.imageWrapperWithText}
          >
            <View style={styles.imageWrapper}>
              <Image style={styles.predictedImage} source={data.original} />
              <Image
                style={[styles.predictedImage, { marginLeft: -2 }]}
                source={data.mask}
              />
            </View>
          </TouchableOpacity>
        </View>
        <View>
          <TouchableOpacity style={[styles.button, styles.listItemButton]}>
            <Text style={styles.listItemButtonText}>Sync</Text>
          </TouchableOpacity>
          <ColoredButton
            label="Delete"
            color="red"
            style={[styles.button, styles.listItemButton]}
          />
        </View>
      </View>
    </View>
  );
};

const SamplePredictionListItem: PredictionResult = {
  original: require("../assets/images/cherry.png"),
  mask: require("../assets/images/cherry.png"),
  scores: [80, 15, 0],
  synced: false,
  state: "unsynced",
  createdAt: new Date().getTime(),
  syncedAt: new Date().getTime(),
};

export function SavedPredictionsScreen() {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [predictions, setPredictions] = useState(
    Array(0).fill(SamplePredictionListItem)
  );
  const [selectAll, setSelectAll] = useState<boolean>(false);

  if (predictions.length === 0) {
    return (
      <NoSavedPredictions
        populateList={() =>
          setPredictions(Array(10).fill(SamplePredictionListItem))
        }
      />
    );
  }

  return (
    <View style={styles.container}>
      <StatusBar style="auto" />

      <FlatList
        style={styles.flatList}
        data={predictions}
        stickyHeaderIndices={[0]}
        showsVerticalScrollIndicator={false}
        keyExtractor={(item, index) => index + ""}
        renderItem={() => <SavedPredictionListItem data={predictions[0]} />}
        ListHeaderComponent={
          <SavedpredictionsHeader {...{ selectAll, setSelectAll }} />
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "flex-start",
    alignItems: "flex-start",
    padding: 20,
  },
  flatList: {
    flex: 1,
    width: "100%",
  },
  center: {
    flex: 1,
    width: "100%",
    justifyContent: "center",
  },
  cherryIcon: {
    width: "100%",
    height: 180,
    resizeMode: "center",
  },
  tagline: {
    fontSize: 20,
    fontWeight: "bold",
    marginBottom: 100,
    textAlign: "center",
  },
  row: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingTop: 16,
    paddingBottom: 36,
    width: "100%",
    // backgroundColor: "#FFF",
  },
  checkboxWrapper: {
    display: "flex",
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "flex-start",
    paddingVertical: 4,
    paddingRight: 16,
    borderWidth: 0,
  },
  checkbox: {},
  buttonWrapper: {
    flexDirection: "row",
  },
  button: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    marginLeft: 16,
  },
  imageWrapperWithText: {
    borderWidth: 0,
  },
  imageWrapper: {
    flexDirection: "row",
    borderWidth: 0,
  },
  predictedImage: {
    width: 104,
    height: 104,
    resizeMode: "center",
    borderWidth: 2,
    borderColor: "#000",
  },
  listItemButton: {
    marginVertical: 4,
  },
  listItemButtonText: {
    textAlign: "center",
  },
});
