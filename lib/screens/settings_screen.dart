import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/data/language_options.dart';
import 'package:rhasspy_mobile/screens/custom_state.dart';

import '../data/theme_options.dart';
import '../settings/settings.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({Key? key}) : super(key: key);

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

var showLog = true.obs;

class _SettingsScreenState extends CustomState<SettingsScreen> {
  @override
  Widget content() {
    final List<Widget> items = <Widget>[
      languageDropDown(),
      themeDropDown(),
      silenceDetection(),
      backgroundWakeWordDetection(),
      backgroundIndication(),
      //showLog
      autoSaveSwitchTile(title: locale.showLog, setting: showLogSetting)
    ];

    return ListView.separated(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        return items[index];
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }

  Widget themeDropDown() {
    return Padding(
        padding: const EdgeInsets.all(8),
        child: autoSaveDropDownListItem(
            option: ThemeOptions(),
            setting: themeSetting,
            onChanged: () {
              Get.changeThemeMode(ThemeOptions.asThemeMode(themeSetting.value));
            }));
  }

  Widget languageDropDown() {
    return Padding(
        padding: const EdgeInsets.all(8),
        child: autoSaveDropDownListItem(
            option: LanguageOptions(),
            setting: languageSetting,
            onChanged: () {
              Get.updateLocale(LanguageOptions.asLocale(languageSetting.value));
            }));
  }

  Widget silenceDetection() {
    return autoSaveSwitchTile(title: locale.automaticSilenceDetection, setting: automaticSilenceDetectionSetting);
  }

  Widget backgroundWakeWordDetection() {
    return ExpansionTile(
        title: Text(locale.backgroundWakeWordDetection),
        subtitle: Obx(() => Text(backgroundWakeWordDetectionSetting.value ? locale.enabled : locale.disabled)),
        backgroundColor: theme.colorScheme.surfaceVariant,
        textColor: theme.colorScheme.onSurfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: [
          autoSaveSwitchTile(title: locale.enableBackgroundWakeWordDetection, setting: backgroundWakeWordDetectionSetting),
          autoSaveSwitchTile(title: locale.backgroundWakeWordDetectionTurnOnDisplay, setting: wakeUpDisplaySetting),
        ]);
  }

  Widget backgroundIndication() {
    return ExpansionTile(
        title: Text(locale.wakeWordIndication),
        subtitle: Obx(() => backgroundSubtitle()),
        backgroundColor: theme.colorScheme.surfaceVariant,
        textColor: theme.colorScheme.onSurfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: [
          autoSaveSwitchTile(title: locale.wakeWordSoundIndication, setting: wakeWordIndicationSoundSetting),
          autoSaveSwitchTile(title: locale.wakeWordLightIndication, setting: wakeWordIndicationVisualSetting),
        ]);
  }

  Widget backgroundSubtitle() {
    String text = "";

    if (wakeWordIndicationSoundSetting.value) {
      text += locale.sound;
    }
    if (wakeWordIndicationVisualSetting.value) {
      if (text.isNotEmpty) {
        text += " " + locale.and + " ";
      }
      text += locale.light;
    }
    if (text.isEmpty) {
      text = locale.disabled;
    }

    return Text(text);
  }
}

//ssl + certificate
//mqtt ssl + certificate
