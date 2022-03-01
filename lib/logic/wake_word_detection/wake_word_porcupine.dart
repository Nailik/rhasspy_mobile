import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_background_service/flutter_background_service.dart';
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
        onTestChannel();
      }
    } else {
      ///could not start, missing permission
      if (!microphonePermissionMissing.value) {
        microphonePermissionMissing.value = true;
      }
    }
  }

  Future<void> stop() async {
    ///TODO
  }

  Future<void> setupPorcupineManager(String accessKey, double sensitivity) async {
    try {
      _porcupineManager = await PorcupineManager.fromBuiltInKeywords(accessKey, [currentKeyword], _wakeWordCallback, sensitivities: [sensitivity]);
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

Future<void> startAsBackgroundService() async {
  final service = FlutterBackgroundService();
  await service.configure(
    androidConfiguration: AndroidConfiguration(
      // auto start service
      autoStart: true,
      isForegroundMode: true,
      onStart: onStart,
    ),
    iosConfiguration: IosConfiguration(
      // auto start service
      autoStart: true, onForeground: onStart, onBackground: onStart,
    ),
  );
  service.start();

  FlutterBackgroundService().sendData({
    "action": "startWakeWordDetection",
    "wakeWordAccessTokenSetting": wakeWordAccessTokenSetting.value,
    "wakeWordSensitivitySetting": wakeWordSensitivitySetting.value
  });
}

void onStart() async {
  const MethodChannel _channel = MethodChannel('flutter_app_sync'); //TODO test not in background

  WidgetsFlutterBinding.ensureInitialized();

  final service = FlutterBackgroundService();
  service.onDataReceived.listen((event) async {
    PorcupineManager? _porcupineManager;

    if (event!["action"] == "setAsForeground") {
      service.setForegroundMode(true);
      return;
    }

    if (event["action"] == "setAsBackground") {
      service.setForegroundMode(false);
    }

    if (event["action"] == "stopService") {
      service.stopBackgroundService();
    }
    if (event["action"] == "startWakeWordDetection") {


      _channel.setMethodCallHandler((MethodCall call) async {

        if(call.method == "wakeword"){
        }

        return null;
      });

      _channel.invokeMethod("startwakeword", <String, dynamic>{
        'AccessKey': event["wakeWordAccessTokenSetting"],
      });

      /*  try {
        _porcupineManager = await PorcupineManager.fromBuiltInKeywords(event["wakeWordAccessTokenSetting"], [BuiltInKeyword.JARVIS], (int
        keywordIndex){},
            sensitivities: [event["wakeWordSensitivitySetting"]]);
      } on PorcupineException catch (err) {
        // handle porcupine init error
        if (kDebugMode) {
          print(err);
        }
      }
      await _porcupineManager?.start();*/
      /*
      ///setup porcupine manager
      await WakeWordDetectionServiceLocal().setupPorcupineManager(event["wakeWordAccessTokenSetting"], event["wakeWordSensitivitySetting"]);

      ///start recognition
      await WakeWordDetectionServiceLocal().startRecognition();
      */

      _porcupineManager.start()

    }
  });

  // bring to foreground
  service.setForegroundMode(true);

  service.setNotificationInfo(
    title: "My App Service",
    content: "Updated at ${DateTime.now()}",
  );
}


void onTestChannel() async {
  WidgetsFlutterBinding.ensureInitialized();
  const MethodChannel _channel = MethodChannel('flutter_app_sync');
  _channel.setMethodCallHandler((MethodCall call) async {

    if(call.method == "wakeword"){
    }

    return null;
  });

  _channel.invokeMethod("startwakeword", <String, dynamic>{
    'AccessKey': wakeWordAccessTokenSetting.value,
  });

}