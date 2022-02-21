import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/speech_to_text_options.dart';
import '../../main.dart';
import 'helper.dart';

Widget speechToText() {
  var speechToTextOption = SpeechToTextOption.disabled.obs;
  return listItem(
      SpeechToTextOptions(),
      speechToTextOption,
      getLocale().speechToText,
      Obx(() => speechToTextSettings(speechToTextOption.value)));
}

Widget speechToTextSettings(SpeechToTextOption speechToTextOption) {
  if (speechToTextOption == SpeechToTextOption.remoteHTTP) {
    return Column(children: [
      const Divider(),
      TextFormField(decoration: defaultDecoration(getLocale().speechToTextURL))
    ]);
  } else {
    return Container();
  }
}
