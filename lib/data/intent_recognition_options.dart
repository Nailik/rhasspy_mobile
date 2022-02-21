import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class IntentRecognitionOptions extends Option<IntentRecognitionOption> {
  static final IntentRecognitionOptions _singleton = IntentRecognitionOptions._internal();

  factory IntentRecognitionOptions() {
    return _singleton;
  }

  IntentRecognitionOptions._internal() : super(IntentRecognitionOption.values, IntentRecognitionOption.disabled);

  @override
  String asText(IntentRecognitionOption option, AppLocalizations local) {
    switch (option) {
      case IntentRecognitionOption.remoteHTTP:
        return local.remoteHTTP;
      case IntentRecognitionOption.remoteMQTT:
        return local.remoteMQTT;
      case IntentRecognitionOption.disabled:
        return local.disabled;
    }
  }
}

enum IntentRecognitionOption { remoteHTTP, remoteMQTT, disabled }
