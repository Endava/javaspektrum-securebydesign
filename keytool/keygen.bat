@echo off

REM:  Generate a certificate authority (CA), store in ca_keystore
keytool -genkey -alias java4spektrum_ca -ext BC=ca:true ^
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA ^
	    -validity 3650 -dname "CN=Java4Spektrum CA,OU=Java4SpektrumLab,O=Java4Spektrum,L=London,ST=London,C=GB" ^
	    -keystore ca_keystore.jks -storepass changeit -storetype pkcs12

REM:  Export certificate authority into .crt format (ready to import)
keytool -export -alias java4spektrum_ca -file java4spektrum_ca.crt -rfc ^
	    -keystore ca_keystore.jks -storepass changeit
echo Certificate stored in file java4spektrum_ca.crt

REM:  Import certificate authority into a HOST truststore (create HOST truststore)
keytool -import -trustcacerts -noprompt -alias java4spektrum_ca -file java4spektrum_ca.crt ^
	    -keystore srv_truststore.jks -storepass changeit -storetype pkcs12
echo CA Certificate was added to HOST truststore

REM:  Import certificate authority into a CLIENT truststore (create CLIENT truststore)
keytool -import -trustcacerts -noprompt -alias java4spektrum_ca -file java4spektrum_ca.crt ^
	    -keystore cln_truststore.jks -storepass changeit -storetype pkcs12
echo Certificate was added to CLIENT truststore

REM:  Generate a HOST private key (create HOST keystore)
keytool -genkey -alias java4spektrum.com ^
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA ^
	    -validity 3650 -dname "CN=java4spektrum.com,OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB" ^
	    -keystore srv_keystore.jks -storepass changeit -storetype pkcs12

REM:  Generate a host certificate signing request
keytool -certreq -alias java4spektrum.com -ext BC=ca:true ^
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA ^
	    -ext san=dns:java4spektrum.com ^
	    -validity 3650 -file "java4spektrum.com.csr" ^
	    -keystore srv_keystore.jks -storepass changeit

REM:  Generate signed certificate with the certificate authority
keytool -gencert -alias java4spektrum_ca ^
	    -validity 3650 -sigalg SHA512withRSA ^
	    -infile "java4spektrum.com.csr" -outfile "java4spektrum.com.crt" -rfc ^
	    -ext san=dns:java4spektrum.com ^
	    -keystore ca_keystore.jks -storepass changeit

REM:  Import signed certificate into the CLIENT truststore
keytool -import -trustcacerts -alias java4spektrum.com ^
	    -file "java4spektrum.com.crt" ^
	    -keystore cln_truststore.jks -storepass changeit
echo Host certificate was added to client truststore

REM:  Generate CLIENT private key (create CLIENT keystore)
keytool -genkey -alias java_spektrum ^
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA ^
	    -validity 3650 -dname "CN=java_spektrum,OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB" ^
	    -keystore cln_keystore.jks -storepass changeit -storetype pkcs12

REM:  Generate a CLIENT certificate signing request
keytool -certreq -alias java_spektrum -ext BC=ca:true ^
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA ^
	    -validity 3650 -file "java_spektrum.csr" ^
	    -keystore cln_keystore.jks -storepass changeit

REM:  Generate CLIENT signed certificate with the certificate authority
keytool -gencert -alias java4spektrum_ca ^
	    -validity 3650 -sigalg SHA512withRSA ^
	    -infile "java_spektrum.csr" -outfile "java_spektrum.crt" -rfc ^
	    -keystore ca_keystore.jks -storepass changeit

REM:  Import signed CLIENT certificate into the HOST truststore
keytool -import -trustcacerts -alias java_spektrum ^
	    -file "java_spektrum.crt" ^
	    -keystore srv_truststore.jks -storepass changeit
echo Client certificate was added to Host truststore
