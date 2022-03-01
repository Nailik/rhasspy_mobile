import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/logic/options/text_to_speech_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';

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
