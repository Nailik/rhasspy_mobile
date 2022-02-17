import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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
      padding: const EdgeInsets.all(8),
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
        decoration: defaultDecoration(locale.siteId),
      ),
    );
  }

  bool _audioRecordingUdpOutput = true;
  bool _passwordHidden = true;

  Widget mqtt() {
    return ExpansionTile(
      title: Text(
        locale.mqtt,
      ),
      children: <Widget>[
        TextField(
          decoration: defaultDecoration(locale.host),
        ),
        TextField(
          decoration: defaultDecoration(locale.port),
        ),
        TextField(
          decoration: defaultDecoration(locale.userName),
        ),
        TextFormField(
          obscureText: _passwordHidden,
          decoration: InputDecoration(
            border: const UnderlineInputBorder(),
            labelText: locale.password,
            suffixIcon: IconButton(
              icon: Icon(
                _passwordHidden ? Icons.visibility_off : Icons.visibility,
                color: theme.primaryColorDark,
              ),
              onPressed: () {
                _passwordHidden = !_passwordHidden;
              },
            ),
          ),
        ),
        MaterialButton(
            child: Text(
              locale.checkConnection,
              style: theme.textTheme.bodyLarge!.copyWith(
                color: theme.colorScheme.error,
              ),
            ),
            onPressed: () {})
      ],
    );
  }

  Widget audioRecording() {
    return ExpansionTile(
      title: Text(locale.audioRecording),
      children: <Widget>[
        SwitchListTile(
            value: _audioRecordingUdpOutput,
            onChanged: (value) {
              setState(() {
                _audioRecordingUdpOutput = value;
              });
            },
            title: Text(locale.udpAudioOutput),
            subtitle: Text(locale.udpAudioOutputDetail)),
        TextField(
          enabled: false,
          decoration: defaultDecoration(locale.host),
        ),
        TextField(
          enabled: _audioRecordingUdpOutput,
          decoration: defaultDecoration(locale.port),
        ),
        //TODO silence detection
      ],
    );
  }

  Widget wakeWord() {
    return ExpansionTile(
      title: Text(locale.wakeWord),
      children: <Widget>[
        Text(locale.wakeWord),
        //TODO dropdown  (local/remote/disabled)
        //TODO dropdown keyword file
        //todo dropdown avaialble keywords
        TextFormField(
          decoration: InputDecoration(
            border: const UnderlineInputBorder(),
            labelText: locale.sensitivity,
          ),
        ),
      ],
    );
  }

  Widget speechToText() {
    return ExpansionTile(
      title: Text(
        locale.speechToText,
      ),
      children: <Widget>[
        Text(locale.speechToText),
        TextFormField(
          decoration: InputDecoration(
            border: const UnderlineInputBorder(),
            labelText: locale.speechToTextURL,
          ),
        ),
      ],
    );
  }

  Widget intentRecognition() {
    return ExpansionTile(
      title: Text(
        locale.intentRecognition,
      ),
      children: <Widget>[
        Text(locale.intentRecognition),
        TextFormField(
          decoration: InputDecoration(
            border: const UnderlineInputBorder(),
            labelText: locale.rhasspyTextToIntentURL,
          ),
        ),
      ],
    );
  }

  Widget textToSpeech() {
    return ExpansionTile(
      title: Text(
        locale.textToSpeech,
      ),
      children: <Widget>[
        Text(locale.textToSpeech),
        TextFormField(
          decoration: InputDecoration(
            border: const UnderlineInputBorder(),
            labelText: locale.rhasspyTextToSpeechURL,
          ),
        ),
      ],
    );
  }

  Widget audioPlaying() {
    return ExpansionTile(
      title: Text(
        locale.audioPlaying,
      ),
      children: <Widget>[
        Text(locale.audioPlaying),
        //Todo volume
      ],
    );
  }

  Widget dialogueManagement() {
    return ExpansionTile(
      title: Text(
        locale.dialogueManagement,
      ),
      children: <Widget>[
        Text(locale.dialogueManagement),
        //Todo not sure??
      ],
    );
  }

  Widget intentHandling() {
    return ExpansionTile(
      title: Text(
        locale.intentHandling,
      ),
      children: <Widget>[
        Text(locale.intentHandling),
        //Todo drop down home assistant/remote http/disabled
      ],
    );
  }

  InputDecoration defaultDecoration(String labelText) {
    return InputDecoration(
      border: const UnderlineInputBorder(),
      labelText: labelText,
    );
  }
}
