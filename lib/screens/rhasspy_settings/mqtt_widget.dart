import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../rhasspy_settings_screen.dart';

extension MQTTWidget on RhasspySettingsScreenState {
  Widget mqtt() {
    var mqttSSL = false.obs;
    var connectionStatus = false.obs;

    var widgets = <Widget>[
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
                      passwordHidden.value ? Icons.visibility_off : Icons.visibility,
                    ),
                    onPressed: () {
                      passwordHidden.value = !passwordHidden.value;
                    },
                  ),
                ),
              ),
          true.obs),
      const Divider(),
      Obx(() => SwitchListTile(
          title: Text(locale.enableSSL),
          value: mqttSSL.value,
          onChanged: (value) {
            mqttSSL.value = value;
          })),
      const Divider(),
      Obx(() => Visibility(
            visible: mqttSSL.value,
            child: MaterialButton(
              child: Text(locale.chooseCertificate),
              textColor: theme.colorScheme.tertiary,
              onPressed: () {},
            ),
          )),
      Obx(() => Visibility(
            visible: mqttSSL.value,
            child: const Divider(),
          )),
      ElevatedButton(child: Text(locale.checkConnection), onPressed: () {})
    ];

    return expandableListItem(title: locale.mqtt, subtitle: () => (connectionStatus.value ? locale.connected : locale.notConnected), children: widgets);
  }
}
