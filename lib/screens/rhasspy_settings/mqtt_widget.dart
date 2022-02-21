import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../rhasspy_settings_screen.dart';
import 'helper.dart';

extension MQTTWidget on RhasspySettingsScreenState {
  Widget mqtt() {
    var connectionStatus = false.obs;
    return expandableListItem(
      title: locale.mqtt,
      subtitle: () =>
          (connectionStatus.value ? locale.connected : locale.notConnected),
      children: <Widget>[
        const Divider(),
        TextField(decoration: defaultDecoration(locale.host)),
        const Divider(),
        TextField(decoration: defaultDecoration(locale.port)),
        const Divider(),
        TextField(decoration: defaultDecoration(locale.userName)),
        const Divider(),
        ObxValue<RxBool>(
            (passwordHidden) => TextFormField(
                  obscureText: passwordHidden.value,
                  decoration: InputDecoration(
                    border: const OutlineInputBorder(),
                    labelText: locale.password,
                    suffixIcon: IconButton(
                      icon: Icon(
                        passwordHidden.value
                            ? Icons.visibility_off
                            : Icons.visibility,
                      ),
                      onPressed: () {
                        passwordHidden.value = !passwordHidden.value;
                      },
                    ),
                  ),
                ),
            true.obs),
        const Divider(),
        ElevatedButton(child: Text(locale.checkConnection), onPressed: () {})
      ],
    );
  }
}
