import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/speech_to_text_options.dart';
import '../custom_state.dart';

extension SpeechToTextWidget on CustomState {
  Widget speechToText() {
    var speechToTextOption = SpeechToTextOption.disabled.obs;
    return expandableDropDownListItem(SpeechToTextOptions(), speechToTextOption, locale.speechToText,
        child: Obx(() => speechToTextSettings(speechToTextOption.value)));
  }

  Widget speechToTextSettings(SpeechToTextOption speechToTextOption) {
    if (speechToTextOption == SpeechToTextOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.speechToTextURL))]);
    } else {
      return Container();
    }
  }
}
