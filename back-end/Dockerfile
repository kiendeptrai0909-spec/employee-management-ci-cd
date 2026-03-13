FROM maven:3.9.9-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

RUN cp target/*.jar app.jar

CMD ["java","-jar","app.jar"]