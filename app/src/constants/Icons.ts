import { FontAwesome, MaterialCommunityIcons } from "@expo/vector-icons";
import { Icon } from "@expo/vector-icons/build/createIconSet";

export type FontAwesomeIconName = typeof FontAwesome extends Icon<infer G, any>
  ? G
  : never;

export type MaterialCommunityIconName =
  typeof MaterialCommunityIcons extends Icon<infer G, any> ? G : never;
