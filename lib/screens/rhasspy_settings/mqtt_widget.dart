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

    return expandableListItem(title: locale.mqtt, subtitle: () => (connectionStatus.value ? locale.connected : locale.notConnected), children: widgets);
  }

  Widget hostTextField() {
    final _hostController = TextEditingController(text: mqttHostSetting.value);
    changeNotifierList.add(_hostController);
    _hostController.addListener(() {
      mqttHostSetting.setValue(_hostController.text);
    });
    return TextField(controller: _hostController, decoration: defaultDecoration(locale.host));
  }

  Widget portTextField() {
    final _portController = TextEditingController(text: mqttPortSetting.value);
    changeNotifierList.add(_portController);
    _portController.addListener(() {
      mqttPortSetting.setValue(_portController.text);
    });
    return TextField(controller: _portController, decoration: defaultDecoration(locale.port));
  }

  Widget userNameTextField() {
    final _userNameController = TextEditingController(text: mqttUserNameSetting.value);
    changeNotifierList.add(_userNameController);
    _userNameController.addListener(() {
      mqttUserNameSetting.setValue(_userNameController.text);
    });
    return TextField(controller: _userNameController, decoration: defaultDecoration(locale.userName));
  }

  Widget passwordTextField() {
    final _passwordController = TextEditingController(text: mqttPasswordSetting.value);
    changeNotifierList.add(_passwordController);
    _passwordController.addListener(() {
      mqttPasswordSetting.setValue(_passwordController.text);
    });
    return ObxValue<RxBool>(
        (passwordHidden) => TextFormField(
              controller: _passwordController,
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
        true.obs);
  }

  Widget enableSSL() {
    return Obx(() => SwitchListTile(
        title: Text(locale.enableSSL),
        value: mqttSSLSetting.value,
        onChanged: (value) {
          mqttSSLSetting.setValue(value);
        }));
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
