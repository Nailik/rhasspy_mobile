import 'package:flutter/material.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:rhasspy_mobile/logic/services.dart';

import '../../logic/wake_word_detection/wake_word_porcupine.dart';
import '../../logic/wake_word_detection/wake_word_service.dart';
import 'custom_state.dart';

class StartScreen extends StatefulWidget {
  const StartScreen({Key? key}) : super(key: key);

  @override
  State<StartScreen> createState() => _StartScreenState();
}

class _StartScreenState extends CustomState<StartScreen> {
  @override
  Widget content() {
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
        splashColor: theme.colorScheme.tertiary,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 16),
          child: Column(mainAxisAlignment: MainAxisAlignment.center, crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            Obx(() => Icon(
                  Icons.mic,
                  size: MediaQuery.of(context).size.height / 5,
                  color: WakeWordDetectionServiceLocal().wakeWordRecognized.value ? theme.colorScheme.primary : null,
                )),
            Text(
              locale.wakeUp,
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
    return ElevatedButton.icon(onPressed: () {}, label: Text(locale.playRecording), icon: const Icon(Icons.play_arrow));
  }

  Widget recognize() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Expanded(child: TextField(decoration: InputDecoration(labelText: locale.textToRecognize, border: const OutlineInputBorder()))),
        const VerticalDivider(),
        customIconButton(onPressed: () {}, icon: const Icon(Icons.keyboard_arrow_right))
      ]),
    );
  }

  Widget speak() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: Row(mainAxisSize: MainAxisSize.min, children: [
        Expanded(child: TextField(decoration: InputDecoration(labelText: locale.textToSpeak, border: const OutlineInputBorder()))),
        const VerticalDivider(),
        customIconButton(onPressed: () {}, icon: const Icon(Icons.volume_up))
      ]),
    );
  }
}
