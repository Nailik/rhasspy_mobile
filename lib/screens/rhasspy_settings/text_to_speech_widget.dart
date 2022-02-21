import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/text_to_speech_options.dart';
import '../rhasspy_settings_screen.dart';

extension IntentHandlingWidget on RhasspySettingsScreenState {
  Widget textToSpeech() {
    var textToSpeechOption = TextToSpeechOption.disabled.obs;
    return expandableDropDownListItem(TextToSpeechOptions(), textToSpeechOption, locale.textToSpeech, child: Obx(() => textToSpeechSettings(textToSpeechOption.value)));
  }

  Widget textToSpeechSettings(TextToSpeechOption textToSpeechOption) {
    if (textToSpeechOption == TextToSpeechOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.rhasspyTextToSpeechURL))]);
    } else {
      return Container();
    }
  }
}
