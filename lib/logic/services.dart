import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:rhasspy_mobile/logic/options/wake_word_options.dart';
import 'package:rhasspy_mobile/logic/wake_word_detection/wake_word_porcupine.dart';

import 'settings.dart';

var reloading = false;

var microphonePermissionGranted = true.obs;

Future<void> startServices() async { //TODO if not running
  if (wakeWordSetting.value == WakeWordOption.localPorcupine) {
    await WakeWordDetectionServiceLocal().start();
  }
}

Future<void> stopServices() async {
  await WakeWordDetectionServiceLocal().stop();
}


Future<void> microphonePermissionUpdated() async {
  ///check serivces that use microphone permission
}

void resumedApp() async {
  microphonePermissionGranted.value = await Permission.microphone.isGranted;
  if (microphonePermissionGranted.value) {
    microphonePermissionUpdated();
  }
}

///to apply settings
void reloadServices() async {
  if (!reloading) {
    reloading = true;
    await stopServices();
    await startServices();
    settingsChanged.value = false;
    reloading = false;
  }
}
