import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:rhasspy_mobile/logic/option.dart';

class TextToSpeechOptions extends Option<TextToSpeechOption> {
  static final TextToSpeechOptions _singleton = TextToSpeechOptions._internal();

  factory TextToSpeechOptions() {
    return _singleton;
  }

  TextToSpeechOptions._internal() : super(TextToSpeechOption.values, TextToSpeechOption.disabled);

  @override
  String asText(TextToSpeechOption option, AppLocalizations local) {
    switch (option) {
      case TextToSpeechOption.remoteHTTP:
        return local.remoteHTTP;
      case TextToSpeechOption.remoteMQTT:
        return local.remoteMQTT;
      case TextToSpeechOption.disabled:
        return local.disabled;
    }
  }
}

enum TextToSpeechOption { remoteHTTP, remoteMQTT, disabled }
