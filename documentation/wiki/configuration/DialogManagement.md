# Dialog Management

The Dialog Management handles that the services are called in order to create a Flow from wake word
to intent recognition and handling. <br />
Events can either be trigger by doing Actions in the app or by using the [[Http Api]]
pr [[MQTT Api]]

## Options

| Option        | Description                                                                                                          | 
|---------------|----------------------------------------------------------------------------------------------------------------------|
| `Local`       | Some Events are only allowed from MQTT when they match the sessionId and the specific service is set to Remote MQTT. |
| `Remote MQTT` | All Events are allowed, but the sessionId must match if not in Idle State.                                           |
| `Disabled`    | Ignores all States and handles only some events, more information below.                                             |

## States

| State                 | Information                                         |
|-----------------------|-----------------------------------------------------|
| `Idle`                | listening for wake word                             | 
| `Recording Intent`    | recording audio until stop or silence/text detected |
| `Transcribing Intent` | converts recording into text                        |
| `Recognizing Intent`  | converts text into intent and handles it            |
| `Playing Audio`       | plays audio from mqtt/http api                      |

## Events

Some Events are handled internally and therefore ignored when coming from external resources.</br>
The MQTT ✓ shows which status information is published to MQTT if the Event was not send by MQTT.

| Event                     | Action                              | MQTT |
|---------------------------|-------------------------------------|------|
| `WakeWordDetected`        | generate sessionId, start recording | ✓    |
| `EndSession`              | stop recording and remove sessionId |      |
| `AsrTextCaptured`         | text to intent                      | ✓    |
| `AsrError`                | EndSession                          | ✓    |
| `SessionStarted`          | ignored                             | ✓    |
| `SessionEnded`            | ignored                             | ✓    |
| `SilenceDetected`         | stop recording, speech to text      |      |
| `StartSession`            | generate sessionId, start recording |      |
| `StopAudioPlaying`        | stop Audio                          |      |
| `StartListening`          | generate sessionId, start recording |      |
| `StopListening`           | stop recording, speech to text      |      |
| `IntentRecognitionResult` | handle Intent                       |      |
| `IntentRecognitionError`  | EndSession                          | ✓    |
| `PlayAudio`               | play Audio                          |      |
| `PlayFinished`            | Idle                                | ✓    |

## State Transitions

| State                  | Event                     | Result State          |
|------------------------|---------------------------|-----------------------|
| `Idle`                 | `WakeWordDetected`        | `Recording Intent`    |
| `Idle`                 | `StartSession`            | `Recording Intent`    |
| `Idle`                 | `PlayAudio`               | `Playing Audio`       |
| `Recording Intent`     | `AsrError`                | `Idle`                |
| `Recording Intent`     | `AsrTextCaptured`         | `Recognizing Intent`  |
| `Recording Intent`     | `EndSession`              | `Idle`                |
| `Recording Intent`     | `SilenceDetected`         | `Transcribing Intent` |
| `Recording Intent`     | `StopListening`           | `Transcribing Intent` |
| `Transcribing Intent ` | `AsrError`                | `Idle`                |
| `Transcribing Intent`  | `AsrTextCaptured`         | `Recognizing Intent`  |
| `Transcribing Intent`  | `EndSession`              | `Idle`                |
| `Recognizing Intent`   | `IntentRecognitionResult` | `Idle`                |
| `Recognizing Intent`   | `IntentRecognitionError`  | `Idle`                |
| `Recognizing Intent`   | `EndSession`              | `Idle`                |
| `Playing Audio`        | `PlayFinished`            | `Idle`                |
| `Playing Audio`        | `StopAudioPlaying`        | `Idle`                |

## Local Dialog Management Mqtt Services

| Event                     | Service                          |
|---------------------------|----------------------------------|
| `WakeWordDetected`        | WakeWordService                  |
| `AsrTextCaptured`         | SpeechToText                     |
| `AsrError`                | SpeechToText                     | 
| `IntentRecognitionResult` | TextToIntent                     |  
| `IntentRecognitionError`  | TextToIntent                     |
| `PlayAudio`               | (always allowed in `Idle` State) |
| `PlayFinished`            | (always allowed in `Idle` State) |

## Disabled Dialog Management Events

| Event                     | Information                   |
|---------------------------|-------------------------------|
| `StartListening`          | starts SpeechToText Service   |
| `StopListening`           | stops SpeechToText Service    |
| `SilenceDetected`         | stops SpeechToText Service    |
| `AsrTextCaptured`         | starts TextToIntent Service   |
| `IntentRecognitionResult` | starts IntendHandling Service |
| `PlayAudio`               | plays audio                   |
| `StopAudioPlaying`        | stops playing audio           | 
