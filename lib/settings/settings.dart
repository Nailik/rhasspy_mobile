import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:rhasspy_mobile/data/speech_to_text_options.dart';
import 'package:rhasspy_mobile/data/wake_word_options.dart';

import '../data/audio_playing_options.dart';
import '../data/dialogue_management_options.dart';
import '../data/intent_handling_options.dart';
import '../data/intent_recognition_options.dart';
import '../data/language_options.dart';
import '../data/text_to_speech_options.dart';
import '../data/theme_options.dart';

final httpSSLSetting = Setting("httpSSLSetting", false);
final siteIdSetting = Setting("siteIdSetting", "");

final mqttHostSetting = Setting("mqttHostSetting", "");
final mqttPortSetting = Setting("mqttPortSetting", "");
final mqttUserNameSetting = Setting("mqttUserNameSetting", "");
final mqttPasswordSetting = Setting("mqttPasswordSetting", "");
final mqttSSLSetting = Setting("mqttSSLSetting", false);

final udpAudioSetting = Setting("udpAudioSetting", false);
final udpAudioHostSetting = Setting("udpAudioHostSetting", "");
final udpAudioPortSetting = Setting("udpAudioPortSetting", "");

final speechToTextSetting = Setting("speechToTextSetting", SpeechToTextOption.disabled);
final speechToTextHTTPURLSetting = Setting("speechToTextHTTPURLSetting", "");

final audioPlayingSetting = Setting("audioPlayingSetting", AudioPlayingOption.disabled);
final audioPlayingHTTPURLSetting = Setting("audioPlayingHTTPURLSetting", "");

final dialogueManagementSetting = Setting("dialogueManagementSetting", DialogueManagementOption.disabled);

final intentHandlingSetting = Setting("intentHandlingSetting", IntentHandlingOption.disabled);
final intentHandlingHTTPURLSetting = Setting("intentHandlingHTTPURLSetting", "");
final intentHandlingHassURLSetting = Setting("intentHandlingHassURLSetting", "");
final intentHandlingHassTokenSetting = Setting("intentHandlingHassTokenSetting", "");

final intentRecognitionSetting = Setting("intentRecognitionSetting", IntentRecognitionOption.disabled);
final intentRecognitionHTTPURLSetting = Setting("intentRecognitionHTTPURLSetting", "");

final languageSettings = Setting("languageSetting", LanguageOption.en);

final textToSpeechSetting = Setting("textToSpeechSetting", TextToSpeechOption.disabled);
final textToSpeechHTTPURL = Setting("textToSpeechHTTPURL", "");

final themeSetting = Setting("themeSetting", ThemeOption.system);

final wakeWordSetting = Setting("wakeWordSetting", WakeWordOption.disabled);

class Setting<T> extends Rx<T> {
  String id;

  Setting(this.id, T initial) : super(initial) {
    if (initial is SpeechToTextOption) {
      value = SpeechToTextOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is AudioPlayingOption) {
      value = AudioPlayingOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is DialogueManagementOption) {
      value = DialogueManagementOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is IntentHandlingOption) {
      value = IntentHandlingOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is IntentRecognitionOption) {
      value = IntentRecognitionOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is LanguageOption) {
      value = LanguageOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is TextToSpeechOption) {
      value = TextToSpeechOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is ThemeOption) {
      value = ThemeOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else if (initial is WakeWordOption) {
      value = WakeWordOption.values.byName(GetStorage().read<String>(id) ?? initial.name) as T;
    } else {
      value = GetStorage().read<T>(id) ?? initial;
    }
  }

  void setValue(T value) {
    if (value is SpeechToTextOption) {
      GetStorage().write(id, value.name);
    } else if (value is AudioPlayingOption) {
      GetStorage().write(id, value.name);
    } else if (value is DialogueManagementOption) {
      GetStorage().write(id, value.name);
    } else if (value is IntentHandlingOption) {
      GetStorage().write(id, value.name);
    } else if (value is IntentRecognitionOption) {
      GetStorage().write(id, value.name);
    } else if (value is LanguageOption) {
      GetStorage().write(id, value.name);
    } else if (value is TextToSpeechOption) {
      GetStorage().write(id, value.name);
    } else if (value is ThemeOption) {
      GetStorage().write(id, value.name);
    } else if (value is WakeWordOption) {
      GetStorage().write(id, value.name);
    } else {
      GetStorage().write(id, value);
    }
    this.value = value;
  }
}
