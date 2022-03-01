import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/logic/options/intent_recognition_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';

extension IntentRecognitionWidget on CustomState {
  Widget intentRecognition() {
    return autoSaveExpandableDropDownListItem(
        title: locale.intentRecognition,
        option: IntentRecognitionOptions(),
        setting: intentRecognitionSetting,
        child: Obx(() => intentRecognitionSettings(intentRecognitionSetting.value)));
  }

  Widget intentRecognitionSettings(IntentRecognitionOption intentRecognitionOption) {
    if (intentRecognitionOption == IntentRecognitionOption.remoteHTTP) {
      return Column(children: [const Divider(), autoSaveTextField(title: locale.rhasspyTextToIntentURL, setting: intentRecognitionHTTPURLSetting)]);
    } else {
      return Container();
    }
  }
}
