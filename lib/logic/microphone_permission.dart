import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:rhasspy_mobile/logic/services.dart';

var shouldShowRationale = false.obs;

requestMicrophonePermission(BuildContext context) async {
  if (await Permission.microphone.shouldShowRequestRationale) {
    await showInformationDialog(context);
  } else {
    await requestSystemMicrophonePermission(context);
  }
}

showInformationDialog(BuildContext context) async {
  showDialog<String>(
    context: context,
    builder: (BuildContext buildContext) => AlertDialog(
      title: const Text('AlertDialog Title'),
      content: const Text('AlertDialog description'),
      actions: <Widget>[
        TextButton(
          onPressed: () {
            Navigator.pop(buildContext, 'Cancel');
            microphonePermissionUpdated();
          },
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () {
            Navigator.pop(buildContext, 'OK');
            requestSystemMicrophonePermission(context);
          },
          child: const Text('OK'),
        ),
      ],
    ),
  );
}

requestSystemMicrophonePermission(BuildContext context) async {
  if (!await Permission.microphone.request().isGranted) {
    final snackBar = SnackBar(
      content: const Text('Yay! A SnackBar!'),
      action: SnackBarAction(
        label: 'Undo',
        onPressed: () => openAppSettings(),
      ),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }
  microphonePermissionUpdated();
}
