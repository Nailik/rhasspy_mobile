import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/text_to_speech_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

extension TextToSpeechWidget on CustomState {
  Widget textToSpeech() {
    return autoSaveExpandableDropDownListItem(
        title: locale.textToSpeech,
        option: TextToSpeechOptions(),
        setting: textToSpeechSetting,
        child: Obx(() => textToSpeechSettings(textToSpeechSetting.value)));
  }

  Widget textToSpeechSettings(TextToSpeechOption textToSpeechOption) {
    if (textToSpeechOption == TextToSpeechOption.remoteHTTP) {
      return Column(children: [const Divider(), autoSaveTextField(title: locale.rhasspyTextToSpeechURL, setting: textToSpeechHTTPURL)]);
    } else {
      return Container();
    }
  }
}
