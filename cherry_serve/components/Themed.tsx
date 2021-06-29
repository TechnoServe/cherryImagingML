/**
 * Learn more about Light and Dark modes:
 * https://docs.expo.io/guides/color-schemes/
 */

import React from "react";
import {
  Text as DefaultText,
  View as DefaultView,
  Image as DefaultImage,
  TextInput as DefaultTextInput,
  TouchableOpacity as DefaultTouchableOpacity,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";

import Colors from "../constants/Colors";
import useColorScheme from "../hooks/useColorScheme";
import { IMAGES } from "../constants/images";

export function useThemeColor(
  props: { light?: string; dark?: string },
  colorName: keyof typeof Colors.light & keyof typeof Colors.dark
) {
  const theme = useColorScheme();
  const colorFromProps = props[theme];

  if (colorFromProps) {
    return colorFromProps;
  } else {
    return Colors[theme][colorName];
  }
}

type ThemeProps = {
  lightColor?: string;
  darkColor?: string;
};

type BaseButtonProps = {
  label: string;
  color: string;
};

type AssetProps = {
  name: string;
};

export type TextProps = ThemeProps & DefaultText["props"];
export type ViewProps = ThemeProps & DefaultView["props"];
export type TextInputProps = ThemeProps & DefaultTextInput["props"];
export type IoniconProps = ThemeProps & React.ComponentProps<typeof Ionicons>;
export type TouchableOpacityProps = ThemeProps &
  DefaultTouchableOpacity["props"];
export type ButtonProps = ThemeProps &
  DefaultTouchableOpacity["props"] &
  BaseButtonProps;
export type ImageProps = DefaultImage["props"] & AssetProps;

export function Text(props: TextProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const color = useThemeColor({ light: lightColor, dark: darkColor }, "text");

  return <DefaultText style={[{ color }, style]} {...otherProps} />;
}

export function View(props: ViewProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const backgroundColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "background"
  );

  return <DefaultView style={[{ backgroundColor }, style]} {...otherProps} />;
}

export function TextInput(props: TextInputProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const backgroundColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "background"
  );
  const color = useThemeColor({ light: lightColor, dark: darkColor }, "text");
  const borderColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "border"
  );
  const placeholderTextColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "placeholder"
  );

  const otherStyles = {
    color,
    borderColor,
    backgroundColor,
    borderRadius: 3,
    borderWidth: StyleSheet.hairlineWidth,
    width: "100%",
  };

  return (
    <DefaultTextInput
      style={[otherStyles, style]}
      placeholderTextColor={placeholderTextColor}
      {...otherProps}
    />
  );
}

export function Icon(props: IoniconProps) {
  return (
    <Ionicons color="white" size={36} style={{ marginBottom: -3 }} {...props} />
  );
}

export function TouchableOpacity(props: TouchableOpacityProps) {
  const { style, children, lightColor, darkColor, ...otherProps } = props;
  const borderColor = useThemeColor(
    { light: lightColor, dark: darkColor },
    "deepBorder"
  );

  const initialStyles = {
    borderColor,
    borderWidth: 2,
    alignItems: "center",
  };

  return (
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    <DefaultTouchableOpacity style={[initialStyles, style]} {...otherProps}>
      {children}
    </DefaultTouchableOpacity>
  );
}

export function ColoredButton(props: ButtonProps) {
  const { label, color, style, ...otherProps } = props;

  return (
    <TouchableOpacity
      {...otherProps}
      style={StyleSheet.flatten([style, { borderColor: color }])}
    >
      <Text style={{ color }}>{label}</Text>
    </TouchableOpacity>
  );
}

export function ThemedImage(props: ImageProps) {
  const { name, ...otherProps } = props;
  const theme = useColorScheme();
  const suffix = theme === "dark" ? "-white" : "";
  const imageName = name + suffix;
  return <DefaultImage {...otherProps} source={IMAGES[imageName]} />;
}
