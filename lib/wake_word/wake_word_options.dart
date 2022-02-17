import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum WakeWordOption{
  local, remote, disabled
}

extension TranslateEnum on WakeWordOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case WakeWordOption.local:
        return local.local;
      case WakeWordOption.remote:
        return local.remote;
      case WakeWordOption.disabled:
        return local.disabled;
    }
  }
}