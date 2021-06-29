import React, { useState, useEffect, useRef } from "react";
import { Camera } from "expo-camera";
import * as ImagePicker from "expo-image-picker";
import { StackNavigationProp, StackScreenProps } from "@react-navigation/stack";
import {
  StyleSheet,
  Image,
  TouchableOpacity,
  ScrollView,
  Dimensions,
} from "react-native";
import { StatusBar } from "expo-status-bar";
import {
  CompositeNavigationProp,
  useNavigation,
} from "@react-navigation/native";

import {
  Text,
  View,
  Icon,
  ColoredButton,
  TouchableOpacity as ThemedButton,
  useThemeColor,
} from "../components/Themed";
import { InferenceParamList } from "../types";

const windowWidth = Dimensions.get("window").width;

type ConfirmationComponentProps = {
  lightColor?: string;
  darkColor?: string;
  image: string;
  setImage: (imgUri: string) => void;
};

function ConfirmationComponent({
  image,
  setImage,
  lightColor,
  darkColor,
}: ConfirmationComponentProps) {
  const iconColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "text"
  );

  type SavedPredictionScreenNavigationProp = CompositeNavigationProp<
    StackNavigationProp<InferenceParamList, "FinalPrediction">,
    StackNavigationProp<InferenceParamList>
  >;

  const navigation = useNavigation<SavedPredictionScreenNavigationProp>();

  const navigateToFinalPredictionScreen = () => {
    navigation.replace("FinalPrediction", { data: image });
  };

  return (
    <View style={[styles.confirmationContainer]}>
      <StatusBar style="auto" hideTransitionAnimation="fade" />
      <TouchableOpacity
        style={styles.topRightButton}
        onPress={() => setImage("")}
      >
        <Icon
          name="ios-close-outline"
          color={iconColor}
          style={styles.cameraScreenBottomIcon}
        />
      </TouchableOpacity>
      <ScrollView style={styles.scrollViewContainer}>
        <Image source={{ uri: image }} style={styles.confirmationImage} />
        <View style={styles.confirmationBox}>
          <Text style={styles.confirmationText}>
            Image successfully retrieved.
          </Text>
          <Text style={styles.confirmationText}>Proceed with prediction?</Text>
          <View style={styles.confirmationButtons}>
            <ColoredButton
              color="red"
              label="No, Cancel"
              style={[styles.button, { marginRight: 8 }]}
              onPress={() => setImage("")}
            />
            <ThemedButton
              onPress={navigateToFinalPredictionScreen}
              style={[styles.button, { marginLeft: 8 }]}
            >
              <Text>Yes, Proceed</Text>
            </ThemedButton>
          </View>
        </View>
      </ScrollView>
    </View>
  );
}

export function MainCameraScreen({
  navigation,
}: StackScreenProps<InferenceParamList, "MainCamera">) {
  const [image, setImage] = useState<string>("");
  const [hasPermission, setHasPermission] = useState<boolean | null>(null);
  const type = Camera.Constants.Type.back;

  const cameraRef = useRef<Camera>();

  const captureImage = async () => {
    // if (cameraRef.current) {
    //   const { uri } = await cameraRef.current.takePictureAsync({
    //     skipProcessing: true,
    //   });
    //   setImage(uri);
    // }
    const result = await ImagePicker.launchCameraAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 1,
    });

    if (!result.cancelled) {
      setImage(result.uri);
    }
  };

  useEffect(() => {
    (async () => {
      const { status } = await Camera.getPermissionsAsync();
      // const { status } = await Camera.requestPermissionsAsync();
      setHasPermission(status === "granted");
    })();
  }, []);

  if (hasPermission === null) {
    return <View />;
  }
  if (hasPermission === false) {
    return (
      <View style={styles.container}>
        <StatusBar style="light" hideTransitionAnimation="fade" />
        <Text style={styles.title}>No access to camera</Text>
      </View>
    );
  }

  const pickImage = async () => {
    (async () => {
      const { status } =
        await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (status !== "granted") {
        // eslint-disable-next-line no-alert
        alert("Sorry, we need camera roll permissions to make this work!");
      }
    })();
    const result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: true,
      aspect: [1, 1],
      quality: 1,
    });

    if (!result.cancelled) {
      setImage(result.uri);
    }
  };

  if (image !== "") {
    return <ConfirmationComponent {...{ image, setImage }} />;
  }

  return (
    <View style={styles.container}>
      <StatusBar style="light" hideTransitionAnimation="fade" />
      <Camera
        ref={(cam: Camera) => (cameraRef.current = cam)}
        ratio={"1:1"}
        useCamera2Api
        style={styles.camera}
        type={type}
      >
        {/* <View style={styles.captureGuide} /> */}
      </Camera>
      <View style={styles.buttonContainer}>
        <TouchableOpacity
          style={styles.galleryButton}
          onPress={() => pickImage()}
        >
          <Icon
            name="ios-images-outline"
            style={styles.cameraScreenBottomIcon}
          />
        </TouchableOpacity>
        <TouchableOpacity onPress={captureImage} style={styles.captureButton} />
        <TouchableOpacity
          style={styles.flipButton}
          onPress={() => navigation.navigate("SavedPredictions")}
        >
          <Icon name="ios-sync" style={styles.cameraScreenBottomIcon} />
        </TouchableOpacity>
      </View>
      <TouchableOpacity
        style={styles.topRightButton}
        onPress={() => navigation.navigate("Auth")}
      >
        <Icon name="ios-person-circle" style={styles.cameraScreenBottomIcon} />
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "black",
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
  },
  camera: {
    width: windowWidth,
    height: windowWidth,
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  },
  buttonContainer: {
    position: "absolute",
    right: 0,
    bottom: 0,
    left: 0,
    height: 100,
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "flex-end",
    backgroundColor: "transparent",
    paddingBottom: 30,
    paddingHorizontal: 30,
  },
  flipButton: {},
  captureButton: {
    width: 60,
    height: 60,
    backgroundColor: "rgba(255, 255, 255, 0.81)",
    borderRadius: 30,
    borderWidth: 3,
    borderStyle: "solid",
    borderColor: "#FFFFFF",
  },
  galleryButton: {},
  topRightButton: {
    position: "absolute",
    top: 60,
    right: 24,
    zIndex: 2,
  },
  cameraScreenBottomIcon: {
    marginBottom: 5,
  },
  captureGuide: {
    borderWidth: 1,
    borderColor: "rgba(255, 255, 255, 0.3)",
    width: windowWidth * 0.7,
    height: windowWidth * 0.7,
    backgroundColor: "transparent",
  },
  confirmationContainer: {
    flex: 1,
    alignItems: "center",
  },
  scrollViewContainer: {
    width: "100%",
  },
  confirmationBox: {},
  confirmationImage: {
    width: windowWidth * 0.9,
    height: windowWidth * 0.9,
    marginTop: windowWidth * 0.4,
    alignSelf: "center",
    resizeMode: "center",
    marginBottom: 32,
  },
  confirmationText: {
    width: "100%",
    marginTop: 12,
    paddingHorizontal: windowWidth * 0.1,
    fontSize: 20,
    textAlign: "center",
  },
  confirmationButtons: {
    marginTop: 32,
    flexDirection: "row",
    justifyContent: "center",
  },
  button: {
    paddingHorizontal: 32,
    paddingVertical: 8,
    minWidth: 120,
    alignItems: "center",
  },
});
