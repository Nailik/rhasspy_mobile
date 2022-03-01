import 'package:flutter/cupertino.dart';
import 'package:rhasspy_mobile/logic/options/dialogue_management_options.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/custom_state.dart';

extension DialogueMangementWidget on CustomState {
  Widget dialogueManagement() {
    return autoSaveExpandableDropDownListItem(
        title: locale.dialogueManagement, option: DialogueManagementOptions(), setting: dialogueManagementSetting);
  }
}
