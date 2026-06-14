#Setting up multistage build
ARG IMAGE_REPO="docker.io/library"
# STAGE to validate and make sure all test cases pass.
FROM $IMAGE_REPO/maven:3.8.4-openjdk-8-slim as VALIDATE_STAGE
LABEL MAINTAINER="Forrester TM(websupport@forrester.com)"

ARG SERVICE_NAME="index-service"

ENV WORKING_DIR /opt/$SERVICE_NAME

WORKDIR $WORKING_DIR

COPY . .

RUN mvn verify

# STAGE to package code into jar file.
FROM $IMAGE_REPO/maven:3.8.4-openjdk-8-slim as BUILD_STAGE
LABEL MAINTAINER="Forrester TM(websupport@forrester.com)"

ARG SERVICE_NAME="index-service"

ENV WORKING_DIR /opt/$SERVICE_NAME

WORKDIR $WORKING_DIR

COPY --from=VALIDATE_STAGE $WORKING_DIR .

RUN mvn -DskipTests=true package

# STAGE to repackage jar file into image.
FROM $IMAGE_REPO/openjdk:8u322-jre-slim as PACKAGE_STAGE
LABEL MAINTAINER="Forrester TM(websupport@forrester.com)"

#Setting build args passed from maven build.
ARG SERVICE_NAME="index-service"
ARG SERVICE_PORT=9046
ARG JKS_FILE_LOCATION=/data/Forrester/IndexService/bin/indextrust.jks

#Building env variable for working directory and volume
ENV WORKING_DIR /opt/$SERVICE_NAME
ENV app.home $WORKING_DIR

WORKDIR $WORKING_DIR

COPY --from=BUILD_STAGE $WORKING_DIR/target/$SERVICE_NAME.jar service.jar
COPY --from=BUILD_STAGE $WORKING_DIR/target/classes/indextrust.jks $JKS_FILE_LOCATION

EXPOSE $SERVICE_PORT

ENTRYPOINT ["/usr/local/openjdk-8/bin/java"]
CMD ["-jar", "service.jar"]