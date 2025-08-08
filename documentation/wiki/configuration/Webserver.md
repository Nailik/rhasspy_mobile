This service handles incoming messages as defined in [[Http Api]].
<br/>The following settings are available:

| Setting           | Information | 
|-------------------|-------------|
| Enable            |             | 
| Port              |             | 
| SSL               | see below   | 
| KeyStore File     |             | 
| KeyStore Password |             | 
| Key Alias         |             | 
| Key Password      |             |

## Enable SSL

Rhasspy Mobile uses KTOR for the internal webserver, therefore an BKS certificate needs to be
created.

### Android

The following files are from https://github.com/myfreax/android-ssl-certificate/tree/main .

1. Install `openssl`,`keytool`,`JDK 1.8` in your computer.
2.
Download [bcprov.jar](https://github.com/Nailik/rhasspy_mobile/tree/master/documentation/images/tools/ssl/bcprov.jar)
3.
Use [mkcert.sh](https://github.com/Nailik/rhasspy_mobile/tree/master/documentation/images/tools/ssl/mkcert.sh)
to generate keystore
4. Load the generated `keystore.bks` file it into the app and set the chosen KeyStore Password, Key
   Alias and Key Password (the script uses the same Password for both)