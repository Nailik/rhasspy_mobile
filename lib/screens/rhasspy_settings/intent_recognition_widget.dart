import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/intent_recognition_options.dart';
import '../../main.dart';
import 'helper.dart';

Widget intentRecognition() {
  var intentRecognitionOption = IntentRecognitionOption.disabled.obs;
  return listItem(
      IntentRecognitionOptions(),
      intentRecognitionOption,
      getLocale().intentRecognition,
      Obx(() => intentRecognitionSettings(intentRecognitionOption.value)));
}

Widget intentRecognitionSettings(IntentRecognitionOption speechToTextOption) {
  if (speechToTextOption == IntentRecognitionOption.remoteHTTP) {
    return Column(children: [
      const Divider(),
      TextFormField(
          decoration: defaultDecoration(getLocale().rhasspyTextToIntentURL))
    ]);
  } else {
    return Container();
  }
}
