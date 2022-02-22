import 'package:flutter/material.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings_screen.dart';
import 'package:rhasspy_mobile/screens/settings_screen.dart';
import 'package:rhasspy_mobile/screens/start_screen.dart';

import 'custom_state.dart';
import 'log_screen.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends CustomState<MainScreen> {
  @override
  Widget content() {
    return Scaffold(
      appBar: navigationBar(),
      body: getBody(),
      bottomNavigationBar: Obx(() => bottomNavigation()),
    );
  }

  /// App Navigation bar with Title and Button to settings
  AppBar navigationBar() {
    return AppBar(
      title: Text(locale.appName),
    );
  }

  Widget? getBody() {
    switch (_selectedIndex) {
      case 0:
        return const StartScreen();
      case 1:
        return const RhasspySettingsScreen();
      case 2:
        return const SettingsScreen();
      case 3:
        return const LogScreen();
      default:
        return null;
    }
  }

  int _selectedIndex = 0;

  Widget bottomNavigation() {
    List<BottomNavigationBarItem> items = <BottomNavigationBarItem>[
      BottomNavigationBarItem(
        icon: const Icon(Icons.mic),
        label: locale.home,
      ),
      BottomNavigationBarItem(
        icon: const ImageIcon(
          AssetImage('assets/rhasspy_icon.png'),
        ),
        label: locale.configuration,
      ),
      BottomNavigationBarItem(
        icon: const Icon(Icons.settings),
        label: locale.settings,
      )
    ];

    if (showLog.value) {
      items.add(BottomNavigationBarItem(
        icon: const Icon(Icons.reorder),
        label: locale.log,
      ));
    }

    return BottomNavigationBar(
      items: items,
      type: BottomNavigationBarType.fixed,
      currentIndex: _selectedIndex,
      backgroundColor: theme.colorScheme.surfaceVariant,
      selectedItemColor: theme.colorScheme.onSurfaceVariant,
      onTap: _onItemTapped,
    );
  }

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }
}
