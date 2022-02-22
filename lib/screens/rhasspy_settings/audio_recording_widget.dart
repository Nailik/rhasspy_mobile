import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../custom_state.dart';

extension AudioRecordingWidget on CustomState {
  Widget audioRecording() {
    var audioRecordingUdpOutput = false.obs;
    return expandableListItem(
      title: locale.audioRecording,
      subtitle: () => audioRecordingUdpOutput.value ? locale.udpAudioOutput : locale.udpAudioOutputOff,
      children: <Widget>[
        const Divider(),
        Obx(() => SwitchListTile(
            value: audioRecordingUdpOutput.value,
            onChanged: (value) {
              audioRecordingUdpOutput.value = value;
            },
            title: Text(locale.udpAudioOutput),
            subtitle: Text(locale.udpAudioOutputDetail))),
        const Divider(),
        Obx(() => TextField(
              enabled: audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.host),
            )),
        const Divider(),
        Obx(() => TextField(
              enabled: audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.port),
            )),
        //TODO silence detection
      ],
    );
  }
}
