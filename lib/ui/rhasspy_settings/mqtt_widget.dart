import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:rhasspy_mobile/logic/settings.dart';
import 'package:rhasspy_mobile/ui/screens/rhasspy_settings_screen.dart';

extension MQTTWidget on RhasspySettingsScreenState {
  Widget mqtt() {
    var connectionStatus = false.obs;

    var widgets = <Widget>[
      const Divider(),

      ///host
      autoSaveTextField(title: locale.host, setting: mqttHostSetting),
      const Divider(),

      ///port
      autoSaveTextField(title: locale.port, setting: mqttPortSetting),
      const Divider(),

      ///username
      autoSaveTextField(title: locale.userName, setting: mqttUserNameSetting),
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

  Widget passwordTextField() {
    final _controller = TextEditingController(text: mqttPasswordSetting.value);
    changeNotifierList.add(_controller);
    _controller.addListener(() {
      mqttPasswordSetting.setValue(_controller.text);
    });

    return ObxValue<RxBool>(
      (passwordHidden) => TextField(
        controller: _controller,
        obscureText: passwordHidden.value,
        decoration: defaultDecoration(
          locale.password,
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
