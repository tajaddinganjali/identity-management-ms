FROM registry.access.redhat.com/ubi8/openjdk-11:latest
ENV API_SERVER_PORT=8080
ENV MANAGEMENT_SERVER_PORT=8081
EXPOSE 8080 8081
#RUN microdnf reinstall tzdata -y
COPY build/libs/identity-management-ms*.jar /app.jar
ENTRYPOINT ["java"]
CMD ["-Duser.timezone=UTC", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "/app.jar"]
