import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../data/intent_handling_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

extension IntentHandlingWidget on CustomState {
  Widget intentHandling() {
    return autoSaveExpandableDropDownListItem(
        title: locale.intentHandling,
        option: IntentHandlingOptions(),
        setting: intentHandlingSetting,
        child: Obx(() => intentHandlingSettings(intentHandlingSetting.value)));
  }

  Widget intentHandlingSettings(IntentHandlingOption intentHandlingOption) {
    if (intentHandlingOption == IntentHandlingOption.remoteHTTP) {
      return Column(children: [
        const Divider(),
        autoSaveTextField(title: locale.remoteURL, setting: intentHandlingHTTPURLSetting),
      ]);
    } else if (intentHandlingOption == IntentHandlingOption.homeAssistant) {
      return Column(children: [
        const Divider(),
        autoSaveTextField(title: locale.hassURL, setting: intentHandlingHassURLSetting),
        const Divider(thickness: 0),
        autoSaveTextField(title: locale.accessToken, setting: intentHandlingHassTokenSetting),
        const Divider(),
        ListTile(
            title: Text(locale.homeAssistantEvents),
            leading: Obx(() => Radio<HomeAssistantIntent>(
                  value: HomeAssistantIntent.events,
                  groupValue: intentHandlingHassIntentSetting.value,
                  onChanged: (HomeAssistantIntent? value) {
                    if (value != null) {
                      intentHandlingHassIntentSetting.setValue(HomeAssistantIntent.events);
                    }
                  },
                ))),
        ListTile(
            title: Text(locale.homeAssistantIntents),
            leading: Obx(() => Radio<HomeAssistantIntent>(
                  value: HomeAssistantIntent.intents,
                  groupValue: intentHandlingHassIntentSetting.value,
                  onChanged: (HomeAssistantIntent? value) {
                    if (value != null) {
                      intentHandlingHassIntentSetting.setValue(HomeAssistantIntent.intents);
                    }
                  },
                ))),
      ]);
    } else {
      return Container();
    }
  }
}
