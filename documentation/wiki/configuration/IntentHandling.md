This service is handles intents for the Event `IntentRecognitionResult`.
<br/>The following options are available:

| Option           | Information                                                                                                                | 
|------------------|----------------------------------------------------------------------------------------------------------------------------|
| Home Assistant   | [[Http Api]] Sends intent to <br/>`<homeassistant>/api/events/rhasspy_` <br/>or `<homeassistant>/api/intent/handle`        | 
| Remote HTTP      | [[Http Api]] `<custom>` url                                                                                                | 
| With Recognition | Rhasspy handles intent when using [[Http Api]] `/api/text-to-intent` <br/>when this is not selected `?nohass=true` is used | 
| Disabled         | Ignores the Event                                                                                                          | 
