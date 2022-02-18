import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum SpeechToTextOption { remoteHTTP, remoteMQTT, disabled }

extension TranslateSpeechToTextOptionEnum on SpeechToTextOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case SpeechToTextOption.remoteHTTP:
        return local.remoteHTTP;
      case SpeechToTextOption.remoteMQTT:
        return local.remoteMQTT;
      case SpeechToTextOption.disabled:
        return local.disabled;
    }
  }
}
