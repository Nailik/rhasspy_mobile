import 'package:flutter/material.dart';
import 'package:rhasspy_mobile/screens/custom_state.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_playing_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_recording_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/dialogue_management_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_handling_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_recognition_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/mqtt_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/rhasspy_base_settings.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/speech_to_text_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/text_to_speech_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/wake_word_widget.dart';

class RhasspySettingsScreen extends StatefulWidget {
  const RhasspySettingsScreen({Key? key}) : super(key: key);

  @override
  State<RhasspySettingsScreen> createState() => RhasspySettingsScreenState();
}

class RhasspySettingsScreenState extends CustomState<RhasspySettingsScreen> {
  @override
  Widget content() {
    final List<Widget> items = <Widget>[
      siteId(),
      rhasspyHTTPSettings(),
      mqtt(),
      audioRecording(),
      wakeWord(),
      speechToText(),
      intentRecognition(),
      textToSpeech(),
      audioPlaying(),
      dialogueManagement(),
      intentHandling()
    ];

    return ListView.separated(
      itemCount: items.length,
      itemBuilder: (BuildContext context, int index) {
        return items[index];
      },
      separatorBuilder: (BuildContext context, int index) => const Divider(),
    );
  }

  Widget siteId() {
    return Container(
      padding: const EdgeInsets.all(10.0),
      child: TextField(
        decoration: InputDecoration(labelText: locale.siteId, border: const OutlineInputBorder()),
      ),
    );
  }
}
