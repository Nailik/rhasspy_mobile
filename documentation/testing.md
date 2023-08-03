Settings:

- app upgrade (settings stay the same)
- new installation (initialization of settings)
- saving settings works (ui, storing files correctly)
- load settings from backup works (ui, loading files correctly)
- sharing settings works (ui, sending data)

Main page:

- permission request (already ui test)
- playing latest recording
    - while idle: works, start/stop
    - while waiting for wake word: stops wake word recording and resumes, start/stop
    - else: not enabled (ui)

Dialog Service:

- starting manually (when all services disabled ends instantly)
- starts wake word on idle when enabled (on session finished)

ViewModels:

- work

Configuration:

- changing site id works (ui)
- Remote hermes http:
    - connects to correct host
    - connects to correct port
    - uses timeout
    - ssl validation http(s) works
    - after saving reconnects to new host
- webserver
    - starting on app start (if enabled)
    - correct port
    - (later) ssl works
    - restarting when settings change
- mqtt
    - starting on app start  (if enabled)
    - connects using correct host, port
    - works without username/password if server allows
    - works with username/password if required
    - (later) ssl works
    - timeout works
    - keep alive works
    - reconnect works
    - restarts when settings change
- wake word
    - starts on app start
    - restarts when settings change
    - porcupine:
        - uses correct key
        - uses correct language (works with different language)
        - uses correct activation words with sensitivity (active from different languages uses only
          correct once)
        - custom wake words work
        - importing custom wake word works (copies file)
    - mqtt:
        - recognizes incoming wake word correctly
    - udp:
        - send packets to udp (with wave header), correct wave header (valid wave)
- speech to text
    - reloads necessary services on settings change
    - http:
        - uses correct url (custom if wanted)
        - reads result
    - mqtt:
        - uses silence detection if enabled
        - reads result
- intent recognition:
    - reloads necessary services on settings change
    - http:
        - uses correct url (custom if wanted)
        - reads result
    - mqtt:
        - uses silence detection if enabled
        - reads result
- text to speech:
    - reloads necessary services on settings change
    - http:
        - uses correct url (custom if wanted)
        - reads result
    - mqtt:
        - uses silence detection if enabled
        - reads result
- audio playing
    - reloads necessary services on settings change
    - Local:
        - uses correctly sound/notification
        - 