import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/screens/main_screen.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

void main() {
  runApp(const RhasspyMobileApp());
}

class RhasspyMobileApp extends StatelessWidget {
  const RhasspyMobileApp({Key? key}) : super(key: key);

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      locale: Get.deviceLocale,
      title: 'Rhasspy Mobile',
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      theme: ThemeData(
          brightness: Brightness.dark,
          useMaterial3: true,
          primarySwatch: Colors.teal),
      home: const MainScreen(),
    );
  }
}
