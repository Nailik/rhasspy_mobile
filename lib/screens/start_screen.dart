import 'package:flutter/material.dart';

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
          Text("audio level"),
          Text("play recording"),
          Text("recognize"),
          Text("speak"),
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
}
