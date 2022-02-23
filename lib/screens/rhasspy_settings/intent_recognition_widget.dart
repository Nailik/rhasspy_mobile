import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/intent_recognition_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

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
