import 'package:flutter/material.dart';
import 'package:rhasspy_mobile/main.dart';

class StartScreen extends StatefulWidget {
  const StartScreen({Key? key}) : super(key: key);

  @override
  State<StartScreen> createState() => _StartScreenState();
}

class _StartScreenState extends State<StartScreen> {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          wakeup(),
          Text("play recording"),
          recognize(),
          speak(),
        ],
      ),
    );
  }

  ///icon button and text wake up, surrounded by audio level
  Widget wakeup() {
    return Column(children: [
      IconButton(
        icon: const Icon(Icons.mic),
        onPressed: () {},
      ),
      Text("wake Up")
    ]);
  }

  Widget recognize() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: TextField(
        decoration: InputDecoration(
            labelText: getLocale().siteId, border: const OutlineInputBorder()),
      ),
    );
  }

  Widget speak() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: TextField(
        decoration: InputDecoration(
            labelText: getLocale().siteId, border: const OutlineInputBorder()),
      ),
    );
  }
}
