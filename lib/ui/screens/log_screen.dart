import 'package:flutter/material.dart';

import 'custom_state.dart';

class LogScreen extends StatefulWidget {
  const LogScreen({Key? key}) : super(key: key);

  @override
  State<LogScreen> createState() => _LogScreenState();
}

class _LogScreenState extends CustomState<LogScreen> {
  @override
  Widget content() {
    final List<Widget> items = <Widget>[const Text("test"), const Text("test2"), const Text("test3"), const Text("test4")];

    return ListView.separated(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        return Padding(padding: const EdgeInsets.all(10.0), child: items[index]);
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }
}
