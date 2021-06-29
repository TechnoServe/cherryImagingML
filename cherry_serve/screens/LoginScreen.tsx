import React from "react";
import { StackScreenProps } from "@react-navigation/stack";
import { StyleSheet } from "react-native";
import { StatusBar } from "expo-status-bar";

import { View, Text, TextInput, TouchableOpacity } from "../components/Themed";
import { MonoText } from "../components/StyledText";
import { AuthParamList } from "../types";

export function LoginScreen({
  navigation,
}: StackScreenProps<AuthParamList, "Login">) {
  return (
    <View style={styles.container}>
      <StatusBar style="auto" />
      <Text style={styles.title}>Login</Text>
      <MonoText style={styles.description}>
        Login today and give us training data
      </MonoText>

      <View style={styles.content}>
        <Text style={styles.label}>Username</Text>
        <TextInput
          placeholder="Username"
          autoFocus={true}
          style={[styles.input]}
        />
        <Text style={styles.label}>Password</Text>
        <TextInput
          placeholder="Password"
          secureTextEntry
          style={[styles.input]}
        />
        <TouchableOpacity
          onPress={() => navigation.navigate("Inference")}
          style={styles.button}
        >
          <Text>Sign in</Text>
        </TouchableOpacity>
      </View>
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
  content: {
    width: "100%",
    padding: 16,
    alignItems: "flex-start",
  },
  input: {
    margin: 8,
    marginTop: 0,
    marginBottom: 16,
    padding: 10,
  },
  label: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 8,
    marginLeft: 8,
  },
  button: {
    margin: 8,
    marginTop: 32,
    alignSelf: "center",
    paddingHorizontal: 32,
    paddingVertical: 8,
    minWidth: 120,
  },
});
