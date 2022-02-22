import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/intent_recognition_options.dart';
import '../custom_state.dart';

extension IntentHandlingWidget on CustomState {
  Widget intentRecognition() {
    var intentRecognitionOption = IntentRecognitionOption.disabled.obs;
    return expandableDropDownListItem(IntentRecognitionOptions(), intentRecognitionOption, locale.intentRecognition,
        child: Obx(() => intentRecognitionSettings(intentRecognitionOption.value)));
  }

  Widget intentRecognitionSettings(IntentRecognitionOption intentRecognitionOption) {
    if (intentRecognitionOption == IntentRecognitionOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.rhasspyTextToIntentURL))]);
    } else {
      return Container();
    }
  }
}
