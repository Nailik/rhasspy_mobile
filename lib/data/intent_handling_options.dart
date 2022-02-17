enum IntentHandling { homeAssistant, remoteHTTP, disabled }

extension TranslateIntentHandlingEnum on IntentHandling {
  String asText(AppLocalizations local) {
    switch (this) {
      case IntentHandling.homeAssistant:
        return local.homeAssistant;
      case IntentHandling.remoteMQTT:
        return local.remoteMQTT;
      case IntentHandling.disabled:
        return local.disabled;
    }
  }
}
