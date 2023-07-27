# Speech To Text Service

This service is recognizing intents from text for the Event `SilenceDetected` or `StopListening`.
<br/>The following options are available:

| Option             | Information                                        | 
|--------------------|----------------------------------------------------|
| Remote HTTP        | [[Http Api]] `/api/speech-to-text` <br/>custom url | 
| Remote Hermes MQTT | [[Mqtt Api]] `hermes/asr/textCaptured`             | 
| Disabled           | Ignores the Event                                  | 