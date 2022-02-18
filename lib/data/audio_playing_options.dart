import 'package:flutter_gen/gen_l10n/app_localizations.dart';

enum AudioPlayingOption { local, remoteHTTP, remoteMQTT, disabled }

extension TranslateAudioPlayingOptionEnum on AudioPlayingOption {
  String asText(AppLocalizations local) {
    switch (this) {
      case AudioPlayingOption.local:
        return local.local;
      case AudioPlayingOption.remoteHTTP:
        return local.remoteHTTP;
      case AudioPlayingOption.remoteMQTT:
        return local.remoteMQTT;
      case AudioPlayingOption.disabled:
        return local.disabled;
    }
  }
}