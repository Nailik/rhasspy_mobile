import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:rhasspy_mobile/data/option.dart';

class AudioPlayingOptions extends Option<AudioPlayingOption> {
  static final AudioPlayingOptions _singleton = AudioPlayingOptions._internal();

  factory AudioPlayingOptions() {
    return _singleton;
  }

  AudioPlayingOptions._internal() : super(AudioPlayingOption.values, AudioPlayingOption.disabled);

  @override
  String asText(AudioPlayingOption option, AppLocalizations local) {
    switch (option) {
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

enum AudioPlayingOption { local, remoteHTTP, remoteMQTT, disabled }
