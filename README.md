# Rhasspy mobile

This will be a Rhasspy satellite on a mobile phone. The original idea is from [rhasspy-mobile-app](https://github.com/razzo04/rhasspy-mobile-app).

## Getting Started

For setup of server and satellite configuration check the official documentation
of [Rhasspy](https://rhasspy.readthedocs.io/en/latest/tutorials/#server-with-satellites).

## Planned features

| Feature  | Android | iOS | Web |
| ------------- | ------------- | ------------- | ------------- |
| Audio Recording | :white_check_mark: | :x: | :x: |
| Silence Detection | :white_check_mark: | :x: | :x: |
| Remote HTTP(s)  | :white_check_mark: | :x: | :x: |
| Local (incoming) HTTP(s)  | :white_check_mark: | :x: | :x: |
| Remote SSL (MQTT, HTTP Server)  | :x: | :x: | :x: |
| WakeWord Remote (MQTT/UDP) | :white_check_mark: | :x: | :x: |
| WakeWord Local (Porcupine) | :white_check_mark: | :x: | :x: |
| WakeWord Indication (Sound, Visual)  | :white_check_mark: | :x: | :x: |

# Information About current status

Local WakeWord, Background Service and Indications are in develop, microphone permission needs to be accepted manually for now via app settings.

# About

While developing i switched from Flutter to Kotlin Multiplatform Mobile for the following reason:

For the WakeWord detection / whole app to run in background continuously a foreground service is necessary. Flutter and all plugins i found use an
isolate for this, but therefore execution of the Porcupine Plugin is not possible anymore because there are several issues calling a MethodChannel
from within an isolate. Using Porcupine Android would require to start multiple service (WakeWord detection and the other, shared code like mqtt). On
Kotlin Multiplatform one can create an "expect" WakeWord detection class and implement it "actual" native on both platforms, but simply call it from
the shared code which will host the Foreground Service.
