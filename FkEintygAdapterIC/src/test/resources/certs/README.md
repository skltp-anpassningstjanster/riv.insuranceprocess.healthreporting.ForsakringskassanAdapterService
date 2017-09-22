To extend expiry date:

keytool -list -v -keystore consumer.jks
keytool -selfcert -validity 2000 -alias consumer -keystore consumer.jks
