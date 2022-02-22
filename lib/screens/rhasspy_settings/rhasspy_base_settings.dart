import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../custom_state.dart';

extension HTTPSettingsWidget on CustomState {
  Widget rhasspyHTTPSettings() {
    var httpSSL = false.obs;
    return expandableListItem(
        title: "HTTP SSL",
        subtitle: () {
          return httpSSL.value ? locale.enabled : locale.disabled;
        },
        children: [
          Obx(() => SwitchListTile(
              title: Text(locale.enableSSL),
              value: httpSSL.value,
              onChanged: (value) {
                httpSSL.value = value;
              })),
          const Divider(),
          Obx(() => Visibility(
                visible: httpSSL.value,
                child: MaterialButton(
                  child: Text(locale.chooseCertificate),
                  textColor: theme.colorScheme.tertiary,
                  onPressed: () {},
                ),
              )),
        ]);
  }
}
