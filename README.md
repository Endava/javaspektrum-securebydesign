
The current project supports the code examples listed in the article "10 Design-Prinzipien zur Erstellung sicherer Software", published in online journal JavaSpektrum.


### 1. REST Client uses X.509 client certificate to authenticate to SpringBoot Server

The first example implements a basic Java Spring Boot application that leverages the Spring Security framework to implement mutual TLS authentication using self-generated digital certtificates.

In order for the application to work there are few steps that must be completed in the background using the `keytool` from one of the latest Java SDK.
The code requires for you to be able to run `make` command in the unix-like environment.
The Makefile had been tested in two main environments: Fedora 29 on VirtualBox VM, MacOS Mojave.


#### 1. Generate the keys
- before running the make command, make sure you are happy with the keys segregation and passwords.
```
cd keystore
make all
```

The output would look as the one below
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


#### 2. Copy Server's and Client's keystores and truststore in the classpath
- at this step you must copy `srv_keystore.jks`, `srv_truststore.jks` into the `src/main/resources` folder in your project
- you also must copy the `cln_truststore.jks`, `cln_keystore.jks` into the `src/test/resources`

#### 3. Alter your local DNS records
Since all the certificates had been creates for a fake domain name `java4spektrum.com` in order for you tests to succeed, the Spring Boot server must be available at that name. 
Fon unix-like systems, you must add a new record in you local DNS file `/etc/hosts` as folows `127.0.0.1 java4spektrum.com`
For Windows its usually in `C:\Windows\System32\Drivers\etc\hosts`. 
Remember, both the systems require privileged access to make the changes. 

#### 4. Configure you IDE to register an run the jUnit tests. 
Follow the guidance for your IDE or the environment in which you run yur automatic tests. If all done correctly the tests will succeed.


### 2. SpringBoot REST API Controller implements logging using Log4j2

In this example we will use the Log4j2 library to define output and logging strategy.
The configuration is done in the log4j2.xml file located in the `src/main/resources`. You can define your own file location and strategy by editing that file.

Please be aware that the current project is for demonstration only and is aimed to support the code listed in the original article.
Please refer to your internal coding  standards, key management and logging standards adopted by your organisation to adapt the code accordingly.

All the Best !
Endava Team.
