# Rhasspy mobile

This will be a Rhasspy satellite on a mobile phone.
The originial idea is from [rhasspy-mobile-app](https://github.com/razzo04/rhasspy-mobile-app).

## Getting Started

For setup of server and satellite configuration check the official documentation of [Rhasspy](https://rhasspy.readthedocs.io/en/latest/tutorials/#server-with-satellites).

## Planned features

| Feature  | Android | iOS | Web |
| ------------- | ------------- | ------------- | ------------- |
| Audio Recording | :x: | :x: | :x: |
| Silence Detection | :x: | :x: | :x: |
| Remote HTTP(s)  | :x: | :x: | :x: |
| Remote MQTT(SSL)  | :x: | :x: | :x: |
| Wakeword Remote (HTTP/MQTT/UDP) | :x: | :x: | :x: |
| Wakeword Local (Porcupine) | :x: | :x: | :x: |
| Wakeword Indication (Sound, Visual)  | :x: | :x: | :x: |

# Tech information
While developing i switched from Flutter to Kotlin Multiplatform Mobile for the following reason:

For the wakeword detection / whole app to run in background continously a foreground service is necessary.
Flutter and all plugins i found use an isolate for this, but therefore execution of the Porcupine Plugin is not possible anymore
because there are several issues calling a MethodChannel from within an isolate.
Using Porcupine Android would require to start multiple service (wakeword detection and the other, shared code like mqtt).
On Kotlin Multiplatform one can create an "expect" Wakeword detection class and implement it "actual" native on both platforms, but simply call it from the shared code which will host the Foreground Service.
