MAKEFLAGS += --no-print-directory

# ${artifactId} Webapp Build System
# Usage: make [target]

APP_NAME = ${artifactId}
CONTAINER_NAME = tomeex
TOMEE_WEBAPPS = /usr/local/tomee/webapps

.DEFAULT_GOAL := quick-deploy

# Build targets
build:
	@echo "Building $(APP_NAME) webapp (dev profile)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P dev
	@echo "Build complete: target/$(APP_NAME).war"

release:
	@echo "Building $(APP_NAME) release package..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P prod
	@echo "Creating release distribution..."
	@rm -rf release-dist
	@mkdir -p release-dist
	@cp target/$(APP_NAME).war release-dist/
	@cp LICENSE.md release-dist/
	@cp install.sh release-dist/
	@chmod +x release-dist/install.sh
	@echo "Packaging release archive..."
	@tar -czf $(APP_NAME)-release.tar.gz -C release-dist .
	@rm -rf release-dist
	@echo ""
	@echo "✓ Release package created: $(APP_NAME)-release.tar.gz"
	@echo ""
	@echo "Contents:"
	@echo "  - $(APP_NAME).war (production build)"
	@echo "  - LICENSE.md"
	@echo "  - install.sh (Docker deployment script)"
	@echo ""
	@echo "To deploy:"
	@echo "  1. Extract: tar -xzf $(APP_NAME)-release.tar.gz"
	@echo "  2. Run: ./install.sh --help"
	@echo ""

compile:
	@echo "Compiling $(APP_NAME)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn compile -q

# Deployment
deploy: build
	@echo "Deploying $(APP_NAME) to $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@cp target/$(APP_NAME).war $(TOMEE_WEBAPPS)/
	@echo "Deployed: http://localhost:9292/$(APP_NAME)"
	@echo "Wait a few seconds for TomEE to extract and deploy the WAR file"

# Quick incremental deployment using rsync
quick-deploy: compile
	@echo "Quick deploying $(APP_NAME) (incremental sync)..."
	@if [ ! -d "$(TOMEE_WEBAPPS)/$(APP_NAME)" ]; then \
		echo "Error: WAR not yet extracted. Run 'make deploy' first for initial deployment."; \
		exit 1; \
	fi
	@rsync -a --delete target/classes/ $(TOMEE_WEBAPPS)/$(APP_NAME)/WEB-INF/classes/
	@if [ -d "src/main/webapp/" ]; then \
		rsync -a src/main/webapp/ $(TOMEE_WEBAPPS)/$(APP_NAME)/; \
	fi
	@touch $(TOMEE_WEBAPPS)/$(APP_NAME)/WEB-INF/web.xml
	@echo "Quick deploy complete: http://localhost:9292/$(APP_NAME)"
	@echo "Changes synced to exploded WAR (hot reload may occur)"

# Testing
test:
	@echo "Running unit tests for $(APP_NAME)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test

test-verbose:
	@echo "Running unit tests with verbose output..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test -X

# Clean build artifacts and remove from TomEE
clean:
	@echo "Cleaning build artifacts..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn clean -q
	@echo "Removing $(APP_NAME) from TomEE webapps..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@echo "Clean complete"

# Display help
help:
	@echo "$(APP_NAME) Webapp - Available targets:"
	@echo ""
	@echo "  quick-deploy  - Quick incremental deploy (compile + rsync) [DEFAULT]"
	@echo "  build         - Build WAR file (dev profile with reloadable=true)"
	@echo "  release       - Create production release package (tar.gz)"
	@echo "  compile       - Compile source code"
	@echo "  deploy        - Build and deploy to TomEE (full deployment)"
	@echo "  test          - Run unit tests"
	@echo "  test-verbose  - Run tests with verbose output"
	@echo "  clean         - Clean build artifacts and remove from TomEE"
	@echo ""
	@echo "  help          - Show this help message"
	@echo ""
	@echo "Access URL: http://localhost:9292/$(APP_NAME)"
	@echo ""
	@echo "For clean redeploy: make clean && make deploy"

.PHONY: build release compile deploy quick-deploy test test-verbose clean help
