import 'package:flutter_gen/gen_l10n/app_localizations.dart';

import 'option.dart';

class DialogueManagementOptions extends Option<DialogueManagementOption> {
  static final DialogueManagementOptions _singleton =
      DialogueManagementOptions._internal();

  factory DialogueManagementOptions() {
    return _singleton;
  }

  DialogueManagementOptions._internal()
      : super(
            DialogueManagementOption.values, DialogueManagementOption.disabled);

  @override
  String asText(DialogueManagementOption option, AppLocalizations local) {
    switch (option) {
      case DialogueManagementOption.local:
        return local.local;
      case DialogueManagementOption.remoteMQTT:
        return local.remoteMQTT;
      case DialogueManagementOption.disabled:
        return local.disabled;
    }
  }
}

enum DialogueManagementOption { local, remoteMQTT, disabled }
