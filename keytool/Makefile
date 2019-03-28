
HOSTNAME=java4spektrum.com
HST_KEYSTORE=srv_keystore.jks
HST_TRUSTSTORE=srv_truststore.jks
HST_STOREPASS=changeit

CLIENTNAME=java_spektrum
CLN_KEYSTORE=cln_keystore.jks
CLN_TRUSTSTORE=cln_truststore.jks
CLN_STOREPASS=changeit

CA_ALIAS=java4spektrum_ca
CA_KEYSTORE=ca_keystore.jks
CA_STOREPASS=changeit

STORETYPE=pkcs12

# CN = Common Name
# OU = Organization Unit
# O  = Organization Name
# L  = Locality Name
# ST = State Name
# C  = Country (2-letter Country Code)
# E  = Email
DNAME_CA='CN=Java4Spektrum CA,OU=Java4SpektrumLab,O=Java4Spektrum,L=London,ST=London,C=GB'
# For server certificates, the Common Name (CN) must be the hostname
DNAME_HOST='CN=$(HOSTNAME),OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB'
DNAME_CLIENT='CN=$(CLIENTNAME),OU=java4spektrum.com,O=Java4Spektrum,L=London,ST=London,C=GB'

all:	clean create-ca create-truststore add-host add-client

create-ca:
	# Generate a certificate authority (CA), store in ca_keystore
	keytool -genkey -alias $(CA_ALIAS) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname $(DNAME_CA) \
	    -keystore $(CA_KEYSTORE) -storepass $(CA_STOREPASS) -storetype $(STORETYPE)

export-authority:
	# Export certificate authority into .crt format (ready to import)
	keytool -export -alias $(CA_ALIAS) -file $(CA_ALIAS).crt -rfc \
	    -keystore $(CA_KEYSTORE) -storepass $(CA_STOREPASS)


create-truststore: export-authority
	# Import certificate authority into a HOST truststore (create HOST truststore)
	keytool -import -trustcacerts -noprompt -alias $(CA_ALIAS) -file $(CA_ALIAS).crt \
	    -keystore $(HST_TRUSTSTORE) -storepass $(HST_STOREPASS) -storetype $(STORETYPE)

	# Import certificate authority into a CLIENT truststore (create CLIENT truststore)
	keytool -import -trustcacerts -noprompt -alias $(CA_ALIAS) -file $(CA_ALIAS).crt \
	    -keystore $(CLN_TRUSTSTORE) -storepass $(CLN_STOREPASS) -storetype $(STORETYPE)

add-host:
	# Generate a HOST private key (create HOST keystore)
	keytool -genkey -alias $(HOSTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname $(DNAME_HOST) \
	    -keystore $(HST_KEYSTORE) -storepass $(HST_STOREPASS) -storetype $(STORETYPE)
	
	# Generate a host certificate signing request
	keytool -certreq -alias $(HOSTNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -ext san=dns:$(HOSTNAME) \
	    -validity 3650 -file "$(HOSTNAME).csr" \
	    -keystore $(HST_KEYSTORE) -storepass $(HST_STOREPASS)
	
	# Generate signed certificate with the certificate authority
	keytool -gencert -alias $(CA_ALIAS) \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(HOSTNAME).csr" -outfile "$(HOSTNAME).crt" -rfc \
	    -ext san=dns:$(HOSTNAME) \
	    -keystore $(CA_KEYSTORE) -storepass $(CA_STOREPASS)

	# Import signed certificate into the CLIENT truststore
	keytool -import -trustcacerts -alias $(HOSTNAME) \
	    -file "$(HOSTNAME).crt" \
	    -keystore $(CLN_TRUSTSTORE) -storepass $(CLN_STOREPASS)

add-client:
	# Generate CLIENT private key (create CLIENT keystore)
	keytool -genkey -alias $(CLIENTNAME) \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -dname $(DNAME_CLIENT) \
	    -keystore $(CLN_KEYSTORE) -storepass $(CLN_STOREPASS) -storetype $(STORETYPE)
	
	# Generate a CLIENT certificate signing request
	keytool -certreq -alias $(CLIENTNAME) -ext BC=ca:true \
	    -keyalg RSA -keysize 4096 -sigalg SHA512withRSA \
	    -validity 3650 -file "$(CLIENTNAME).csr" \
	    -keystore $(CLN_KEYSTORE) -storepass $(CLN_STOREPASS)
	
	# Generate CLIENT signed certificate with the certificate authority
	keytool -gencert -alias $(CA_ALIAS) \
	    -validity 3650 -sigalg SHA512withRSA \
	    -infile "$(CLIENTNAME).csr" -outfile "$(CLIENTNAME).crt" -rfc \
	    -keystore $(CA_KEYSTORE) -storepass $(CA_STOREPASS)
	
	
	# Import signed CLIENT certificate into the HOST truststore
	keytool -import -trustcacerts -alias $(CLIENTNAME) \
	    -file "$(CLIENTNAME).crt" \
	    -keystore $(HST_TRUSTSTORE) -storepass $(HST_STOREPASS)
	

clean:
	# Remove generated artifacts
	find . \( -name "$(CLIENTNAME)*" -o -name "$(HOSTNAME)*" \
	-o -name "$(CA_KEYSTORE)" -o -name "$(CA_ALIAS).crt" \
	-o -name "$(HST_KEYSTORE)" -o -name "$(HST_TRUSTSTORE)" \
	-o -name "$(CLN_KEYSTORE)" -o -name "$(CLN_TRUSTSTORE)" \
	\) -type f -exec rm -f {} \;