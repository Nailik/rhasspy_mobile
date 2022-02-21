import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/audio_playing_options.dart';
import '../../main.dart';
import 'helper.dart';

Widget audioPlaying() {
  var audioPlayingOption = AudioPlayingOption.disabled.obs;
  return listItem(
      AudioPlayingOptions(),
      audioPlayingOption,
      getLocale().audioPlaying,
      Obx(() => audioPlayingSettings(audioPlayingOption.value)));
}

Widget audioPlayingSettings(AudioPlayingOption audioPlayingOption) {
  if (audioPlayingOption == AudioPlayingOption.remoteHTTP) {
    return Column(children: [
      const Divider(),
      TextFormField(decoration: defaultDecoration(getLocale().audioOutputURL))
    ]);
  } else {
    return Container();
  }
}
