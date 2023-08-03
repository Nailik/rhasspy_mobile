#!/bin/bash
rm -rf fullchain.pem privkey.pem keystore.bks keystore.p12
openssl genrsa > privkey.pem
openssl req -new -x509 -key privkey.pem > fullchain.pem

PRIVKEY_FILE="privkey.pem"
FULLCHAIN_FILE="fullchain.pem"

if [ ! -f "$PRIVKEY_FILE" ]; then
    echo "ERROR: $PRIVKEY_FILE doesn't exists in a dir."
    exit 1
fi

if [ ! -f "$FULLCHAIN_FILE" ]; then
  echo "ERROR: $FULLCHAIN_FILE doesn't exists in a dir."
  exit 1
fi
read -p "Please Enter Keystore Alias Name? " ALIAS
read -p "Please Enter Keystore Password? " PASSWD

# Generate a PKCS12 certificate bundle from the cert and private key
openssl pkcs12 -export -out keystore.p12 -inkey $PRIVKEY_FILE -in $FULLCHAIN_FILE -name $ALIAS \
  -passin pass:$PASSWD -passout pass:$PASSWD

# Convert to BKS
keytool -importkeystore -alias $ALIAS -srckeystore keystore.p12 -srcstoretype PKCS12 \
  -srcstorepass $PASSWD -storepass $PASSWD \
  -deststoretype BKS -providerpath bcprov.jar \
  -provider org.bouncycastle.jce.provider.BouncyCastleProvider -destkeystore keystore.bks
rm -rf keystore.p12