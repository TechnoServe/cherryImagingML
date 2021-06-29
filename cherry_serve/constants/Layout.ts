import { Dimensions } from "react-native";

const { width } = Dimensions.get("window");
const { height } = Dimensions.get("window");

const Layout = {
  window: {
    width,
    height,
  },
  isSmallDevice: width < 375,
};

export default Layout;
