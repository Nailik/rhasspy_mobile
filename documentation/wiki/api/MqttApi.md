# MQTT Api

The MQTT API subscribes and publishes to a given in MQTT Broker. <br /> More information can be
found on
the [rhasspy mqtt api documentation](https://rhasspy.readthedocs.io/en/latest/reference/#mqtt-api).
<br /> Some topics limited in functionality, Unsupported means that those values are ignored.
SiteId has to match the siteId in the App Configuration in order for any topic to be recognized.
If there is a running session also the sessionId has to mach the current session depending on the
selected Dialog Management.

## Subscription Topics

Those topics are subscribed to and result in an Event that is consumed by the [[Dialog Management]]
or Settings are changed (Settings -> Device).
The
official [rhasspy mqtt api documentation](https://rhasspy.readthedocs.io/en/latest/reference/#mqtt-api)
contains more data, those that is not listed here is currently being ignored.

| Topic                                               | Data                                                       | Event                     | Settings                        |
|-----------------------------------------------------|------------------------------------------------------------|---------------------------|---------------------------------|
| `hermes/dialogueManager/startSession`               | siteId: String                                             | `StartSession`            |                                 |
| `hermes/dialogueManager/endSession`                 | siteId: String </br> sessionId: String                     | `EndSession`              |                                 |
| `hermes/dialogueManager/sessionStarted`             | siteId: String </br> sessionId: String                     | `SessionStarted`          |                                 |
| `hermes/dialogueManager/sessionEnded`               | siteId: String </br> sessionId: String                     | `SessionEnded`            |                                 |
| `hermes/hotword/toggleOn`                           | siteId: String                                             |                           | enable Wake Word Service        |
| `hermes/hotword/toggleOff`                          | siteId: String                                             |                           | disabled Wake Word Service      |
| `hermes/hotword/<wakewordId>/detected`              | siteId: String                                             | `WakeWordDetected`        |                                 |
| `hermes/asr/startListening`                         | siteId: String </br> sendAudioCaptured: Boolean            | `StartListening`          |                                 |
| `hermes/asr/stopListening`                          | siteId: String </br> sessionId: String                     | `StopListening`           |                                 |
| `hermes/asr/textCaptured`                           | siteId: String </br> sessionId: String  </br> text: String | `AsrTextCaptured`         |                                 |
| `hermes/error/asr`                                  | siteId: String </br> sessionId: String                     | `AsrError`                |                                 |
| `hermes/intent/<intentName>`                        | siteId: String </br> sessionId: String                     | `IntentRecognitionResult` |                                 |
| `hermes/nlu/intentNotRecognized`                    | siteId: String </br> sessionId: String                     | `IntentRecognitionError`  |                                 |
| `hermes/handle/toggleOn`                            | siteId: String                                             |                           | enable Intent Handling Service  |
| `hermes/handle/toggleOff`                           | siteId: String                                             |                           | disable Intent Handling Service |
| `hermes/audioServer/<siteId>/playBytes/<requestId>` |                                                            | `PlayAudio`               |                                 |
| `hermes/audioServer/<siteId>/playFinished`          |                                                            | `PlayFinished`            |                                 |
| `hermes/audioServer/toggleOn`                       | siteId: String                                             |                           | enable Audio Playing Service    |
| `hermes/audioServer/toggleOff`                      | siteId: String                                             |                           | disable Audio Playing Service   |
| `rhasspy/audioServer/setVolume`                     | siteId: String </br> volume: Float                         |                           | set App Audio Volume            |

## Publish Topics

Those topics are information published to. Either by a service or triggered because of an event

| Topic                                               | Data                                                                                            | Event                    | Service                     |
|-----------------------------------------------------|-------------------------------------------------------------------------------------------------|--------------------------|-----------------------------|
| `hermes/dialogueManager/sessionStarted`             | siteId: String </br> sessionId: String                                                          | `SessionStarted`         |                             |                                   
| `hermes/dialogueManager/sessionEnded`               | siteId: String </br> sessionId: String                                                          | `SessionEnded`           |                             |                        
| `hermes/dialogueManager/intentNotRecognized`        | siteId: String </br> sessionId: String                                                          | `IntentRecognitionError` |                             |             
| `hermes/asr/startListening`                         | siteId: String </br> sessionId: String </br> stopOnSilence: bool </br> sendAudioCaptured : bool |                          | `SpeechToText Service`      |    
| `hermes/asr/stopListening`                          | siteId: String </br> sessionId: String                                                          |                          | `SpeechToText Service`      |    
| `hermes/asr/textCaptured`                           | siteId: String </br> sessionId: String </br> text: String                                       | `AsrTextCaptured`        |                             |    
| `hermes/error/asr`                                  | siteId: String </br> sessionId: String                                                          | `AsrError`               |                             |    
| `rhasspy/asr/<siteId>/<sessionId>/audioCaptured`    | bytes (with WAV header)                                                                         | `StopListening`          |                             |    
| `hermes/audioServer/<siteId>/audioFrame`            | bytes (with WAV header)                                                                         |                          | `SpeechToText Service`      |    
| `hermes/hotword/<wakewordId>/detected`              | siteId: String </br> modelId: String                                                            | `WakeWordDetected`       |                             |    
| `hermes/error/hotword`                              | siteId: String </br> error: String                                                              |                          | `WakeWord Service`          |    
| `hermes/nlu/query`                                  | siteId: String </br> sessionId: String </br> input: String                                      |                          | `IntentRecognition Service` |    
| `hermes/tts/say`                                    | siteId: String </br> sessionId: String? </br> text: String                                      |                          | `TextToSpeech Service`      |    
| `hermes/audioServer/<siteId>/playBytes/<requestId>` | bytes (with WAV header)                                                                         |                          | `AudioPlaying Service`      |    
| `hermes/audioServer/<siteId>/playFinished`          |                                                                                                 | `PlayFinished`           | `AudioPlaying Service`      |                              