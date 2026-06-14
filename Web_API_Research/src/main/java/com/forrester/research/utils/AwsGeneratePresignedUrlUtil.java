package com.forrester.research.utils;

import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.exception.ServiceException;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
public class AwsGeneratePresignedUrlUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AwsGeneratePresignedUrlUtil.class);

	@Autowired
	private S3Presigner s3Presigner;

	@Value("${forr.aws.bucket.name}")
	private String bucketName;

	@Value("${forr.aws.s3.presigned.url.ttl}")
	private long ttl;

	@Autowired
	private S3Client s3Client;

	@LogThis
	public String uploadAndGetPresignedUrl(ResponseEntity<ByteArrayResource> resource, String entryId)
			throws ServiceException {
		try {
			String fileName = entryId + Instant.now().toEpochMilli() + RandomUtils.nextLong();

			boolean isFileUpload = fileUpload(fileName, resource);
			LOGGER.info("File uploaded successfully in s3 bucket : {}", isFileUpload);

			if (isFileUpload) {
				String resourceFileName = Objects.requireNonNull(resource.getBody()).getFilename();

				String downloadFileName = StringUtils.isNotBlank(resourceFileName) ? resourceFileName : entryId;
				String encodedFileName = URLEncoder.encode(downloadFileName, "UTF-8").replace("+", "-");
				GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName)
						.responseContentDisposition("attachment; filename="+encodedFileName)
						.responseContentType(MediaType.APPLICATION_PDF_VALUE)
						.key(fileName).build();

				GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
						.signatureDuration(Duration.ofMinutes(ttl)).getObjectRequest(getObjectRequest).build();

				PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner
						.presignGetObject(getObjectPresignRequest);
				
				LOGGER.info("PreSigned URL Generated Succesfully..");
				s3Presigner.close();
				return presignedGetObjectRequest.url().toString();
			}
		} catch (Exception e) {
			throw new ServiceException("Unable to generate presigned URL for entryId " + entryId, e);
		}
		throw new ServiceException("Unable to generate presigned URL for entryId.");
	}

	@LogThis
	public boolean fileUpload(String fileName, ResponseEntity<ByteArrayResource> resource) {

		PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName)
				.contentType(MediaType.APPLICATION_PDF_VALUE).build();

		PutObjectResponse putObjectResult = s3Client.putObject(putObjectRequest,
				RequestBody.fromBytes(Objects.requireNonNull(resource.getBody()).getByteArray()));

		return Objects.nonNull(putObjectResult);
	}
}