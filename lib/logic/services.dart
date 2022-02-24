import 'package:rhasspy_mobile/logic/wake_word_detection/wake_word_porcupine.dart';

import '../settings/settings.dart';

var reloading = false;
final wakeWordService = WakeWordDetectionServiceLocal();

Future<void> startServices() async {
  await wakeWordService.start();
}

Future<void> stopServices() async {
  await wakeWordService.stop();
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
