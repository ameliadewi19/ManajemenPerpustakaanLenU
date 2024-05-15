# Menggunakan parent image
FROM openjdk:17

# setting workspace directory
WORKDIR /app

# Menyalin jar file ke container
COPY target/perpustakaan-0.0.1-SNAPSHOT.jar app.jar

# Setting port
EXPOSE 9090
EXPOSE 9091

# Menjalankan jar file
ENTRYPOINT ["java", "-jar", "app.jar"]