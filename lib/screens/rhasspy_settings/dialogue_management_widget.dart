import 'package:flutter/cupertino.dart';

import '../../data/dialogue_management_options.dart';
import '../../settings/settings.dart';
import '../custom_state.dart';

extension DialogueMangementWidget on CustomState {
  Widget dialogueManagement() {
    return autoSaveExpandableDropDownListItem(
        title: locale.dialogueManagement, option: DialogueManagementOptions(), setting: dialogueManagementSetting);
  }
}
