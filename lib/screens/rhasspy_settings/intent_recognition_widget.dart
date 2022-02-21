import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/intent_recognition_options.dart';
import '../rhasspy_settings_screen.dart';

extension IntentHandlingWidget on RhasspySettingsScreenState {
  Widget intentRecognition() {
    var intentRecognitionOption = IntentRecognitionOption.disabled.obs;
    return expandableDropDownListItem(IntentRecognitionOptions(), intentRecognitionOption, locale.intentRecognition, child: Obx(() => intentRecognitionSettings(intentRecognitionOption.value)));
  }

  Widget intentRecognitionSettings(IntentRecognitionOption intentRecognitionOption) {
    if (intentRecognitionOption == IntentRecognitionOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.rhasspyTextToIntentURL))]);
    } else {
      return Container();
    }
  }
}
