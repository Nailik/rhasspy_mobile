import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/screens/main_screen.dart';

void main() {
  runApp(const RhasspyMobileApp());
}

var themeMode = ThemeMode.system.obs;

ThemeData _getLightTheme() {
  var themeData = ThemeData(brightness: Brightness.light, useMaterial3: true, primarySwatch: Colors.teal);
  themeData = themeData.copyWith(colorScheme: themeData.colorScheme.copyWith(surfaceVariant: themeData.colorScheme.surfaceVariant));
  return themeData;
}

ThemeData _getDarkTheme() {
  var themeData = ThemeData(brightness: Brightness.dark, useMaterial3: true, primarySwatch: Colors.teal);
  themeData = themeData.copyWith(colorScheme: themeData.colorScheme.copyWith(surfaceVariant: themeData.colorScheme.surfaceVariant));
  return themeData;
}

class RhasspyMobileApp extends StatefulWidget {
  const RhasspyMobileApp({Key? key}) : super(key: key);

  @override
  State<RhasspyMobileApp> createState() => _RhasspyMobileAppState();
}

class _RhasspyMobileAppState extends State<RhasspyMobileApp> {
  void removeFocus() {
    FocusScopeNode currentFocus = FocusScope.of(context);
    if (!currentFocus.hasPrimaryFocus) {
      currentFocus.focusedChild?.unfocus();
    }
  }

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return Listener(
        onPointerDown: (_) {
          removeFocus();
        },
        child: Obx(() => GetMaterialApp(
              locale: Get.deviceLocale,
              title: 'Rhasspy Mobile',
              localizationsDelegates: AppLocalizations.localizationsDelegates,
              supportedLocales: AppLocalizations.supportedLocales,
              theme: _getLightTheme(),
              darkTheme: _getDarkTheme(),
              themeMode: themeMode.value,
              home: const MainScreen(),
            )));
  }
}
