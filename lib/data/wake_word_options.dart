import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class WakeWordOptions extends Option<WakeWordOption> {
  static final WakeWordOptions _singleton = WakeWordOptions._internal();

  factory WakeWordOptions() {
    return _singleton;
  }

  WakeWordOptions._internal()
      : super(WakeWordOption.values, WakeWordOption.disabled);

  @override
  String asText(WakeWordOption option, AppLocalizations local) {
    switch (option) {
      case WakeWordOption.localPorcupine:
        return local.localPorcupine;
      case WakeWordOption.remoteMQTT:
        return local.remoteMQTT;
      case WakeWordOption.disabled:
        return local.disabled;
    }
  }
}

enum WakeWordOption { localPorcupine, remoteMQTT, disabled }
