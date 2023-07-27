# Intent Recognition Service

This service is recognizing intents from text for the Event `AsrTextCaptured`.
<br/>The following options are available:

| Option             | Information                                        | 
|--------------------|----------------------------------------------------|
| Remote HTTP        | [[Http Api]] `/api/text-to-intent` <br/>custom url | 
| Remote Hermes MQTT | [[Mqtt Api]] `hermes/asr/textCaptured`             | 
| Disabled           | Ignores the Event                                  | 