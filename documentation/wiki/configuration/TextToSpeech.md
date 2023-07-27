# Text To Speech Service

This service is is used to translate text to speech when [[Http Api]] `/api/say`
or [[Mqtt Api]] `hermes/tts/say` is called.
<br/>The following options are available:

| Option             | Information                                        | 
|--------------------|----------------------------------------------------|
| Remote HTTP        | [[Http Api]] `/api/speech-to-text` <br/>custom url | 
| Remote Hermes MQTT | [[Mqtt Api]] `hermes/tts/say`                      | 
| Disabled           | Ignores the Event                                  | 