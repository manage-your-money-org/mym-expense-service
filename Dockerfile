FROM eclipse-temurin:17-jdk-alpine
COPY build/libs/*.jar mym-expense-service.jar
ENTRYPOINT ["java", "-jar", "/mym-expense-service.jar"]