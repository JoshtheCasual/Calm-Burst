#!/bin/bash
# Docker-based build script for Calm Burst
# This builds the APK inside a Docker container without requiring local Android SDK

set -e

echo "=========================================="
echo "Calm Burst - Docker Build Script"
echo "=========================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ ERROR: Docker not found. Please install Docker first."
    echo "   Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "✅ Docker found: $(docker --version)"
echo ""

# Build the Docker image
echo "Building Docker image (this may take a few minutes)..."
docker build -t calm-burst-builder .

echo ""
echo "✅ Docker image built successfully!"
echo ""

# Create a container to extract the APK
echo "Extracting APK from container..."
docker create --name calm-burst-temp calm-burst-builder

# Copy the APK out
docker cp calm-burst-temp:/app/app/build/outputs/apk/debug/app-debug.apk ./app-debug.apk

# Clean up the temporary container
docker rm calm-burst-temp

echo ""
echo "=========================================="
echo "✅ SUCCESS! APK built and extracted!"
echo "=========================================="
echo ""
echo "APK Location: ./app-debug.apk"
echo "File size: $(du -h app-debug.apk | cut -f1)"
echo ""
echo "To install on connected device:"
echo "  adb install app-debug.apk"
echo ""
echo "To clean up Docker image (optional):"
echo "  docker rmi calm-burst-builder"
echo ""
