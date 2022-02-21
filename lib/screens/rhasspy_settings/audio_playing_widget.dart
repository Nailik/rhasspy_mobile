import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/audio_playing_options.dart';
import '../rhasspy_settings_screen.dart';

extension AudioPlayingWidget on RhasspySettingsScreenState {
  Widget audioPlaying() {
    var audioPlayingOption = AudioPlayingOption.disabled.obs;
    return listItem(AudioPlayingOptions(), audioPlayingOption, locale.audioPlaying, Obx(() => audioPlayingSettings(audioPlayingOption.value)));
  }

  Widget audioPlayingSettings(AudioPlayingOption audioPlayingOption) {
    if (audioPlayingOption == AudioPlayingOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.audioOutputURL))]);
    } else {
      return Container();
    }
  }
}
