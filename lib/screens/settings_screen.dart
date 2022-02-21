import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/data/language_options.dart';
import 'package:rhasspy_mobile/main.dart';
import 'package:rhasspy_mobile/screens/custom_state.dart';

import '../data/theme_options.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({Key? key}) : super(key: key);

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends CustomState<SettingsScreen> {
  @override
  Widget content() {
    final List<Widget> items = <Widget>[languageDropDown(), themeDropDown()];

    return ListView.separated(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        return items[index];
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }

  Widget themeDropDown() {
    var themeOption = ThemeOption.system.obs;
    return expandableDropDownListItem(ThemeOptions(), themeOption, locale.theme, onChanged: (ThemeOption? theme) {
      if (theme != null) {
        themeMode.value = ThemeOptions.asThemeMode(theme);
      }
    });
  }

  Widget languageDropDown() {
    var languageOption = LanguageOption.en.obs;
    return expandableDropDownListItem(LanguageOptions(), languageOption, locale.language, onChanged: (LanguageOption? language) {
      if (language != null) {
        Get.updateLocale(LanguageOptions.asLocale(language));
      }
    });
  }
}
