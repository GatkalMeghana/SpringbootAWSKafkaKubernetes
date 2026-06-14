package com.forrester.index;

import javax.jms.ConnectionFactory;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.forrester.index.aws.interceptor.AWSESInterceptor;
import com.forrester.index.utils.IndexAliasesBuilder;
import com.forrester.index.utils.IndexNameProvider;

import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@EnableJms
@RefreshScope
@Configuration
@EnableRetry
public class ApplicationConfig {

    @Value("${forr.elasticsearch.domain.url}")
    private String esDomainURL;

    @Value("${forr.aws.accesskey.id}")
    private String awsAccessKeyID;

    @Value("${forr.aws.secret.accesskey}")
    private String awsSecretAccessKey;

    @Bean
    public RestHighLevelClient restHighLevelClient(AWSESInterceptor awsesInterceptor) {

        RestClientBuilder builder = RestClient.builder(
                HttpHost.create(esDomainURL))
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.addInterceptorLast(awsesInterceptor));

        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(RestHighLevelClient restHighLevelClient) {
        return new ElasticsearchRestTemplate(restHighLevelClient);
    }

    @Bean
    public AwsCredentials awsCredentials() {
        return AwsBasicCredentials.create(awsAccessKeyID, awsSecretAccessKey);
    }

    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> listenerFactory(ConnectionFactory connectionFactory) {

        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        factory.setConcurrency("1-1");
        return factory;
    }
    
    @Bean
    @RequestScope
    public IndexNameProvider indexNameProvider() {
    	return new IndexNameProvider();
    }
    
    @Bean
    @Scope("prototype")
    public IndexAliasesBuilder indexAliasesBuilder() {
    	return new IndexAliasesBuilder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder){
        return builder.build();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
