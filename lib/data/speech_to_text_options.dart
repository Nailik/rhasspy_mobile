import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class SpeechToTextOptions extends Option<SpeechToTextOption> {
  static final SpeechToTextOptions _singleton = SpeechToTextOptions._internal();

  factory SpeechToTextOptions() {
    return _singleton;
  }

  SpeechToTextOptions._internal() : super(SpeechToTextOption.values, SpeechToTextOption.disabled);

  @override
  String asText(SpeechToTextOption option, AppLocalizations local) {
    switch (option) {
      case SpeechToTextOption.remoteHTTP:
        return local.remoteHTTP;
      case SpeechToTextOption.remoteMQTT:
        return local.remoteMQTT;
      case SpeechToTextOption.disabled:
        return local.disabled;
    }
  }
}

enum SpeechToTextOption { remoteHTTP, remoteMQTT, disabled }
