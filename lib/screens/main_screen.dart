import 'package:flutter/material.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings_screen.dart';
import 'package:rhasspy_mobile/screens/settings_screen.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:rhasspy_mobile/screens/start_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: navigationBar(),
      body: getBody(),
      bottomNavigationBar: bottomNavigation(),
    );
  }

  /// App Navigation bar with Title and Button to settings
  AppBar navigationBar() {
    return AppBar(
      title: Text(AppLocalizations.of(context)!.appName),
    );
  }

  Widget? getBody() {
    if (_selectedIndex == 0) {
      return const StartScreen();
    } else if (_selectedIndex == 1) {
      return const RhasspySettingsScreen();
    } else if (_selectedIndex == 2) {
      return const SettingsScreen();
    }
    return null;
  }

  int _selectedIndex = 0;

  BottomNavigationBar bottomNavigation() {
    return BottomNavigationBar(
      items: <BottomNavigationBarItem>[
        BottomNavigationBarItem(
          icon: const Icon(Icons.mic),
          label: AppLocalizations.of(context)!.home,
        ),
        BottomNavigationBarItem(
          icon: const ImageIcon(
            AssetImage('assets/rhasspy_icon.png'),
          ),
          label: AppLocalizations.of(context)!.configuration,
        ),
        BottomNavigationBarItem(
          icon: const Icon(Icons.settings),
          label: AppLocalizations.of(context)!.settings,
        ),
      ],
      currentIndex: _selectedIndex,
      selectedItemColor: Colors.amber[800],
      onTap: _onItemTapped,
    );
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }
}
