import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/wake_word_options.dart';
import '../rhasspy_settings_screen.dart';

extension WakeWordWidget on RhasspySettingsScreenState {
  Widget wakeWord() {
    var wakeWordOption = WakeWordOption.disabled.obs;
    return Obx(() => listItem(WakeWordOptions(), wakeWordOption, locale.wakeWord, localWakeWordSettings(wakeWordOption.value)));
  }

  Widget localWakeWordSettings(WakeWordOption wakeWordOption) {
    if (wakeWordOption == WakeWordOption.localPorcupine) {
      return Column(children: [const Divider(), localWakeWordKeyword(), const Divider(), localWakeWordSensitivity()]);
    } else {
      return Container();
    }
  }

  Widget localWakeWordKeyword() {
    var wakeWordKeywordOption = "jarvis".obs;
    var wakeWordKeywordOptions = ["jarvis", "porcupine"];

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Expanded(
            child: Obx(
          () => DropdownButtonFormField2<String>(
            value: wakeWordKeywordOption.value,
            onChanged: (String? newValue) {
              if (newValue != null) {
                wakeWordKeywordOption.value = newValue;
              }
            },
            items: wakeWordKeywordOptions.map<DropdownMenuItem<String>>((String value) {
              return DropdownMenuItem<String>(
                value: value,
                child: Text(value),
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
    var wakeWordSensitivity = 0.55.obs;

    return Obx(() => Column(children: <Widget>[
          Padding(padding: const EdgeInsets.fromLTRB(0, 16, 0, 0), child: Align(alignment: Alignment.centerLeft, child: Text("${locale.sensitivity} (${wakeWordSensitivity.value.toString()})"))),
          SliderTheme(
            data: SliderThemeData(
                inactiveTrackColor: theme.colorScheme.secondary, trackHeight: 0.1, valueIndicatorShape: const PaddleSliderValueIndicatorShape(), thumbShape: const RoundSliderThumbShape()),
            child: Slider(
              value: wakeWordSensitivity.value,
              max: 1,
              divisions: 100,
              label: wakeWordSensitivity.toString(),
              onChanged: (double value) {
                wakeWordSensitivity.value = value;
              },
            ),
          ),
        ]));
  }
}
