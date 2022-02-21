import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/main.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({Key? key}) : super(key: key);

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  String dropdownValue = 'One';

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          const Text(
            'Settings Page Content',
          ),
          languageDropDown(),
          MaterialButton(
            color: getTheme().colorScheme.background,
              child: Text(themeBrightness.value == Brightness.light
                  ? "Make Dark"
                  : "Make Light"),
              onPressed: () {
                themeBrightness.value =
                    (themeBrightness.value == Brightness.light)
                        ? Brightness.dark
                        : Brightness.light;
                setState(() {});
              })
        ],
      ),
    );
  }

  /// App Navigation bar with Title
  AppBar navigationBar() {
    return AppBar(
      title: Text(AppLocalizations.of(context)!.settings),
      actions: [languageDropDown()],
    );
  }

  Widget languageDropDown() {
    return DropdownButtonFormField2<Locale>(
      value: Localizations.localeOf(context),
      icon: const SizedBox(width: 15),
      onChanged: (Locale? newLocale) {
        Get.updateLocale(newLocale!);
      },
      selectedItemBuilder: (BuildContext context) {
        return AppLocalizations.supportedLocales.map((Locale value) {
          return Container(
              alignment: Alignment.center,
              child: Text(
                value.toLanguageTag(),
          //      style: Theme.of(context).primaryTextTheme.titleLarge,
              ));
        }).toList();
      },
      items: AppLocalizations.supportedLocales
          .map<DropdownMenuItem<Locale>>((Locale value) {
        return DropdownMenuItem<Locale>(
          value: value,
          child: Text(
            value.toLanguageTag(),
          ),
        );
      }).toList(),
    );
  }
}
