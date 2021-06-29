import React from "react";
import { StackScreenProps } from "@react-navigation/stack";
import { StyleSheet } from "react-native";

import { View, Text, TouchableOpacity } from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { AuthParamList } from "../types";

// Check if user is logged in
// If not, redirect to login

export function AuthStatusScreen({
  navigation,
}: StackScreenProps<AuthParamList, "AuthStatus">) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>
        You can sync photos because you're logged in as
      </Text>
      <MonoText style={styles.description}>XYZ</MonoText>
      <TouchableOpacity
        onPress={() => navigation.replace("Inference")}
        style={styles.link}
      >
        <Text style={styles.linkText}>Logout</Text>
      </TouchableOpacity>

      <MonoText style={styles.description}>
        If you're not actually logged in, this page will never get rendered and
        you'll be taken to the login screen
      </MonoText>
      <TouchableOpacity
        onPress={() => navigation.replace("Login")}
        style={styles.link}
      >
        <Text style={styles.linkText}>Go to Login Screen</Text>
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
    marginBottom: 32,
    paddingHorizontal: 32,
    paddingVertical: 8,
    minWidth: 120,
  },
  linkText: {
    fontSize: 14,
    color: "#2e78b7",
  },
});
