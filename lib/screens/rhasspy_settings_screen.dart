import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/data/audio_playing_options.dart';
import 'package:rhasspy_mobile/data/dialogue_management_options.dart';
import 'package:rhasspy_mobile/data/intent_handling_options.dart';
import 'package:rhasspy_mobile/data/intent_recognition_options.dart';
import 'package:rhasspy_mobile/data/speech_to_text_options.dart';

import '../data/option.dart';
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

  Widget mqtt() {
    var connectionStatus = false.obs;
    return expandableListItem(
      title: locale.mqtt,
      subtitle: () =>
          (connectionStatus.value ? locale.connected : locale.notConnected),
      children: <Widget>[
        const Divider(),
        TextField(decoration: defaultDecoration(locale.host)),
        const Divider(),
        TextField(decoration: defaultDecoration(locale.port)),
        const Divider(),
        TextField(decoration: defaultDecoration(locale.userName)),
        const Divider(),
        ObxValue<RxBool>(
            (passwordHidden) => TextFormField(
                  obscureText: passwordHidden.value,
                  decoration: InputDecoration(
                    border: const OutlineInputBorder(),
                    labelText: locale.password,
                    suffixIcon: IconButton(
                      icon: Icon(
                        passwordHidden.value
                            ? Icons.visibility_off
                            : Icons.visibility,
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
    var audioRecordingUdpOutput = false.obs;
    return expandableListItem(
      title: locale.audioRecording,
      subtitle: () => audioRecordingUdpOutput.value
          ? locale.udpAudioOutput
          : locale.udpAudioOutputOff,
      children: <Widget>[
        const Divider(),
        Obx(() => SwitchListTile(
            value: audioRecordingUdpOutput.value,
            onChanged: (value) {
              audioRecordingUdpOutput.value = value;
            },
            title: Text(locale.udpAudioOutput),
            subtitle: Text(locale.udpAudioOutputDetail))),
        const Divider(),
        TextField(
          enabled: audioRecordingUdpOutput.value,
          decoration: defaultDecoration(locale.host),
        ),
        const Divider(),
        TextField(
          enabled: audioRecordingUdpOutput.value,
          decoration: defaultDecoration(locale.port),
        ),
        //TODO silence detection
      ],
    );
  }

  Widget wakeWord() {
    var wakeWordOption = WakeWordOption.disabled.obs;
    return Obx(() => listItem(WakeWordOptions(), wakeWordOption,
        locale.wakeWord, localWakeWordSettings(wakeWordOption.value)));
  }

  Widget localWakeWordSettings(WakeWordOption wakeWordOption) {
    if (wakeWordOption == WakeWordOption.localPorcupine) {
      return Column(children: [
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
    var wakeWordKeywordOption = "jarvis".obs;
    var wakeWordKeywordOptions = ["jarvis", "porcupine"];

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
              padding: const EdgeInsets.fromLTRB(0, 16, 0, 0),
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
    return listItem(
        SpeechToTextOptions(),
        speechToTextOption,
        locale.speechToText,
        Obx(() => speechToTextSettings(speechToTextOption.value)));
  }

  Widget speechToTextSettings(SpeechToTextOption speechToTextOption) {
    if (speechToTextOption == SpeechToTextOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(decoration: defaultDecoration(locale.speechToTextURL))
      ]);
    } else {
      return Container();
    }
  }

  Widget intentRecognition() {
    var intentRecognitionOption = IntentRecognitionOption.disabled.obs;
    return listItem(
        IntentRecognitionOptions(),
        intentRecognitionOption,
        locale.intentRecognition,
        Obx(() => intentRecognitionSettings(intentRecognitionOption.value)));
  }

  Widget intentRecognitionSettings(IntentRecognitionOption speechToTextOption) {
    if (speechToTextOption == IntentRecognitionOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(
            decoration: defaultDecoration(locale.rhasspyTextToIntentURL))
      ]);
    } else {
      return Container();
    }
  }

  Widget textToSpeech() {
    var textToSpeechOption = TextToSpeechOption.disabled.obs;
    return listItem(
        TextToSpeechOptions(),
        textToSpeechOption,
        locale.textToSpeech,
        Obx(() => textToSpeechSettings(textToSpeechOption.value)));
  }

  Widget textToSpeechSettings(TextToSpeechOption textToSpeechOption) {
    if (textToSpeechOption == TextToSpeechOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(
            decoration: defaultDecoration(locale.rhasspyTextToSpeechURL))
      ]);
    } else {
      return Container();
    }
  }

  Widget audioPlaying() {
    var audioPlayingOption = AudioPlayingOption.disabled.obs;
    return listItem(
        AudioPlayingOptions(),
        audioPlayingOption,
        locale.audioPlaying,
        Obx(() => audioPlayingSettings(audioPlayingOption.value)));
  }

  Widget audioPlayingSettings(AudioPlayingOption audioPlayingOption) {
    if (audioPlayingOption == AudioPlayingOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(decoration: defaultDecoration(locale.audioOutputURL))
      ]);
    } else {
      return Container();
    }
  }

  Widget dialogueManagement() {
    var dialogueManagementOption = DialogueManagementOption.disabled.obs;
    return listItem(DialogueManagementOptions(), dialogueManagementOption,
        locale.dialogueManagement, null);
  }

  Widget intentHandling() {
    var intentHandlingOption = IntentHandlingOption.disabled.obs;
    return listItem(
        IntentHandlingOptions(),
        intentHandlingOption,
        locale.intentHandling,
        Obx(() => intentHandlingSettings(intentHandlingOption.value)));
  }

  Widget intentHandlingSettings(IntentHandlingOption intentHandlingOption) {
    var homeAssistantIntentOption = HomeAssistantIntent.events.obs;

    if (intentHandlingOption == IntentHandlingOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        TextFormField(decoration: defaultDecoration(locale.remoteURL))
      ]);
    } else if (intentHandlingOption == IntentHandlingOption.homeAssistant) {
      return Column(children: [
        const Divider(),
        TextFormField(decoration: defaultDecoration(locale.hassURL)),
        const Divider(thickness: 0),
        TextFormField(decoration: defaultDecoration(locale.accessToken)),
        const Divider(),
        ListTile(
            title: Text(locale.homeAssistantEvents),
            leading: Obx(() => Radio<HomeAssistantIntent>(
                  value: HomeAssistantIntent.events,
                  groupValue: homeAssistantIntentOption.value,
                  onChanged: (HomeAssistantIntent? value) {
                    if (value != null) {
                      homeAssistantIntentOption.value =
                          HomeAssistantIntent.events;
                    }
                  },
                ))),
        ListTile(
            title: Text(locale.homeAssistantIntents),
            leading: Obx(() => Radio<HomeAssistantIntent>(
                  value: HomeAssistantIntent.intents,
                  groupValue: homeAssistantIntentOption.value,
                  onChanged: (HomeAssistantIntent? value) {
                    if (value != null) {
                      homeAssistantIntentOption.value =
                          HomeAssistantIntent.intents;
                    }
                  },
                ))),
      ]);
    } else {
      return Container();
    }
  }

  Widget listItem<T>(
      Option<T> option, Rx<T> optionValue, String title, Widget? children) {
    var childWidgets = <Widget>[
      const Divider(),
      Obx(() => DropdownButtonFormField2<T>(
            value: optionValue.value,
            onChanged: (T? newValue) {
              if (newValue != null) {
                optionValue.value = newValue;
              }
            },
            items: option.options.map<DropdownMenuItem<T>>((T value) {
              return DropdownMenuItem<T>(
                value: value,
                child: Text(option.asText(value, locale)),
              );
            }).toList(),
          )),
    ];

    if (children != null) {
      childWidgets.add(children);
    }

    childWidgets.add(const SizedBox(height: 8));

    return expandableListItem(
        title: title,
        subtitle: () => option.asText(optionValue.value, locale),
        children: childWidgets);
  }

  Widget expandableListItem(
      {required String title,
      required String Function() subtitle,
      required List<Widget> children}) {
    Widget? subtitleWidget;
    subtitleWidget = Obx(() => Text(subtitle()));

    return ExpansionTile(
        title: Text(title),
        subtitle: subtitleWidget,
        backgroundColor: theme.colorScheme.surfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: children);
  }

  InputDecoration defaultDecoration(String labelText) {
    return InputDecoration(
      border: const OutlineInputBorder(),
      labelText: labelText,
    );
  }
}
