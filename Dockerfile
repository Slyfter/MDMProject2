# Use a base image with Java 17 installed
FROM openjdk:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY playground/target/playground-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application listens on (if applicable)
EXPOSE 8080

# Set the command to run your application
CMD ["java", "-jar", "app.jar"]
