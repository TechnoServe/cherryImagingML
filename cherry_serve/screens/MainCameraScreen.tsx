import React, { useState, useEffect, useRef } from "react";
import { Camera } from "expo-camera";
import * as ImagePicker from "expo-image-picker";
import { StackNavigationProp, StackScreenProps } from "@react-navigation/stack";
import {
  StyleSheet,
  Image,
  Alert,
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
import { MonoText } from "../components/StyledText";
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
    navigation.push("FinalPrediction", { data: image });
    setImage("");
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
    if (cameraRef.current) {
      const capturedImage = await cameraRef.current.takePictureAsync({
        skipProcessing: true,
      });
      setImage(capturedImage.uri);
    }
    // const result = await ImagePicker.launchCameraAsync({
    //   mediaTypes: ImagePicker.MediaTypeOptions.Images,
    //   allowsEditing: true,
    //   aspect: [1, 1],
    //   quality: 1,
    // });

    // if (!result.cancelled) {
    //   setImage(result.uri);
    // }
  };

  const getCameraPermissions = async () => {
    const { status: initialStatus } = await Camera.getPermissionsAsync();
    if (initialStatus !== "granted") {
      const { status } = await Camera.requestPermissionsAsync();
      setHasPermission(status === "granted");
    }
    setHasPermission(initialStatus === "granted");
  };

  useEffect(() => {
    (async () => {
      await getCameraPermissions();
    })();
  }, []);

  if (hasPermission === null) {
    return <View />;
  }

  if (hasPermission === false) {
    return (
      <View style={styles.plainContainer}>
        <StatusBar style="auto" />
        <Text style={styles.title}>No access to camera</Text>
        <MonoText
          style={{
            textAlign: "center",
            margin: 48,
          }}
        >
          We need access to your camera for this to work.
        </MonoText>
        <ThemedButton style={styles.button} onPress={getCameraPermissions}>
          <Text>Grant permissions</Text>
        </ThemedButton>
      </View>
    );
  }

  const pickImage = async () => {
    const { status: initialStatus } =
      await ImagePicker.getMediaLibraryPermissionsAsync();
    if (initialStatus !== "granted") {
      const { status } =
        await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (status !== "granted") {
        Alert.alert(
          "No access to gallery",
          "Sorry, we need camera roll permissions to make this work!"
        );
        return;
      }
    }
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
      />
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
  plainContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
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
    alignItems: "center",
    backgroundColor: "transparent",
    paddingBottom: 30,
    paddingHorizontal: 30,
  },
  captureButton: {
    width: 60,
    height: 60,
    backgroundColor: "rgba(255, 255, 255, 0.81)",
    borderRadius: 30,
    borderWidth: 3,
    borderStyle: "solid",
    borderColor: "#FFFFFF",
  },
  flipButton: {
    width: 60,
    height: 60,
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
  },
  galleryButton: {
    width: 60,
    height: 60,
    display: "flex",
    justifyContent: "flex-end",
    alignItems: "center",
  },
  topRightButton: {
    position: "absolute",
    top: 60,
    right: 24,
    zIndex: 2,
  },
  cameraScreenBottomIcon: {
    marginBottom: 5,
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
