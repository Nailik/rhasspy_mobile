import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

import '../../data/dialogue_management_options.dart';
import '../rhasspy_settings_screen.dart';

extension DialogueMangementWidget on RhasspySettingsScreenState {
  Widget dialogueManagement() {
    var dialogueManagementOption = DialogueManagementOption.disabled.obs;
    return listItem(DialogueManagementOptions(), dialogueManagementOption,
        locale.dialogueManagement, null);
  }
}
