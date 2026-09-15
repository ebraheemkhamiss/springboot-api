# ===== مرحلة البناء =====
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# نسخ ملف الاعتماديات أولاً للاستفادة من الـ cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# نسخ الكود وبناء المشروع
COPY src ./src
RUN mvn clean package -DskipTests

# ===== مرحلة التشغيل =====
FROM eclipse-temurin:17-jre

WORKDIR /app

# نسخ ملف الـ jar الناتج من مرحلة البناء
COPY --from=build /app/target/api-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
