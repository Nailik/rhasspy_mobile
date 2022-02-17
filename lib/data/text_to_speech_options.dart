enum TextToSpeechOption { remoteHTTP, remoteMQTT, disabled }

extension TranslateTextToSpeechOptionEnum on TextToSpeechOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case TextToSpeechOption.remoteHTTP:
        return local.local;
      case TextToSpeechOption.remoteMQTT:
        return local.remoteMQTT;
      case TextToSpeechOption.disabled:
        return local.disabled;
    }
  }
}