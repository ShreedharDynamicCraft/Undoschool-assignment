#!/bin/bash

# Course Search API - Quick Start Script
# This script helps you quickly set up and run the Course Search API

set -e  # Exit on any error

echo "🚀 Course Search API - Quick Start"
echo "=================================="

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
echo "📋 Checking prerequisites..."

if ! command_exists java; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

if ! command_exists mvn; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

if ! command_exists docker; then
    echo "❌ Docker is not installed. Please install Docker."
    exit 1
fi

if ! command_exists docker-compose; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose."
    exit 1
fi

echo "✅ All prerequisites are installed!"

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version is $JAVA_VERSION. Please use Java 17 or higher."
    exit 1
fi

echo "✅ Java version is compatible!"

# Start Elasticsearch
echo ""
echo "🔍 Starting Elasticsearch..."
docker-compose up -d

# Wait for Elasticsearch to be ready
echo "⏳ Waiting for Elasticsearch to be ready..."
timeout=60
counter=0
while ! curl -s http://localhost:9200 > /dev/null; do
    sleep 2
    counter=$((counter + 2))
    if [ $counter -ge $timeout ]; then
        echo "❌ Elasticsearch did not start within $timeout seconds"
        exit 1
    fi
    echo "   Still waiting... ($counter/$timeout seconds)"
done

echo "✅ Elasticsearch is ready!"

# Verify Elasticsearch
echo ""
echo "🔍 Elasticsearch Health Check:"
curl -s http://localhost:9200 | jq . || curl -s http://localhost:9200

# Build the application
echo ""
echo "🔨 Building the application..."
mvn clean compile -q

# Run the application
echo ""
echo "🚀 Starting Course Search API..."
echo "   The application will start on http://localhost:8080"
echo "   Press Ctrl+C to stop the application"
echo ""

# Start the Spring Boot application
mvn spring-boot:run
