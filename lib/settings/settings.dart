import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

final httpSSLSetting = Setting("httpSSL", false);
final siteIdSetting = Setting("siteId", "");

final mqttHostSetting = Setting("mqttHost", "");
final mqttPortSetting = Setting("mqttPort", "");
final mqttUserNameSetting = Setting("mqttUserName", "");
final mqttPasswordSetting = Setting("mqttPassword", "");
final mqttSSLSetting = Setting("mqttSSL", false);

final udpAudioSetting = Setting("udpAudio", false);
final udpAudioHostSetting = Setting("udpAudioHost", "");
final udpAudioPortSetting = Setting("udpAudioPort", "");

class Setting<T> extends Rx<T> {
  String id;

  Setting(this.id, T initial) : super(initial) {
    value = GetStorage().read<T>(id) ?? initial;
  }

  void setValue(T value) {
    GetStorage().write(id, value);
    this.value = value;
  }
}
