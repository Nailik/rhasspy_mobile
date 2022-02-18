import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum IntentHandlingOption { homeAssistant, remoteHTTP, disabled }

extension TranslateIntentHandlingEnum on IntentHandlingOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case IntentHandlingOption.homeAssistant:
        return local.homeAssistant;
      case IntentHandlingOption.remoteHTTP:
        return local.remoteHTTP;
      case IntentHandlingOption.disabled:
        return local.disabled;
    }
  }
}
