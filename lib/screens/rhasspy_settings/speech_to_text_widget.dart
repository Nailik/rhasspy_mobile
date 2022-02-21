import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/speech_to_text_options.dart';
import '../rhasspy_settings_screen.dart';
import 'helper.dart';

extension SpeechToTextWidget on RhasspySettingsScreenState {
  Widget speechToText() {
    var speechToTextOption = SpeechToTextOption.disabled.obs;
    return listItem(
        SpeechToTextOptions(),
        speechToTextOption,
        locale.speechToText,
        Obx(() => speechToTextSettings(speechToTextOption.value)));
  }

  Widget speechToTextSettings(SpeechToTextOption speechToTextOption) {
    if (speechToTextOption == SpeechToTextOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(decoration: defaultDecoration(locale.speechToTextURL))
      ]);
    } else {
      return Container();
    }
  }
}
