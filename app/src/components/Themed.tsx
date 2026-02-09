/**
 * Learn more about Light and Dark modes:
 * https://docs.expo.io/guides/color-schemes/
 */

import { ScrollView as DefaultScrollView, Text as DefaultText, TextInput as DefaultTextInput, View as DefaultView, useColorScheme } from 'react-native';

import Colors from '@app/constants/Colors';

type ThemeProps = {
  lightColor?: string;
  darkColor?: string;
};

export type ThemeColor = keyof typeof Colors.light & keyof typeof Colors.dark;

export type TextProps = ThemeProps & DefaultText['props'];
export type ViewProps = ThemeProps & DefaultView['props'];
export type TextInputProps = ThemeProps & DefaultTextInput['props'];

export function useThemeColor(
  colorName: ThemeColor,
  props?: { light?: string; dark?: string },
) {
  const theme = useColorScheme() ?? 'light';
  const colorFromProps = props && props[theme];

  if (colorFromProps) {
    return colorFromProps;
  } else {
    return Colors[theme][colorName];
  }
}

export function Text(props: TextProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const color = useThemeColor('text', { light: lightColor, dark: darkColor });

  return <DefaultText style={[{ color }, style]} {...otherProps} />;
}

export function View(props: ViewProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const backgroundColor = useThemeColor('background', { light: lightColor, dark: darkColor });

  return <DefaultView style={[{ backgroundColor }, style]} {...otherProps} />;
}

export function ScrollView(props: ViewProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const backgroundColor = useThemeColor('background', { light: lightColor, dark: darkColor });

  return <DefaultScrollView style={[{ backgroundColor }, style]} {...otherProps} />;
}

export function TextInput(props: TextInputProps) {
  const { style, lightColor, darkColor, ...otherProps } = props;
  const backgroundColor = useThemeColor('background', { light: lightColor, dark: darkColor });
  const color = useThemeColor('text', { light: lightColor, dark: darkColor });

  return <DefaultTextInput style={[{ backgroundColor, color }, style]} {...otherProps} />;
}
