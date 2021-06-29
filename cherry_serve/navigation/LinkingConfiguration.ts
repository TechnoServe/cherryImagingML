/**
 * Learn more about deep linking with React Navigation
 * https://reactnavigation.org/docs/deep-linking
 * https://reactnavigation.org/docs/configuring-links
 */

import * as Linking from "expo-linking";

const LinkingConfig = {
  prefixes: [Linking.makeUrl("/")],
  config: {
    screens: {
      Root: {
        screens: {
          Inference: {
            screens: {
              InferenceScreen: "inference",
            },
          },
          SavedPredictions: {
            screens: {
              SavedPredictionsScreen: "savedpredictions",
            },
          },
        },
      },
      NotFound: "*",
    },
  },
};

export default LinkingConfig;
