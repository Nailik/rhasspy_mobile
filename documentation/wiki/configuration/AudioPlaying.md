This service is playing audio for the Event `PlayAudio`.
<br/>The following options are available:

| Option             | Information                                                                                                                | 
|--------------------|----------------------------------------------------------------------------------------------------------------------------|
| Local              | Plays audio on the mobile phone <br/>`Sound` like music <br/>`Notification` like messages                                  | 
| Remote HTTP        | [[Http Api]] `/api/play-wav` <br/>custom url                                                                               | 
| Remote Hermes MQTT | [[Mqtt Api]] `hermes/audioServer/<siteId>/playBytes/<requestId>`<br/> `siteId` as selected <br/>`requestId` uuid generated | 
| Disabled           | Ignores the Event                                                                                                          | 