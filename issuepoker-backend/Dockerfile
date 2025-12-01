# For documentation see https://jboss-container-images.github.io/openjdk/
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:1.23-6.1764155306@sha256:119469d5b7f9ac41d46b5d62ed9e378a192745c1e3a5fc5e5fa34958a68b8245

# Copy runnable jar to deployments
COPY target/*.jar /deployments/application.jar