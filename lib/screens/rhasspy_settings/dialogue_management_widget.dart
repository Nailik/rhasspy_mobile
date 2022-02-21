import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/main.dart';

import '../../data/dialogue_management_options.dart';
import 'helper.dart';

Widget dialogueManagement() {
  var dialogueManagementOption = DialogueManagementOption.disabled.obs;
  return listItem(DialogueManagementOptions(), dialogueManagementOption,
      getLocale().dialogueManagement, null);
}
