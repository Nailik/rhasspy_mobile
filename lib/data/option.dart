import 'package:flutter_gen/gen_l10n/app_localizations.dart';

abstract class Option<T> {
  List<T> options;
  T initial;

  Option(this.options, this.initial);

  String asText(T option, AppLocalizations local);
}
