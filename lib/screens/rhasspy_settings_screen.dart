import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
import 'package:get/get_rx/src/rx_types/rx_types.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_playing_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/audio_recording_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/dialogue_management_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_handling_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/intent_recognition_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/mqtt_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/speech_to_text_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/text_to_speech_widget.dart';
import 'package:rhasspy_mobile/screens/rhasspy_settings/wake_word_widget.dart';

import '../data/option.dart';

class RhasspySettingsScreen extends StatefulWidget {
  const RhasspySettingsScreen({Key? key}) : super(key: key);

  @override
  State<RhasspySettingsScreen> createState() => RhasspySettingsScreenState();
}

class RhasspySettingsScreenState extends State<RhasspySettingsScreen> {
  late AppLocalizations locale;
  late ThemeData theme;

  @override
  Widget build(BuildContext context) {
    locale = AppLocalizations.of(context)!;
    theme = Theme.of(context);
    return content();
  }

  Widget content() {
    final List<Widget> items = <Widget>[siteId(), mqtt(), audioRecording(), wakeWord(), speechToText(), intentRecognition(), textToSpeech(), audioPlaying(), dialogueManagement(), intentHandling()];

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

  InputDecoration defaultDecoration(String labelText) {
    return InputDecoration(
      border: const OutlineInputBorder(),
      labelText: labelText,
    );
  }

  Widget listItem<T>(Option<T> option, Rx<T> optionValue, String title, Widget? children) {
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

    return expandableListItem(title: title, subtitle: () => option.asText(optionValue.value, locale), children: childWidgets);
  }

  Widget expandableListItem({required String title, required String Function() subtitle, required List<Widget> children}) {
    Widget? subtitleWidget;
    subtitleWidget = Obx(() => Text(subtitle()));

    return ExpansionTile(
        title: Text(title),
        subtitle: subtitleWidget,
        backgroundColor: theme.colorScheme.surfaceVariant,
        textColor: theme.colorScheme.onSurfaceVariant,
        childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
        children: children);
  }
}
