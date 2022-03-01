import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';

extension AudioRecordingWidget on CustomState {
  Widget audioRecording() {
    var audioRecordingUdpOutput = false.obs;
    return expandableListItem(
      title: locale.audioRecording,
      subtitle: () => audioRecordingUdpOutput.value ? locale.udpAudioOutput : locale.udpAudioOutputOff,
      children: <Widget>[
        const Divider(),

        ///switch
        autoSaveSwitchTile(title: locale.udpAudioOutput, subtitle: locale.udpAudioOutputDetail, setting: udpAudioSetting),
        const Divider(),

        ///host
        Obx(() => autoSaveTextField(title: locale.host, setting: udpAudioHostSetting, enabled: udpAudioSetting.value)),
        const Divider(),

        ///Ports
        Obx(() => autoSaveTextField(title: locale.port, setting: udpAudioPortSetting, enabled: udpAudioSetting.value)),
      ],
    );
  }
}
