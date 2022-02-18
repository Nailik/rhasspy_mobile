import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum TextToSpeechOption { remoteHTTP, remoteMQTT, disabled }

extension TranslateTextToSpeechOptionEnum on TextToSpeechOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case TextToSpeechOption.remoteHTTP:
        return local.remoteHTTP;
      case TextToSpeechOption.remoteMQTT:
        return local.remoteMQTT;
      case TextToSpeechOption.disabled:
        return local.disabled;
    }
  }
}