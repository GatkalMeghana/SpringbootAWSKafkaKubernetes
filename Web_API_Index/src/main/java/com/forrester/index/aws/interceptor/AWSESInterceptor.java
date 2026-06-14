package com.forrester.index.aws.interceptor;

import org.apache.http.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.protocol.HttpCoreContext.HTTP_TARGET_HOST;

@Component
public class AWSESInterceptor implements HttpRequestInterceptor {

    private static final String AWS_SERVICE = "es";

    private Aws4Signer signer = Aws4Signer.create();

    private Aws4SignerParams aws4SignerParams;

    public AWSESInterceptor(AwsCredentials awsCredentials) {
        this.aws4SignerParams = Aws4SignerParams.builder()
                .awsCredentials(awsCredentials)
                .signingName(AWS_SERVICE)
                .signingRegion(Region.US_EAST_1)
                .build();
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws IOException {
        SdkHttpFullRequest.Builder builder = SdkHttpFullRequest.builder();

        try {
            URIBuilder uriBuilder = new URIBuilder(request.getRequestLine().getUri());
            builder.encodedPath(uriBuilder.build().getRawPath());
            builder.rawQueryParameters(parseNVPToMultiValueMap(uriBuilder.getQueryParams()));
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI", e);
        }
        builder.protocol(request.getRequestLine().getProtocolVersion().getProtocol());
        HttpHost host = (HttpHost) context.getAttribute(HTTP_TARGET_HOST);
        if (host != null) {
            builder.host(host.getHostName());
        }
        builder.method(SdkHttpMethod.fromValue(request.getRequestLine().getMethod()));
        builder.headers(parseArrayToMultiValueMap(request.getAllHeaders()));

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest) request;
            if (httpEntityEnclosingRequest.getEntity() != null) {
                builder.contentStreamProvider(() -> {
                    try {
                        return httpEntityEnclosingRequest.getEntity().getContent();
                    } catch (IOException e) {
                        return null;
                    }
                });
            }
        }

        SdkHttpFullRequest fullRequest = signer.sign(builder.build(), aws4SignerParams);

        request.setHeaders(parseMultiValueMapToArray(fullRequest.headers()));
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest) request;
            if (httpEntityEnclosingRequest.getEntity() != null) {
                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                Optional<ContentStreamProvider> contentStreamProvider = fullRequest.contentStreamProvider();
                if (contentStreamProvider.isPresent()) {
                    basicHttpEntity.setContent(contentStreamProvider.get().newStream());
                }
                httpEntityEnclosingRequest.setEntity(basicHttpEntity);
            }
        }
    }

    private Map<String, List<String>> parseNVPToMultiValueMap(final List<NameValuePair> nameValuePairs) {
        return nameValuePairs.stream().collect(Collectors
                .groupingBy(NameValuePair::getName, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER), Collectors.mapping(NameValuePair::getValue, Collectors.toList())));
    }

    private Map<String, List<String>> parseArrayToMultiValueMap(final Header[] headers) {
        return Arrays.asList(headers).stream().filter(header -> !skipHeader(header))
                .collect(Collectors.groupingBy(Header::getName, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER), Collectors.mapping(Header::getValue, Collectors.toList())));
    }

    private boolean skipHeader(final Header header) {
        return ("content-length".equalsIgnoreCase(header.getName())
                && "0".equals(header.getValue())) // Strip Content-Length: 0
                || "host".equalsIgnoreCase(header.getName()); // Host comes from endpoint
    }

    private Header[] parseMultiValueMapToArray(final Map<String, List<String>> headers) {
        return headers.entrySet().stream()
                .map(entry -> new BasicHeader(entry.getKey(), entry.getValue().stream().findFirst().get()))
                .collect(Collectors.toList()).toArray(new Header[headers.size()]);
    }
}
