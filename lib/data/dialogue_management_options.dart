import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum DialogueManagementOption { local, remoteMQTT, disabled }

extension TranslateDialogueManagementOptionEnum on DialogueManagementOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case DialogueManagementOption.local:
        return local.local;
      case DialogueManagementOption.remoteMQTT:
        return local.remoteMQTT;
      case DialogueManagementOption.disabled:
        return local.disabled;
    }
  }
}