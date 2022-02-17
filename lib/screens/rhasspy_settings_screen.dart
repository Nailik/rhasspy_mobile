import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';
import '../wake_word/wake_word_options.dart';

enum WhyFarther { harder, smarter, selfStarter, tradingCharter }

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

  Widget mqtt() {
    var _passwordHidden = true.obs;

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
        Obx(() => TextFormField(
              obscureText: _passwordHidden.value,
              decoration: InputDecoration(
                border: const UnderlineInputBorder(),
                labelText: locale.password,
                suffixIcon: IconButton(
                  icon: Icon(
                    _passwordHidden.value
                        ? Icons.visibility_off
                        : Icons.visibility,
                    color: theme.primaryColorDark,
                  ),
                  onPressed: () {
                    _passwordHidden.value = !_passwordHidden.value;
                  },
                ),
              ),
            )),
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
    var _audioRecordingUdpOutput = true.obs;
    return Obx(() => ExpansionTile(
          title: Text(locale.audioRecording),
          children: <Widget>[
            SwitchListTile(
                value: _audioRecordingUdpOutput.value,
                onChanged: (value) {
                  _audioRecordingUdpOutput.value = value;
                },
                title: Text(locale.udpAudioOutput),
                subtitle: Text(locale.udpAudioOutputDetail)),
            TextField(
              enabled: _audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.host),
            ),
            TextField(
              enabled: _audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.port),
            ),
            //TODO silence detection
          ],
        ));
  }

  Widget wakeWord() {
    var wakeWordSetting = WakeWordOption.disabled.obs;
    var wakeWordSensitivity = 0.55.obs;

    return Obx(() => ExpansionTile(
            title: Text(locale.wakeWord),
            subtitle: Text(wakeWordSetting.value.asText(locale)),
            children: <Widget>[
              DropdownButtonFormField2<WakeWordOption>(
                value: wakeWordSetting.value,
                onChanged: (WakeWordOption? newValue) {
                  if (newValue != null) {
                    wakeWordSetting.value = newValue;
                  }
                },
                items: WakeWordOption.values
                    .map<DropdownMenuItem<WakeWordOption>>(
                        (WakeWordOption value) {
                  return DropdownMenuItem<WakeWordOption>(
                    value: value,
                    child: Text(value.asText(locale)),
                  );
                }).toList(),
              ),
              //TODO dropdown  (local/remote/disabled)
              //TODO dropdown keyword file
              //todo dropdown avaialble keywords
              Padding(
                  padding: const EdgeInsets.fromLTRB(0, 32, 0, 0),
                  child: Align(
                      alignment: Alignment.centerLeft,
                      child: Text(
                          "${locale.sensitivity} (${wakeWordSensitivity.value.toString()})"))),
              SliderTheme(
                data: SliderThemeData(
                    inactiveTrackColor: theme.colorScheme.secondary,
                    trackHeight: 0.1,
                    valueIndicatorShape:
                        const PaddleSliderValueIndicatorShape(),
                    thumbShape: const RoundSliderThumbShape()),
                child: Slider(
                  value: wakeWordSensitivity.value,
                  max: 1,
                  divisions: 100,
                  label: wakeWordSensitivity.toString(),
                  onChanged: (double value) {
                    wakeWordSensitivity.value = value;
                  },
                ),
              ),
            ]));
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
