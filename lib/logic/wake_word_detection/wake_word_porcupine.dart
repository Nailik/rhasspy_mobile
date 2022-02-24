import 'package:flutter/foundation.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:porcupine_flutter/porcupine.dart';
import 'package:porcupine_flutter/porcupine_error.dart';
import 'package:porcupine_flutter/porcupine_manager.dart';
import 'package:rhasspy_mobile/logic/options/wake_word_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/logic/wake_word_detection/wake_word_service.dart';

var microphonePermissionMissing = false.obs;

///
class WakeWordDetectionServiceLocal extends WakeWordService {
  static final WakeWordDetectionServiceLocal _singleton = WakeWordDetectionServiceLocal._internal();

  factory WakeWordDetectionServiceLocal() {
    return _singleton;
  }

  WakeWordDetectionServiceLocal._internal() : super();

  var _isRunning = false;

  PorcupineManager? _porcupineManager;
  BuiltInKeyword currentKeyword = wakeWordNameOptionsSetting.value;

  ///starts the service
  ///listen if wake word settings changes
  ///listen if keyword changes
  Future<void> start() async {
    if (await Permission.microphone.isGranted) {
      microphonePermissionMissing.value = false;
      if (wakeWordSetting.value == WakeWordOption.localPorcupine && !_isRunning) {
        _isRunning = true;

        ///setup porcupine manager
        await setupPorcupineManager();

        ///start recognition
        await startRecognition();
      }
    } else {
      ///could not start, missing permission
      if (!microphonePermissionMissing.value) {
        microphonePermissionMissing.value = true;
      }
    }
  }

  Future<void> stop() async {
    await _porcupineManager?.delete();
  }

  Future<void> setupPorcupineManager() async {
    try {
      _porcupineManager = await PorcupineManager.fromBuiltInKeywords(wakeWordAccessTokenSetting.value, [currentKeyword], _wakeWordCallback,
          sensitivities: [wakeWordSensitivitySetting.value]);
    } on PorcupineException catch (err) {
      // handle porcupine init error
      if (kDebugMode) {
        print(err);
      }
    }
  }

  void _wakeWordCallback(int keywordIndex) async {
    wakeWordRecognized.value = true;
    Future.delayed(const Duration(seconds: 2), () => wakeWordRecognized.value = false);
    if (keywordIndex == 0) {
      // porcupine detected
    } else if (keywordIndex == 1) {
      // bumblebee detected
    }
  }

  Future<void> startRecognition() async {
    try {
      await _porcupineManager?.start();
    } on Exception catch (ex) {
      // deal with audio exception
      if (kDebugMode) {
        print(ex);
      }
    }
  }

  Future<void> stopRecognition() async {
    try {
      await _porcupineManager?.stop();
    } on Exception catch (ex) {
      // deal with audio exception
      if (kDebugMode) {
        print(ex);
      }
    }
  }
}
