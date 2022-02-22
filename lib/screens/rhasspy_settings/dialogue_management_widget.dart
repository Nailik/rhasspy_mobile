import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

import '../../data/dialogue_management_options.dart';
import '../custom_state.dart';

extension DialogueMangementWidget on CustomState {
  Widget dialogueManagement() {
    var dialogueManagementOption = DialogueManagementOption.disabled.obs;
    return expandableDropDownListItem(DialogueManagementOptions(), dialogueManagementOption, locale.dialogueManagement);
  }
}
