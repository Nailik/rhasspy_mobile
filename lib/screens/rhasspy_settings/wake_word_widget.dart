import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:porcupine_flutter/porcupine.dart';

import '../../data/wake_word_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

extension WakeWordWidget on CustomState {
  Widget wakeWord() {
    return autoSaveExpandableDropDownListItem(
        title: locale.wakeWord, option: WakeWordOptions(), setting: wakeWordSetting, child: Obx(() => localWakeWordSettings(wakeWordSetting.value)));
  }

  Widget localWakeWordSettings(WakeWordOption wakeWordOption) {
    if (wakeWordOption == WakeWordOption.localPorcupine) {
      return Column(children: [const Divider(), localWakeWordKeyword(), const Divider(), localWakeWordSensitivity()]);
    } else {
      return Container();
    }
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
