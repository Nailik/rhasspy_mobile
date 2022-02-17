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