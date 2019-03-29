
This Java project provides the code examples listed in the article "10 Design-Prinzipien zur Erstellung sicherer Software", published in the online journal JavaSpektrum in March 2019.


### 1. REST Client usingg X.509 client certificate to authenticate to a SpringBoot Server

The first example implements a basic Java Spring Boot application that leverages the Spring Security framework to implement mutual TLS authentication using self-generated digital certificates.

In order for the application to work there are few initial steps, explained below, that must be completed before using the code.  These require use of the `keytool` from a recent Java SDK.  These steps require you to be able to run a `make` command in a Unix-like environment.  The Makefile has been tested in two environments: Fedora 29 on a VirtualBox VM and MacOS Mojave.


#### 1. Generate the keys
- before running the make command, inspect the commands listed below to make sure that you are happy with the key segregation and passwords used.
```
cd keystore
make all
```

The output would similar to the listing below:
```
# Generate a certificate authority (CA), store in ca_keystore
keytool -genkey -alias java4spektrum_ca -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname 'CN=Java4Spektrum CA,OU=Java4SpektrumLab,O=Java4Spektrum,L=London,ST=London,C=GB' \
	    -keystore ca_keystore.jks -storepass changeit -storetype pkcs12
# Export certificate authority into .crt format (ready to import)
keytool -export -alias java4spektrum_ca -file java4spektrum_ca.crt -rfc \
	    -keystore ca_keystore.jks -storepass changeit
Certificate stored in file <java4spektrum_ca.crt>
# Import certificate authority into a HOST truststore (create HOST truststore)
keytool -import -trustcacerts -noprompt -alias java4spektrum_ca -file java4spektrum_ca.crt \
	    -keystore srv_truststore.jks -storepass changeit -storetype pkcs12
Certificate was added to keystore
# Import certificate authority into a CLIENT truststore (create CLIENT truststore)
keytool -import -trustcacerts -noprompt -alias java4spektrum_ca -file java4spektrum_ca.crt \
	    -keystore cln_truststore.jks -storepass changeit -storetype pkcs12
Certificate was added to keystore
# Generate a HOST private key (create HOST keystore)
keytool -genkey -alias java4spektrum.com \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname 'CN=java4spektrum.com,OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB' \
	    -keystore srv_keystore.jks -storepass changeit -storetype pkcs12
# Generate a host certificate signing request
keytool -certreq -alias java4spektrum.com -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -ext san=dns:java4spektrum.com \
	    -validity 3650 -file "java4spektrum.com.csr" \
	    -keystore srv_keystore.jks -storepass changeit
# Generate signed certificate with the certificate authority
keytool -gencert -alias java4spektrum_ca \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "java4spektrum.com.csr" -outfile "java4spektrum.com.crt" -rfc \
	    -ext san=dns:java4spektrum.com \
	    -keystore ca_keystore.jks -storepass changeit
# Import signed certificate into the CLIENT truststore
keytool -import -trustcacerts -alias java4spektrum.com \
	    -file "java4spektrum.com.crt" \
	    -keystore cln_truststore.jks -storepass changeit
Certificate was added to keystore
# Generate CLIENT private key (create CLIENT keystore)
keytool -genkey -alias java_spektrum \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname 'CN=java_spektrum,OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB' \
	    -keystore cln_keystore.jks -storepass changeit -storetype pkcs12
# Generate a CLIENT certificate signing request
keytool -certreq -alias java_spektrum -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "java_spektrum.csr" \
	    -keystore cln_keystore.jks -storepass changeit
# Generate CLIENT signed certificate with the certificate authority
keytool -gencert -alias java4spektrum_ca \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "java_spektrum.csr" -outfile "java_spektrum.crt" -rfc \
	    -keystore ca_keystore.jks -storepass changeit
# Import signed CLIENT certificate into the HOST truststore
keytool -import -trustcacerts -alias java_spektrum \
	    -file "java_spektrum.crt" \
	    -keystore srv_truststore.jks -storepass changeit
Certificate was added to keystore
```


#### 2. Copy Server's and Client's keystores and truststore to the classpath
- copy `srv_keystore.jks` and `srv_truststore.jks` into the `src/main/resources` folder in your project
- copy `cln_truststore.jks` and `cln_keystore.jks` into the `src/test/resources` folder in your project

#### 3. Alter your local DNS records
All of the certificates have been created for a fake domain name `java4spektrum.com` so in order for the tests to succeed, the Spring Boot server must be available at that DNS name. 

Fon Unix-like systems, the simplest approach is to add a new record in your local DNS file `/etc/hosts` to resolve that name to the local loopback interface.  The line needed is: `127.0.0.1 java4spektrum.com`

For Windows the same approach can be used, but the host file is usually `C:\Windows\System32\Drivers\etc\hosts`. 

Both systems require privileged access to make the changes. 

#### 4. Configure your IDE to register and run the JUnit tests. 
Follow your normal setup steps to allow you to run the JUnit tests from the project (found in `src/test/java`).

### 5. Configure the SpringBoot REST API controller to log using Log4j2

This code uses the Log4j2 library to write its log messages.  The configuration for Log4J2 is in the `log4j2.xml` file located in the `src/main/resources` directory. You can change logging parameters such as the file location by editing that file.

This project is for demonstration only and is aimed to support the code listed in the original article.  It is not intended to form the basis of a production application.  Refer to your organisation's coding  standards, key management and logging standards to allow the code to be adapted for use in your environment.

We hope you find this useful.

Endava Team
