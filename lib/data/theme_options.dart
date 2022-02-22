import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class ThemeOptions extends Option<ThemeOption> {
  static final ThemeOptions _singleton = ThemeOptions._internal();

  factory ThemeOptions() {
    return _singleton;
  }

  ThemeOptions._internal() : super(ThemeOption.values, ThemeOption.system);

  @override
  String asText(ThemeOption option, AppLocalizations local) {
    switch (option) {
      case ThemeOption.dark:
        return local.darkTheme;
      case ThemeOption.light:
        return local.lightTheme;
      case ThemeOption.system:
        return local.systemTheme;
    }
  }

  static ThemeMode asThemeMode(ThemeOption option) {
    switch (option) {
      case ThemeOption.dark:
        return ThemeMode.dark;
      case ThemeOption.light:
        return ThemeMode.light;
      case ThemeOption.system:
        return ThemeMode.system;
    }
  }
}

enum ThemeOption { dark, light, system }
