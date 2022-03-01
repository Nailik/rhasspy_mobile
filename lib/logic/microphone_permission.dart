import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';
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
  var locale = AppLocalizations.of(context)!;
  showDialog<String>(
    context: context,
    builder: (BuildContext buildContext) => AlertDialog(
      title: Text(locale.microphonePermissionDialogTitle),
      content: Text(locale.microphonePermissionDialogMessage),
      actions: <Widget>[
        TextButton(
          onPressed: () {
            Navigator.pop(buildContext);
            microphonePermissionUpdated();
          },
          child: Text(locale.cancel),
        ),
        TextButton(
          onPressed: () {
            Navigator.pop(buildContext);
            requestSystemMicrophonePermission(context);
          },
          child: Text(locale.ok),
        ),
      ],
    ),
  );
}

requestSystemMicrophonePermission(BuildContext context) async {
  var locale = AppLocalizations.of(context)!;
  if (!await Permission.microphone.request().isGranted) {
    final snackBar = SnackBar(
      content: Text(locale.microphonePermissionDenied),
      action: SnackBarAction(
        label: locale.ok,
        onPressed: () => openAppSettings(),
      ),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }
  microphonePermissionUpdated();
}
