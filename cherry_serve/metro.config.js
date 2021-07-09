/* eslint-disable @typescript-eslint/no-var-requires */
const { getDefaultConfig } = require("@expo/metro-config");

const defaultConfig = getDefaultConfig(__dirname);

defaultConfig.resolver.assetExts.push("bin");
defaultConfig.resolver.assetExts.push("tflite");

module.exports = defaultConfig;
