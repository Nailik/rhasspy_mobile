import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';

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
        children: const <Widget>[
          Text(
            'Settings Page Content',
          ),
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
    return DropdownButtonHideUnderline(
        child: DropdownButton<Locale>(
          value: Localizations.localeOf(context),
          icon: const SizedBox(width: 15),
          onChanged: (Locale? newLocale) {
            setState(() {
              Get.updateLocale(newLocale!);
            });
          },
          selectedItemBuilder: (BuildContext context) {
            return AppLocalizations.supportedLocales.map((Locale value) {
              return Container(
                  alignment: Alignment.center,
                  child: Text(
                    value.toLanguageTag(),
                    style: Theme
                        .of(context)
                        .primaryTextTheme
                        .titleLarge,
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
        ));
  }

}
