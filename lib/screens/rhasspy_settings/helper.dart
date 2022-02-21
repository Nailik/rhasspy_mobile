import 'package:dropdown_button2/dropdown_button2.dart';
import 'package:flutter/material.dart';
import 'package:get/get_rx/src/rx_types/rx_types.dart';
import 'package:get/get_state_manager/src/rx_flutter/rx_obx_widget.dart';

import '../../data/option.dart';
import '../../main.dart';

InputDecoration defaultDecoration(String labelText) {
  return InputDecoration(
    border: const OutlineInputBorder(),
    labelText: labelText,
  );
}

Widget listItem<T>(
    Option<T> option, Rx<T> optionValue, String title, Widget? children) {
  var childWidgets = <Widget>[
    const Divider(),
    Obx(() => DropdownButtonFormField2<T>(
          value: optionValue.value,
          onChanged: (T? newValue) {
            if (newValue != null) {
              optionValue.value = newValue;
            }
          },
          items: option.options.map<DropdownMenuItem<T>>((T value) {
            return DropdownMenuItem<T>(
              value: value,
              child: Text(option.asText(value, getLocale())),
            );
          }).toList(),
        )),
  ];

  if (children != null) {
    childWidgets.add(children);
  }

  childWidgets.add(const SizedBox(height: 8));

  return expandableListItem(
      title: title,
      subtitle: () => option.asText(optionValue.value, getLocale()),
      children: childWidgets);
}

Widget expandableListItem(
    {required String title,
    required String Function() subtitle,
    required List<Widget> children}) {
  Widget? subtitleWidget;
  subtitleWidget = Obx(() => Text(subtitle()));

  return ExpansionTile(
      title: Text(title),
      subtitle: subtitleWidget,
      backgroundColor: getTheme().colorScheme.surfaceVariant,
      textColor: getTheme().colorScheme.onSurfaceVariant,
      childrenPadding: const EdgeInsets.fromLTRB(8, 0, 8, 8),
      children: children);
}
