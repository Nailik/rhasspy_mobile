import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum IntentRecognitionOption { remoteHTTP, remoteMQTT, disabled }

extension TranslateIntentRecognitionOptionEnum on IntentRecognitionOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case IntentRecognitionOption.remoteHTTP:
        return local.remoteHTTP;
      case IntentRecognitionOption.remoteMQTT:
        return local.remoteMQTT;
      case IntentRecognitionOption.disabled:
        return local.disabled;
    }
  }
}