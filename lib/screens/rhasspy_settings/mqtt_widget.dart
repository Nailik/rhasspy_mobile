import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../settings/settings.dart';
import '../rhasspy_settings_screen.dart';

extension MQTTWidget on RhasspySettingsScreenState {
  Widget mqtt() {
    var connectionStatus = false.obs;

    var widgets = <Widget>[
      const Divider(),
      hostTextField(),
      const Divider(),
      portTextField(),
      const Divider(),
      userNameTextField(),
      const Divider(),
      passwordTextField(),
      const Divider(),
      enableSSL(),
      const Divider(),
      sslCertificate(),
      Obx(() => Visibility(
            visible: mqttSSLSetting.value,
            child: const Divider(),
          )),
      ElevatedButton(child: Text(locale.checkConnection), onPressed: () {})
    ];

    return expandableListItem(
        title: locale.mqtt, subtitle: () => (connectionStatus.value ? locale.connected : locale.notConnected), children: widgets);
  }

  Widget hostTextField() {
    return autoSaveTextField(title: locale.host, setting: mqttHostSetting);
  }

  Widget portTextField() {
    return autoSaveTextField(title: locale.port, setting: mqttPortSetting);
  }

  Widget userNameTextField() {
    return autoSaveTextField(title: locale.userName, setting: mqttUserNameSetting);
  }

  Widget passwordTextField() {
    return ObxValue<RxBool>(
      (passwordHidden) => autoSaveTextField(
        title: locale.userName,
        setting: mqttPasswordSetting,
        obscureText: passwordHidden.value,
        suffixIcon: IconButton(
          icon: Icon(
            passwordHidden.value ? Icons.visibility_off : Icons.visibility,
          ),
          onPressed: () {
            passwordHidden.value = !passwordHidden.value;
          },
        ),
      ),
      true.obs,
    );
  }

  Widget enableSSL() {
    return autoSaveSwitchTile(title: locale.enableSSL, setting: mqttSSLSetting);
  }

  Widget sslCertificate() {
    return Obx(() => Visibility(
          visible: mqttSSLSetting.value,
          child: MaterialButton(
            child: Text(locale.chooseCertificate),
            textColor: theme.colorScheme.tertiary,
            onPressed: () {},
          ),
        ));
  }
}
