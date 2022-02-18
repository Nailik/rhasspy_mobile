import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/data/audio_playing_options.dart';
import 'package:rhasspy_mobile/data/dialogue_management_options.dart';
import 'package:rhasspy_mobile/data/intent_handling_options.dart';
import 'package:rhasspy_mobile/data/intent_recognition_options.dart';
import 'package:rhasspy_mobile/data/speech_to_text_options.dart';
import '../data/text_to_speech_options.dart';
import '../data/wake_word_options.dart';

enum WhyFarther { harder, smarter, selfStarter, tradingCharter }

class RhasspySettingsScreen extends StatefulWidget {
  const RhasspySettingsScreen({Key? key}) : super(key: key);

  @override
  State<RhasspySettingsScreen> createState() => _RhasspySettingsScreenState();
}

class _RhasspySettingsScreenState extends State<RhasspySettingsScreen> {
  late ThemeData theme;
  late AppLocalizations locale;

  final _tilePadding = const EdgeInsets.fromLTRB(8, 0, 8, 8);

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
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
      children: <Widget>[
        const Divider(),
        TextField(
          decoration: defaultDecoration(locale.host),
        ),
        const Divider(),
        TextField(
          decoration: defaultDecoration(locale.port),
        ),
        const Divider(),
        TextField(
          decoration: defaultDecoration(locale.userName),
        ),
        const Divider(),
        ObxValue<RxBool>(
            (passwordHidden) => TextFormField(
                  obscureText: passwordHidden.value,
                  decoration: InputDecoration(
                    border: const UnderlineInputBorder(),
                    labelText: locale.password,
                    suffixIcon: IconButton(
                      icon: Icon(
                        passwordHidden.value
                            ? Icons.visibility_off
                            : Icons.visibility,
                        color: theme.primaryColorDark,
                      ),
                      onPressed: () {
                        passwordHidden.value = !passwordHidden.value;
                      },
                    ),
                  ),
                ),
            true.obs),
        const Divider(),
        ElevatedButton(child: Text(locale.checkConnection), onPressed: () {})
      ],
    );
  }

  Widget audioRecording() {
    var _audioRecordingUdpOutput = true.obs;
    return Obx(() => ExpansionTile(
          title: Text(locale.audioRecording),
          backgroundColor: theme.colorScheme.surfaceVariant,
          childrenPadding: _tilePadding,
          children: <Widget>[
            const Divider(),
            SwitchListTile(
                value: _audioRecordingUdpOutput.value,
                onChanged: (value) {
                  _audioRecordingUdpOutput.value = value;
                },
                title: Text(locale.udpAudioOutput),
                subtitle: Text(locale.udpAudioOutputDetail)),
            const Divider(),
            TextField(
              enabled: _audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.host),
            ),
            const Divider(),
            TextField(
              enabled: _audioRecordingUdpOutput.value,
              decoration: defaultDecoration(locale.port),
            ),
            //TODO silence detection
          ],
        ));
  }

  Widget wakeWord() {
    var wakeWordOption = WakeWordOption.disabled.obs;
    return Obx(() => ExpansionTile(
            title: Text(locale.wakeWord),
            subtitle: Text(wakeWordOption.value.asText(locale)),
            backgroundColor: theme.colorScheme.surfaceVariant,
            childrenPadding: _tilePadding,
            children: <Widget>[
              const Divider(),
              DropdownButtonFormField2<WakeWordOption>(
                value: wakeWordOption.value,
                onChanged: (WakeWordOption? newValue) {
                  if (newValue != null) {
                    wakeWordOption.value = newValue;
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
              localWakeWordSettings(wakeWordOption.value)
            ]));
  }

  Widget localWakeWordSettings(WakeWordOption wakeWordOption) {
    if (wakeWordOption == WakeWordOption.localPorcupine) {
      return Column(children: <Widget>[
        const Divider(),
        localWakeWordKeyword(),
        const Divider(),
        localWakeWordSensitivity()
      ]);
    } else {
      return Container();
    }
  }

  Widget localWakeWordKeyword() {
    var wakeWordKeywordOption = "1".obs;
    var wakeWordKeywordOptions = ["1", "2g"];

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Expanded(
            child: Obx(
          () => DropdownButtonFormField2<String>(
            value: wakeWordKeywordOption.value,
            onChanged: (String? newValue) {
              if (newValue != null) {
                wakeWordKeywordOption.value = newValue;
              }
            },
            items: wakeWordKeywordOptions
                .map<DropdownMenuItem<String>>((String value) {
              return DropdownMenuItem<String>(
                value: value,
                child: Text(value),
              );
            }).toList(),
          ),
        )),
        const VerticalDivider(),
        ElevatedButton(onPressed: () {}, child: Text(locale.refresh))
      ],
    );
  }

  Widget localWakeWordSensitivity() {
    var wakeWordSensitivity = 0.55.obs;

    return Obx(() => Column(children: <Widget>[
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
                valueIndicatorShape: const PaddleSliderValueIndicatorShape(),
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
    var speechToTextOption = SpeechToTextOption.disabled.obs;
    return Obx(() => ExpansionTile(
            title: Text(locale.speechToText),
            subtitle: Text(speechToTextOption.value.asText(locale)),
            backgroundColor: theme.colorScheme.surfaceVariant,
            childrenPadding: _tilePadding,
            children: <Widget>[
              const Divider(),
              DropdownButtonFormField2<SpeechToTextOption>(
                value: speechToTextOption.value,
                onChanged: (SpeechToTextOption? newValue) {
                  if (newValue != null) {
                    speechToTextOption.value = newValue;
                  }
                },
                items: SpeechToTextOption.values
                    .map<DropdownMenuItem<SpeechToTextOption>>(
                        (SpeechToTextOption value) {
                  return DropdownMenuItem<SpeechToTextOption>(
                    value: value,
                    child: Text(value.asText(locale)),
                  );
                }).toList(),
              ),
              const Divider(),
              TextFormField(
                decoration: InputDecoration(
                  border: const UnderlineInputBorder(),
                  labelText: locale.speechToTextURL,
                ),
              )
            ]));
  }

  Widget intentRecognition() {
    var intentRecognitionOption = IntentRecognitionOption.disabled.obs;
    return ExpansionTile(
      title: Text(locale.intentRecognition),
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
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
    var textToSpeechOption = TextToSpeechOption.disabled.obs;
    return ExpansionTile(
      title: Text(locale.textToSpeech),
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
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
    var audioPlayingOption = AudioPlayingOption.disabled.obs;
    return ExpansionTile(
      title: Text(locale.audioPlaying),
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
      children: <Widget>[
        Text(locale.audioPlaying),
        //Todo volume
      ],
    );
  }

  Widget dialogueManagement() {
    var dialogueManagementOption = DialogueManagementOption.disabled.obs;
    return ExpansionTile(
      title: Text(locale.dialogueManagement),
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
      children: <Widget>[
        Text(locale.dialogueManagement),
        //Todo not sure??
      ],
    );
  }

  Widget intentHandling() {
    var intentHandlingOption = IntentHandlingOption.disabled.obs;
    return ExpansionTile(
      title: Text(locale.intentHandling),
      backgroundColor: theme.colorScheme.surfaceVariant,
      childrenPadding: _tilePadding,
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
