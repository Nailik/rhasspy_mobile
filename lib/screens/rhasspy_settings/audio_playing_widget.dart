import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/audio_playing_options.dart';
import '../custom_state.dart';

extension AudioPlayingWidget on CustomState {
  Widget audioPlaying() {
    var audioPlayingOption = AudioPlayingOption.disabled.obs;
    return expandableDropDownListItem(AudioPlayingOptions(), audioPlayingOption, locale.audioPlaying, child: Obx(() => audioPlayingSettings(audioPlayingOption.value)));
  }

  Widget audioPlayingSettings(AudioPlayingOption audioPlayingOption) {
    if (audioPlayingOption == AudioPlayingOption.remoteHTTP) {
      return Column(children: [const Divider(), TextFormField(decoration: defaultDecoration(locale.audioOutputURL))]);
    } else {
      return Container();
    }
  }
}
