import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/text_to_speech_options.dart';
import '../../main.dart';
import 'helper.dart';

Widget textToSpeech() {
  var textToSpeechOption = TextToSpeechOption.disabled.obs;
  return listItem(
      TextToSpeechOptions(),
      textToSpeechOption,
      getLocale().textToSpeech,
      Obx(() => textToSpeechSettings(textToSpeechOption.value)));
}

Widget textToSpeechSettings(TextToSpeechOption textToSpeechOption) {
  if (textToSpeechOption == TextToSpeechOption.remoteHTTP) {
    return Column(children: [
      const Divider(),
      TextFormField(
          decoration: defaultDecoration(getLocale().rhasspyTextToSpeechURL))
    ]);
  } else {
    return Container();
  }
}
