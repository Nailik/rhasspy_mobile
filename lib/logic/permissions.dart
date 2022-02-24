import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';

///request permission
///should show rational -> dialog
///granted -> finished
///

var shouldShowRationale = false.obs;

requestPermission(Permission permission, BuildContext context, Function(bool) onResult) async {
  if (await permission.shouldShowRequestRationale) {
    await showRequestRationale(permission, context, onResult);
  } else {
    await requestSystemPermission(permission, context, onResult);
  }
}

showRequestRationale(Permission permission, BuildContext context, Function(bool) onResult) async {
  showDialog<String>(
    context: context,
    builder: (BuildContext context2) => AlertDialog(
      title: const Text('AlertDialog Title'),
      content: const Text('AlertDialog description'),
      actions: <Widget>[
        TextButton(
          onPressed: () {
            Navigator.pop(context2, 'Cancel');
            onResult(false);
          },
          child: const Text('Cancel'),
        ),
        TextButton(
          onPressed: () {
            Navigator.pop(context2, 'OK');
            requestSystemPermission(permission, context, onResult);
          },
          child: const Text('OK'),
        ),
      ],
    ),
  );
}

requestSystemPermission(Permission permission, BuildContext context, Function(bool) onGranted) async {
  var granted = await permission.request().isGranted;
  onGranted(granted);
  if (!granted) {
    requestPermissionSnackbar(permission, context);
  }
}

requestPermissionSnackbar(Permission permission, BuildContext context) {
  final snackBar = SnackBar(
    content: const Text('Yay! A SnackBar!'),
    action: SnackBarAction(
      label: 'Undo',
      onPressed: () => openAppSettings(),
    ),
  );
  ScaffoldMessenger.of(context).showSnackBar(snackBar);
}
