import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/data/language_options.dart';
import 'package:rhasspy_mobile/screens/custom_state.dart';

import '../data/theme_options.dart';

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
      showLogWidget()
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
    var themeOption = ThemeOption.system.obs;
    return Padding(
        padding: const EdgeInsets.all(8),
        child: dropDownListItem(ThemeOptions(), themeOption, onChanged: (ThemeOption? theme) {
          if (theme != null) {
            Get.changeThemeMode(ThemeOptions.asThemeMode(theme));
          }
        }));
  }

  Widget languageDropDown() {
    var languageOption = LanguageOption.en.obs;
    return Padding(
        padding: const EdgeInsets.all(8),
        child: dropDownListItem(LanguageOptions(), languageOption, onChanged: (LanguageOption? language) {
          if (language != null) {
            Get.updateLocale(LanguageOptions.asLocale(language));
          }
        }));
  }

  Widget silenceDetection() {
    var silenceDetection = false.obs;
    return Obx(() => SwitchListTile(
        value: silenceDetection.value,
        onChanged: (value) {
          silenceDetection.value = value;
        },
        title: Text(locale.automaticSilenceDetection)));
  }

  Widget backgroundWakeWordDetection() {
    var backgroundWakeWordDetection = false.obs;
    var backgroundWakeWordDetectionTurnOnDisplay = false.obs;
    return ExpansionTile(
        title: Text(locale.backgroundWakeWordDetection),
        subtitle: Obx(() => Text(backgroundWakeWordDetection.value ? locale.enabled : locale.disabled)),
        backgroundColor: theme.colorScheme.surfaceVariant,
        textColor: theme.colorScheme.onSurfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: [
          Obx(() => SwitchListTile(
              title: Text(locale.enableBackgroundWakeWordDetection),
              value: backgroundWakeWordDetection.value,
              onChanged: (value) {
                backgroundWakeWordDetection.value = value;
              })),
          Obx(() => SwitchListTile(
              title: Text(locale.backgroundWakeWordDetectionTurnOnDisplay),
              value: backgroundWakeWordDetectionTurnOnDisplay.value,
              onChanged: (value) {
                backgroundWakeWordDetectionTurnOnDisplay.value = value;
              })),
        ]);
  }

  var wakeWordSoundIndication = false.obs;
  var wakeWordLightIndication = false.obs;

  Widget backgroundIndication() {
    return ExpansionTile(
        title: Text(locale.wakeWordIndication),
        subtitle: Obx(() => backgroundSubtitle()),
        backgroundColor: theme.colorScheme.surfaceVariant,
        textColor: theme.colorScheme.onSurfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: [
          Obx(() => SwitchListTile(
              title: Text(locale.wakeWordSoundIndication),
              value: wakeWordSoundIndication.value,
              onChanged: (value) {
                wakeWordSoundIndication.value = value;
              })),
          Obx(() => SwitchListTile(
              title: Text(locale.wakeWordLightIndication),
              value: wakeWordLightIndication.value,
              onChanged: (value) {
                wakeWordLightIndication.value = value;
              })),
          Obx(() => SwitchListTile(
              title: Text(locale.showLog),
              value: showLog.value,
              onChanged: (value) {
                showLog.value = value;
              })),
        ]);
  }

  Widget backgroundSubtitle() {
    String text = "";

    if (wakeWordSoundIndication.value) {
      text += locale.sound;
    }
    if (wakeWordLightIndication.value) {
      if (text.isNotEmpty) {
        text += locale.and;
      }
      text += locale.light;
    }
    if (text.isEmpty) {
      text = locale.disabled;
    }

    return Text(text);
  }

  Widget showLogWidget() {
    return Obx(() => SwitchListTile(
        value: showLog.value,
        onChanged: (value) {
          showLog.value = value;
          setState(() {});
        },
        title: Text(locale.showLog)));
  }
}

//ssl + certificate
//mqtt ssl + certificate
