import 'package:rhasspy_mobile/logic/options/wake_word_options.dart';
import 'package:rhasspy_mobile/logic/wake_word_detection/wake_word_porcupine.dart';

import 'settings.dart';

var reloading = false;

final wakeWordService = WakeWordDetectionServiceLocal();

Future<void> startServices() async {
  if (wakeWordSetting.value == WakeWordOption.localPorcupine) {
    await wakeWordService.start();
  }
}

Future<void> stopServices() async {
  await wakeWordService.stop();
}


///check serivces that use microphone permission
Future<void> microphonePermissionUpdated() async {

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
