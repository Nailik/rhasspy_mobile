import 'package:flutter/material.dart';
import 'package:rhasspy_mobile/screens/settings_screen.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class StartScreen extends StatefulWidget {
  const StartScreen({Key? key}) : super(key: key);

  @override
  State<StartScreen> createState() => _StartScreenState();
}

class _StartScreenState extends State<StartScreen> {

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: navigationBar(),
      body: content(),
    );
  }

  /// App Navigation bar with Title and Button to settings
  AppBar navigationBar() {
    return AppBar(
      title: Text(AppLocalizations.of(context)!.appName),
      actions: [
        IconButton(
            onPressed: () {
              Navigator.push(
                context,
                PageRouteBuilder(
                  //go to settings button
                  pageBuilder: (c, a1, a2) => const SettingsScreen(),
                  //left to right slide animation
                  transitionsBuilder: (c, anim, a2, child) => SlideTransition(
                      position: anim.drive(
                          Tween(begin: const Offset(1.0, 0.0), end: Offset.zero)
                              .chain(CurveTween(curve: Curves.ease))),
                      child: child),
                ),
              );
            },
            icon: const Icon(Icons.settings))
      ],
    );
  }

  Widget content() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Text(
              AppLocalizations.of(context)!.appName
          ),
        ],
      ),
    );
  }
}
