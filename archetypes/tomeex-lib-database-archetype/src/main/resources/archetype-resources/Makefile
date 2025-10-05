MAKEFLAGS += --no-print-directory

# ${artifactId} Library Build System
# Usage: make [target]

.DEFAULT_GOAL := install

# Build targets
compile:
	@echo "Compiling ${artifactId} library..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn compile -q

build: compile
	@echo "Packaging ${artifactId} library..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests
	@echo "Copying JAR to /workspace/lib..."
	@mkdir -p /workspace/lib
	@cp target/*.jar /workspace/lib/

# Testing
test:
	@echo "Running unit tests for ${artifactId}..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test

test-verbose:
	@echo "Running unit tests with verbose output..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test -X

# Install to local Maven repository
install: build
	@echo "Installing ${artifactId} to local Maven repository..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn install -q -DskipTests

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn clean -q

# Generate Javadoc
docs:
	@echo "Generating Javadoc..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn javadoc:javadoc -q

# Check if library is available in local repo
check-repo:
	@echo "Checking if ${artifactId} is installed in local Maven repository:"
	@find ~/.m2/repository -name "*${artifactId}*" -type f 2>/dev/null | head -5 || echo "Library not found in local repository"

# Show Maven coordinates for using this library
coordinates:
	@echo "To use this library in other projects, add this dependency:"
	@echo ""
	@echo "<dependency>"
	@echo "    <groupId>${groupId}</groupId>"
	@echo "    <artifactId>${artifactId}</artifactId>"
	@echo "    <version>${version}</version>"
	@echo "</dependency>"

# Display help
help:
	@echo "${artifactId} Library - Available targets:"
	@echo ""
	@echo "  build          - Build JAR library"
	@echo "  compile        - Compile source code"
	@echo "  test          - Run unit tests"
	@echo "  test-verbose  - Run tests with verbose output"
	@echo "  install       - Install to local Maven repository"
	@echo "  clean         - Clean build artifacts"
	@echo "  docs          - Generate Javadoc"
	@echo ""
	@echo "  check-repo    - Check if library is in local Maven repo"
	@echo "  coordinates   - Show Maven dependency coordinates"
	@echo ""
	@echo "  help          - Show this help message"

.PHONY: build compile test test-verbose install clean docs check-repo coordinates help