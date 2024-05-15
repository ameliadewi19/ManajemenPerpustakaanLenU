# Gunakan image dasar dari Eclipse Temurin dengan JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Tentukan working directory di dalam container
WORKDIR /app

# Copy file pom.xml, Maven Wrapper, dan source code ke dalam container
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src ./src

# Berikan izin eksekusi untuk Maven Wrapper
RUN chmod +x ./mvnw

# Download dependencies, compile dan package aplikasi
RUN ./mvnw dependency:resolve
RUN ./mvnw clean package -DskipTests

# Copy file jar dari target folder ke dalam container
COPY target/*.jar app.jar

# Tentukan command untuk menjalankan aplikasi
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
