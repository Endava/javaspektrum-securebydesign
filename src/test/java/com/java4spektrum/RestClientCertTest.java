package com.java4spektrum;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = ServerApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RestClientCertTest {

	@LocalServerPort
	private int port;

	@Value("${http.client.ssl.key-alias}")
	private String CLIENT_CERT;

    @Value("${http.client.ssl.key-store}")
    private String keyStorePath;

    @Value("${http.client.ssl.trust-store}")
    private String trustStorePath;

    @Value("${http.client.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${http.client.ssl.trust-store-password}")
    private String  trustStorePassword;

    @Value("${http.client.ssl.client-key-password}")
    private String  clientKeyPassword;


	@Test
	public void givenValidCertificates_whenUsingHttpClient_thenCorrect()
			throws Exception {

		String HOST_WITH_SSL = "https://java4spektrum.com:" + port + "/api/hello";

		PrivateKeyStrategy aliasStrategy = new PrivateKeyStrategy() {
			@Override
			public String chooseAlias(Map<String, PrivateKeyDetails> map, Socket socket) {
				return CLIENT_CERT;
			}
		};

		SSLContext sslContext = SSLContextBuilder
				.create()
				.loadKeyMaterial(ResourceUtils.getFile(keyStorePath),
                        keyStorePassword.toCharArray(),
                        clientKeyPassword.toCharArray(),
                        aliasStrategy)
				.loadTrustMaterial(ResourceUtils.getFile(trustStorePath), trustStorePassword.toCharArray())
				.build();

		CloseableHttpClient client = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(new DefaultHostnameVerifier()) // HostnameVerifier
				.build();

		HttpGet httpGet = new HttpGet(HOST_WITH_SSL);

		HttpResponse response = client.execute(httpGet);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), 200);
	}



	@Test
	public void givenValidCertificate_whenUsingRestTemplate_thenCorrect()
			throws ClientProtocolException, IOException, Exception {

		String HOST_WITH_SSL = "https://java4spektrum.com:" + port + "/api/hello";

		PrivateKeyStrategy aliasStrategy = new PrivateKeyStrategy() {
			@Override
			public String chooseAlias(Map<String, PrivateKeyDetails> map, Socket socket) {
				return CLIENT_CERT;
			}
		};

		SSLContext sslContext = SSLContextBuilder
				.create()
				.loadKeyMaterial(ResourceUtils.getFile(keyStorePath),
                        keyStorePassword.toCharArray(),
                        clientKeyPassword.toCharArray(),
                        aliasStrategy)
				.loadTrustMaterial(ResourceUtils.getFile(trustStorePath), trustStorePassword.toCharArray())
				.build();

		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(new DefaultHostnameVerifier())
				.build();

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

		ResponseEntity<String> response = new RestTemplate(requestFactory).exchange(
				HOST_WITH_SSL, HttpMethod.GET, null, String.class);
		Assert.assertEquals(response.getStatusCode().value(), 200);

	}


}
