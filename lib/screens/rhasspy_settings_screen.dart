import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_playing_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_recording_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/dialogue_management_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_handling_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_recognition_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/mqtt_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/speech_to_text_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/text_to_speech_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/wake_word_widget.dart';

class RhasspySettingsScreen extends StatefulWidget {
  const RhasspySettingsScreen({Key? key}) : super(key: key);

  @override
  State<RhasspySettingsScreen> createState() => _RhasspySettingsScreenState();
}

class _RhasspySettingsScreenState extends State<RhasspySettingsScreen> {
  late ThemeData theme;
  late AppLocalizations locale;

  @override
  Widget build(BuildContext context) {
    theme = Theme.of(context);
    locale = AppLocalizations.of(context)!;
    return content();
  }

  Widget content() {
    final List<Widget> items = <Widget>[
      siteId(),
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
        decoration: InputDecoration(
            labelText: locale.siteId, border: const OutlineInputBorder()),
      ),
    );
  }
}
