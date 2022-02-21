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
    return LayoutBuilder(builder: (context, constraint) {
      return SingleChildScrollView(
        child: ConstrainedBox(
            constraints: BoxConstraints(minHeight: constraint.maxHeight),
            child: IntrinsicHeight(
                child: Column(
              children: <Widget>[wakeup(), const Divider(), actions()],
            ))),
      );
    });
  }

  ///icon button and text wake up, surrounded by audio level
  Widget wakeup() {
    return Expanded(
      child: InkWell(
        onTap: () {},
        splashColor: getTheme().colorScheme.tertiary,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 16),
          child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Icon(Icons.mic, size: MediaQuery.of(context).size.height / 5),
                Text(
                  getLocale().wakeUp,
                  textAlign: TextAlign.center,
                )
              ]),
        ),
      ),
    );
  }

  Widget actions() {
    return Padding(
        padding: const EdgeInsets.symmetric(vertical: 16),
        child: Column(children: [
          playRecording(),
          recognize(),
          speak(),
        ]));
  }

  Widget playRecording() {
    return ElevatedButton.icon(
        onPressed: () {},
        label: Text(getLocale().playRecording),
        icon: const Icon(Icons.play_arrow));
  }

  Widget recognize() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Expanded(
            child: TextField(
                decoration: InputDecoration(
                    labelText: getLocale().textToRecognize,
                    border: const OutlineInputBorder()))),
        const VerticalDivider(),
        customIconButton(
            onPressed: () {}, icon: const Icon(Icons.keyboard_arrow_right))
      ]),
    );
  }

  Widget speak() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Expanded(
            child: TextField(
                decoration: InputDecoration(
                    labelText: getLocale().textToSpeak,
                    border: const OutlineInputBorder()))),
        const VerticalDivider(),
        customIconButton(onPressed: () {}, icon: const Icon(Icons.volume_up))
      ]),
    );
  }
}
