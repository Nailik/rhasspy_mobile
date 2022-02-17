import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum WakeWordOption { localPorcupine, remoteMQTT, disabled }

extension TranslateWakeWordOptionEnum on WakeWordOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case WakeWordOption.localPorcupine:
        return local.localPorcupine;
      case WakeWordOption.remoteMQTT:
        return local.remoteMQTT;
      case WakeWordOption.disabled:
        return local.disabled;
    }
  }
}