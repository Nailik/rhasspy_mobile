import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/audio_playing_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

extension AudioPlayingWidget on CustomState {
  Widget audioPlaying() {
    return autoSaveExpandableDropDownListItem(
        title: locale.audioPlaying,
        option: AudioPlayingOptions(),
        setting: audioPlayingSetting,
        child: Obx(() => audioPlayingSettings(audioPlayingSetting.value)));
  }

  Widget audioPlayingSettings(AudioPlayingOption audioPlayingOption) {
    if (audioPlayingOption == AudioPlayingOption.remoteHTTP) {
      return Column(children: [const Divider(), autoSaveTextField(title: locale.audioOutputURL, setting: audioPlayingHTTPURLSetting)]);
    } else {
      return Container();
    }
  }
}
