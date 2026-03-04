# Stage 1: Build frontend
FROM node:20.11.0-alpine AS frontend-builder
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
ARG NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}
RUN npm run build

# Stage 2: Build backend with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app
COPY pom.xml ./
COPY models/ ./models/
COPY interfaces/ ./interfaces/
COPY persistence/ ./persistence/
COPY services/ ./services/
COPY webapp/ ./webapp/
COPY --from=frontend-builder /app/frontend/out ./frontend/out
RUN mvn clean package -DskipTests

# Stage 3: Runtime (Tomcat)
FROM tomcat:10.1-jdk21-temurin-jammy
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=backend-builder /app/webapp/target/webapp.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
