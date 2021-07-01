import React from "react";
import { createStackNavigator } from "@react-navigation/stack";

import DummyScreen from "../components/DummyScreen";
import {
  InferenceParamList,
  SavedPredictionsParamList,
  AuthParamList,
  AppStackParamList,
} from "../types";
import { SavedPredictionsScreen } from "../screens/SavedPredictionsScreen";
import { SavedPredictionScreen } from "../screens/SavedPredictionScreen";
import { LoginScreen } from "../screens/LoginScreen";
import { MainCameraScreen } from "../screens/MainCameraScreen";
import { FinalPredictionScreen } from "../screens/FinalPredictionScreen";

const InferenceStack = createStackNavigator<InferenceParamList>();

export function InferenceNavigator() {
  return (
    <InferenceStack.Navigator>
      <InferenceStack.Screen
        name="Inference"
        component={MainCameraScreen}
        options={{ headerShown: false }}
      />
      <InferenceStack.Screen
        name="FinalPrediction"
        component={FinalPredictionScreen}
        options={{ headerShown: true, headerTitle: "Prediction" }}
      />
    </InferenceStack.Navigator>
  );
}

const SavedPredictionsStack = createStackNavigator<SavedPredictionsParamList>();

export function SavedPredictionsNavigator() {
  return (
    <SavedPredictionsStack.Navigator initialRouteName="SavedPredictions">
      <SavedPredictionsStack.Screen
        name="SavedPredictions"
        component={SavedPredictionsScreen}
        options={{ headerTitle: "Saved Predictions" }}
      />
      <SavedPredictionsStack.Screen
        name="SavedPrediction"
        component={SavedPredictionScreen}
        options={{ headerTitle: "Saved Prediction" }}
      />
    </SavedPredictionsStack.Navigator>
  );
}

const AuthStack = createStackNavigator<AuthParamList>();

export function AuthNavigator() {
  return (
    <AuthStack.Navigator>
      <AuthStack.Screen
        name="Login"
        component={LoginScreen}
        options={{ headerTitle: "Login" }}
      />
      <AuthStack.Screen
        name="AuthStatus"
        component={DummyScreen}
        options={{ headerTitle: "Profile" }}
      />
      <AuthStack.Screen
        name="ResetPassword"
        component={DummyScreen}
        options={{ headerTitle: "Reset password" }}
      />
    </AuthStack.Navigator>
  );
}

const AppStack = createStackNavigator<AppStackParamList>();

export function AppNavigator() {
  return (
    <AppStack.Navigator initialRouteName="Inference" headerMode="none">
      <AppStack.Screen
        name="Inference"
        component={InferenceNavigator}
        options={{ headerTitle: "Inference" }}
      />
      <AppStack.Screen
        name="SavedPredictions"
        component={SavedPredictionsNavigator}
        options={{ headerTitle: "Saved Predictions" }}
      />
      <AppStack.Screen
        name="Auth"
        component={AuthNavigator}
        options={{ headerTitle: "Auth" }}
      />
    </AppStack.Navigator>
  );
}
