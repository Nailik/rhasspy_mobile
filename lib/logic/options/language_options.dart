import 'dart:ui';

import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:rhasspy_mobile/logic/option.dart';

class LanguageOptions extends Option<LanguageOption> {
  static final LanguageOptions _singleton = LanguageOptions._internal();

  factory LanguageOptions() {
    return _singleton;
  }

  LanguageOptions._internal() : super(LanguageOption.values, LanguageOption.en);

  @override
  String asText(LanguageOption option, AppLocalizations local) {
    switch (option) {
      case LanguageOption.en:
        return local.en;
      case LanguageOption.de:
        return local.de;
    }
  }

  static Locale asLocale(LanguageOption option) {
    switch (option) {
      case LanguageOption.en:
        return const Locale.fromSubtags(languageCode: "en");
      case LanguageOption.de:
        return const Locale.fromSubtags(languageCode: "de");
    }
  }
}

enum LanguageOption { en, de }
