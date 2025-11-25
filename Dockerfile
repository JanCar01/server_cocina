FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .

# 👇 Arregla el error de permisos
RUN chmod +x mvnw

RUN ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
