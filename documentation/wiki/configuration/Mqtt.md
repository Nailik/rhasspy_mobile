This service handles incoming and outgoing messages as defined in [[Mqtt Api]].
<br/>The following settings are available:

| Setting            | Information                    | 
|--------------------|--------------------------------|
| Enable             |                                | 
| Host               | Host to connect to (IP or DNS) | 
| Port               |                                | 
| Username           |                                | 
| Password           |                                | 
| SSL                | see below                      | 
| Certificate        |                                | 
| Store Password     |                                |
| Connection timeout |                                | 
| KeepAlive interval |                                | 
| Retry interval     |                                | 

## Enable SSL

---

#### **Introduction**
This guide provides instructions for securing MQTT communication using TLS/SSL. Two methods are available:
1. **Direct Connection with Certificates and a `.bks` File**
2. **Automatic Connection with a Reverse Proxy**

---

### **Method 1: Direct Connection with Certificates and a `.bks` File**

#### **Introduction**
This section explains how to secure the MQTT protocol using its encrypted variant, **MQTTS**. The goal is to establish encrypted communication between an MQTT broker and clients.

#### **Generating Keys and Certificates with OpenSSL**

##### **Certificate Authority (CA)**
1. Create a working directory and generate the key and certificate for the CA.
   ```bash
   mkdir ~/certs && cd ~/certs
   mkdir ca && cd ca
   openssl req -new -x509 -days 365 -extensions v3_ca -keyout ca.key -out ca.crt
   ```
   > **Note:** The Common Name (CN) must differ from the broker and client certificates.

##### **Broker Certificates**
1. Generate a private key for the broker.
   ```bash
   ~/certs
   mkdir broker && cd broker
   openssl genrsa -out broker.key 2048
   ```

2. Create a Certificate Signing Request (CSR) for the broker.
   ```bash
   openssl req -new -key broker.key -out broker.csr
   ```

3. Sign the broker's CSR with the CA.
   ```bash
   openssl x509 -req -in broker.csr -CA ../ca/ca.crt -CAkey ../ca/ca.key -CAcreateserial -out broker.crt -days 365
   ```

##### **Client Certificates**
1. Generate a private key for the client.
   ```bash
   ~/certs
   mkdir client && cd client
   openssl genrsa -out client.key 2048
   ```

2. Create a CSR for the client.
   ```bash
   openssl req -new -key client.key -out client.csr
   ```

3. Sign the client's CSR with the CA.
   ```bash
   openssl x509 -req -in client.csr -CA ../../ca/ca.crt -CAkey ../../ca/ca.key -CAcreateserial -out client.crt -days 365
   ```

##### **Folder structure**
Now you should have all these files:
   ```bash
   cd ~/certs
   tree .
   .
   ├── broker
   │   ├── broker.crt
   │   └── broker.key
   ├── ca
   │   ├── ca.crt
   │   ├── ca.key
   │   └── ca.srl
   └── client
    ├── client.crt
    └── client.key
   ```
##### **Generating `.bks` File**
1. Download the BouncyCastle library.
   ```bash
   mkdir ~/bouncycastle
   wget https://repo1.maven.org/maven2/org/bouncycastle/bcprov-jdk18on/1.82/bcprov-jdk18on-1.82.jar -O ~/bouncycastle/bcprov-jdk18on-1.82.jar
   ```

2. Convert the `.crt` and `.key` files to `.p12`.
> **note** : for create the file you will need to setup a password
   ```bash
   openssl pkcs12 -export -in ~/certs/client/client.crt -inkey ~/certs/client/client.key -out client.p12 -name "client" -CAfile ~/certs/ca/ca.crt -caname "ca"
   ```

3. Generate the `.bks` file and import the certificate.
> **note** : for create the file you will need to setup a password, this password will be usefull in the app to allow it to decrypt the information from the store
   ```bash
   keytool -importkeystore -srckeystore client.p12 -srcstoretype PKCS12 -destkeystore client.bks -deststoretype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ~/bouncycastle/bcprov-jdk18on-1.82.jar
   keytool -importcert -alias ca -file ~/certs/ca/ca.crt -keystore client.bks -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ~/bouncycastle/bcprov-jdk18on-1.82.jar
   ```

#### **Configuring the MQTT Broker**
1. Configure Mosquitto to use the certificates.
   ```bash
   sudo nano /etc/mosquitto/mosquitto.conf
   ```
   Add the following lines:
   ```ini
   listen 8883
   cafile ~/certs/ca/ca.crt
   certfile ~/certs/broker/broker.crt
   keyfile ~/certs/broker/broker.key
   require_certificate true
   ```

#### **Verifying Certificates**
- To check the contents of a certificate:
  ```bash
  openssl x509 -in ~/certs/broker/broker.crt -text -noout
  openssl x509 -in ~/certs/client/client.crt -text -noout
  openssl x509 -in ~/certs/ca/ca.crt -text -noout
  ```
- To check the contents of the `.bks` file:
  ```bash
  keytool -list -keystore client.bks -storetype BKS -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath ~/bouncycastle/bcprov-jdk18on-1.82.jar
  ```

#### **Configuring Rhasspy Mobile**
- In the MQTT config page, enable SSL, upload the `.bks` file, and enter the password set during file creation in the "KeyStore Password" field.
> **Please note that certificates have an expiration date; you will need to repeat this procedure to renew them.**
---

### **Method 2: Automatic Connection with a Reverse Proxy**

#### **Introduction**
This section explains how to secure an MQTT connection between a client and a reverse proxy positioned before an MQTT server.
the connection between traefik and the mqtt server is made in clear (no certificate or ssl connection in the MQTT server).
but the connection between your mobile and traefik will be encrypted

#### **Example Configuration for Traefik Reverse Proxy**
use the documentation if you are not used to using traefik https://doc.traefik.io/traefik/
##### **Traefik Configuration**
- Example `traefik.yml` file:
  ```yaml
  entryPoints:
    web:
      address: ":http"
    websecure:
      address: ":https"
    mqttsecure:
      address: ":8883"

  certificatesResolvers:
    myresolver:
      acme:
        tlsChallenge: {}
        email: "yourEmail@gmail.com"
        storage: "/letsencrypt/acme.json"
        httpChallenge:
          entryPoint: "web"

  providers:
    file:
      directory: /etc/traefik/dyn/
  ```

##### **Dynamic Configuration**
- Example `dynamic.yml` file:
  ```yaml
  tcp:
    routers:
      mqtt:
        rule: HostSNI(`your.dns.example`)
        entryPoints: mqttsecure
        tls:
          passthrough: true
        service: mqtt@file
    services:
      mqtt:
        loadBalancer:
          servers:
            - port: 1883
  ```

#### **Configuring Rhasspy Mobile**
- In the MQTT config page, just enable SSL.