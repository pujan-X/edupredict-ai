# Use an official Java image
FROM eclipse-temurin:17-jdk-jammy

# Install Python and pip
RUN apt-get update && apt-get install -y python3 python3-pip

# Copy your project code into the server
COPY . .

# 👈 THIS IS THE NEW FIX: Give permission to run the Maven wrapper
RUN chmod +x mvnw

# Build the Java application
RUN ./mvnw clean package -DskipTests

# Start the application 
CMD ["java", "-jar", "target/edupredict-0.0.1-SNAPSHOT.jar"]