package com.forrester.research;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@RefreshScope
@Configuration
public class ApplicationConfig {

	@Value("${forr.aws.accesskey.id}")
	private String awsAccessKeyID;

	@Value("${forr.aws.secret.accesskey}")
	private String awsSecretAccessKey;

	@Bean
	public AwsCredentials awsCredentials() {
		return AwsBasicCredentials.create(awsAccessKeyID, awsSecretAccessKey);
	}

	@Bean
	public S3Presigner s3Presigner(AwsCredentials awsCredentials) {
		return S3Presigner.builder().credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
				.region(Region.US_EAST_1).build();
	}

	@Bean
	public S3Client s3Client(AwsCredentials awsCredentials) {
		return S3Client.builder().credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
				.region(Region.US_EAST_1).build();
	}
}
