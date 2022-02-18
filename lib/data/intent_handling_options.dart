import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class IntentHandlingOptions extends Option<IntentHandlingOption> {
  static final IntentHandlingOptions _singleton =
  IntentHandlingOptions._internal();

  factory IntentHandlingOptions() {
    return _singleton;
  }

  IntentHandlingOptions._internal()
      : super(IntentHandlingOption.values, IntentHandlingOption.disabled);

  @override
  String asText(IntentHandlingOption option, AppLocalizations local) {
    switch (option) {
      case IntentHandlingOption.homeAssistant:
        return local.homeAssistant;
      case IntentHandlingOption.remoteHTTP:
        return local.remoteHTTP;
      case IntentHandlingOption.disabled:
        return local.disabled;
    }
  }
}

enum IntentHandlingOption { homeAssistant, remoteHTTP, disabled }

enum HomeAssistantIntent { events, intents }