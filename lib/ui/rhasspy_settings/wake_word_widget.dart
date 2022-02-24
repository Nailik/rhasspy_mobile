import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:porcupine_flutter/porcupine.dart';
import 'package:rhasspy_mobile/logic/options/wake_word_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';
import 'package:url_launcher/url_launcher.dart';

extension WakeWordWidget on CustomState {
  void permissionRequest(bool result) {}

  Widget wakeWord() {
    return autoSaveExpandableDropDownListItem(
        title: locale.wakeWord, option: WakeWordOptions(), setting: wakeWordSetting, child: localWakeWordSettings());
  }

  Widget localWakeWordSettings() {
    return Obx(
      () => Visibility(
        visible: wakeWordSetting.value == WakeWordOption.localPorcupine,
        child: Column(
          children: [
            const Divider(),
            autoSaveTextField(title: locale.porcupineAccessKey, setting: wakeWordAccessTokenSetting),
            const Divider(),
            MaterialButton(
              child: Text(locale.openPicoVoiceConsole),
              textColor: theme.colorScheme.tertiary,
              onPressed: () {
                launch("https://console.picovoice.ai/access_key");
              },
            ),
            const Divider(),
            localWakeWordKeyword(),
            const Divider(),
            localWakeWordSensitivity(),
          ],
        ),
      ),
    );
  }

  Widget localWakeWordKeyword() {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Expanded(
            child: Obx(
          () => DropdownButtonFormField2<BuiltInKeyword>(
            value: wakeWordNameOptionsSetting.value,
            onChanged: (BuiltInKeyword? newValue) {
              if (newValue != null) {
                wakeWordNameOptionsSetting.setValue(newValue);
              }
            },
            items: BuiltInKeyword.values.map<DropdownMenuItem<BuiltInKeyword>>((BuiltInKeyword value) {
              return DropdownMenuItem<BuiltInKeyword>(
                value: value,
                child: Text(value.toString()),
              );
            }).toList(),
          ),
        )),
        const VerticalDivider(),
        ElevatedButton(onPressed: () {}, child: Text(locale.refresh))
      ],
    );
  }

  Widget localWakeWordSensitivity() {
    return Obx(() => Column(children: <Widget>[
          Padding(
              padding: const EdgeInsets.fromLTRB(0, 16, 0, 0),
              child: Align(alignment: Alignment.centerLeft, child: Text("${locale.sensitivity} (${wakeWordSensitivitySetting.value.toString()})"))),
          SliderTheme(
            data: SliderThemeData(
                inactiveTrackColor: theme.colorScheme.secondary,
                trackHeight: 0.1,
                valueIndicatorShape: const PaddleSliderValueIndicatorShape(),
                thumbShape: const RoundSliderThumbShape()),
            child: Slider(
              value: wakeWordSensitivitySetting.value,
              max: 1,
              divisions: 100,
              label: wakeWordSensitivitySetting.toString(),
              onChanged: (double value) {
                wakeWordSensitivitySetting.setValue(value);
              },
            ),
          ),
        ]));
  }
}
