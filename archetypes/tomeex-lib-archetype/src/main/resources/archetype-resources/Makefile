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
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn clean package -q -DskipTests
	@echo "Copying JAR to /workspace/lib..."
	@mkdir -p /workspace/lib
	@cp target/${artifactId}.jar /workspace/lib/

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

# Generate Javadoc HTML documentation
docs:
	@echo "Generating Javadoc..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn javadoc:javadoc -q

# Generate and install sources JAR
install-sources: build
	@echo "Generating sources JAR..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn source:jar -q
	@echo "Installing sources JAR to Maven repository..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn source:jar-no-fork install:install-file \
		-Dfile=target/${artifactId}-sources.jar \
		-DgroupId=${groupId} \
		-DartifactId=${artifactId} \
		-Dversion=${version} \
		-Dpackaging=jar \
		-Dclassifier=sources \
		-DgeneratePom=false -q
	@cp target/${artifactId}-sources.jar /workspace/lib/ 2>/dev/null || true
	@echo "Sources JAR installed"

# Generate and install javadoc JAR
install-javadoc: build
	@echo "Generating Javadoc JAR..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn javadoc:jar -q
	@echo "Installing Javadoc JAR to Maven repository..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn install:install-file \
		-Dfile=target/${artifactId}-javadoc.jar \
		-DgroupId=${groupId} \
		-DartifactId=${artifactId} \
		-Dversion=${version} \
		-Dpackaging=jar \
		-Dclassifier=javadoc \
		-DgeneratePom=false -q
	@cp target/${artifactId}-javadoc.jar /workspace/lib/ 2>/dev/null || true
	@echo "Javadoc JAR installed"

# Generate and install both sources and javadoc JARs
install-full: install install-sources install-javadoc
	@echo "Full installation complete (main + sources + javadoc)"

# Configure Git safe directory
git-safe:
	@echo "Adding project directory to Git safe.directory..."
	@git config --global --add safe.directory /workspace/projects/${groupId}
	@echo "Git safe.directory configured"

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
	@echo "  build            - Build JAR library"
	@echo "  compile          - Compile source code"
	@echo "  test             - Run unit tests"
	@echo "  test-verbose     - Run tests with verbose output"
	@echo "  install          - Install main JAR to Maven repository [DEFAULT]"
	@echo "  install-sources  - Install sources JAR to Maven repository"
	@echo "  install-javadoc  - Install Javadoc JAR to Maven repository"
	@echo "  install-full     - Install main + sources + javadoc JARs"
	@echo "  clean            - Clean build artifacts"
	@echo "  docs             - Generate Javadoc HTML documentation"
	@echo ""
	@echo "  git-safe         - Add project to Git safe.directory"
	@echo "  check-repo       - Check if library is in local Maven repo"
	@echo "  coordinates      - Show Maven dependency coordinates"
	@echo ""
	@echo "  help             - Show this help message"

.PHONY: build compile test test-verbose install install-sources install-javadoc install-full clean docs git-safe check-repo coordinates help