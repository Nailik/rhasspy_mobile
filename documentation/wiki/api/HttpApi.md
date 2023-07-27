# Http Api

The HTTP API consists of a WebServer receiving calls and a Client making calls to various
endpoints. <br/> More information can be
found on
the [rhasspy http api documentation](https://rhasspy.readthedocs.io/en/latest/reference/#http-api).

## WebServer Route

Calls to the WebServer result in an Event that is consumed by the [[Dialog Management]]
or Settings are changed (Settings -> Device). </br> It's also possible to send any request as
defined in [[MQTT Api]]

| Path                      | Type | Data                             | Event              | Settings                                                 |
|---------------------------|------|----------------------------------|--------------------|----------------------------------------------------------|
| `/api/listen-for-command` | POST |                                  | `WakeWordDetected` |                                                          |
| `/api/listen-for-wake`    | POST | String ("on"/"off")              |                    | enable/disable Wake Word Service                         |
| `/api/play-recording`     | POST |                                  |                    | Play or Stop Playing latest recording                    |
| `/api/play-recording`     | GET  |                                  |                    | return latest recording WAV Audio with Header: ByteArray |
| `/api/play-wav`           | POST | WAV Audio with Header: ByteArray | `PlayAudio`        |                                                          |
| `/api/set-volume`         | POST |                                  |                    | set App Audio Volume                                     |
| `/api/start-recording`    | POST |                                  | `StartListening`   |                                                          |
| `/api/stop-recording`     | POST |                                  | `StopListening`    |                                                          |
| `/api/say`                | POST | text: String                     |                    | trigger `TextToSpeech Service`                           |
| `/api/mqtt/<topic>`       | POST | payload: ByteArray               | see [[MQTT Api]]   | see [[MQTT Api]]                                         |

## Client Calls

The urls for the outgoing calls can set to custom endpoints as well and will be used by different
services.
<br/>
Default Endpoint is defined in [[Remote Hermes HTTP]]

| Path                                  | Type   | Data                             | Service                     |
|---------------------------------------|--------|----------------------------------|-----------------------------|
| `/api/play-wav`                       | POST   | WAV Audio with Header: ByteArray | `AudioPlaying Service`      | 
| `/api/text-to-intent`                 | POST   | text: String                     | `IntentRecognition Service` |
| `/api/speech-to-text`                 | STREAM | WAV Audio with Header: ByteArray | `SpeechToText Service`      | 
| `/api/text-to-speech`                 | POST   | text: String                     | `TextToSpeech Service`      | 
| `<custom>`                            | POST   | intent: Json                     | `IntentHandling Service`    |
| `<homeassistant>/api/events/rhasspy_` | POST   | intent: Json                     | `IntentHandling Service`    | 
| `<homeassistant>/api/intent/handle`   | POST   | intent: Json                     | `IntentHandling Service`    | 