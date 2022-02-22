import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../settings/settings.dart';
import '../custom_state.dart';

extension HTTPSettingsWidget on CustomState {
  Widget rhasspyHTTPSettings() {
    return expandableListItem(
        title: locale.httpSSL,
        subtitle: () {
          return httpSSLSetting.value ? locale.enabled : locale.disabled;
        },
        children: [
          Obx(() => SwitchListTile(
              title: Text(locale.enableSSL),
              value: httpSSLSetting.value,
              onChanged: (value) {
                httpSSLSetting.setValue(value);
              })),
          const Divider(),
          Obx(() => Visibility(
                visible: httpSSLSetting.value,
                child: MaterialButton(
                  child: Text(locale.chooseCertificate),
                  textColor: theme.colorScheme.tertiary,
                  onPressed: () {},
                ),
              )),
        ]);
  }
}
