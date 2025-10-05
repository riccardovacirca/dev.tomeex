MAKEFLAGS += --no-print-directory

# ${artifactId} Webapp Build System
# Usage: make [target]

APP_NAME = ${artifactId}
CONTAINER_NAME = tomee-dev
TOMEE_WEBAPPS = /usr/local/tomee/webapps

.DEFAULT_GOAL := quick-deploy

# Build targets
build:
	@echo "Building $(APP_NAME) webapp (dev profile)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P dev
	@echo "Build complete: target/$(APP_NAME).war"

build-prod:
	@echo "Building $(APP_NAME) webapp (production profile)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P prod
	@echo "Production build complete: target/$(APP_NAME).war"

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

undeploy:
	@echo "Undeploying $(APP_NAME) from $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@echo "Undeployed successfully"

redeploy: undeploy deploy
	@echo "Redeployment complete"

status:
	@echo "Checking deployment status for $(APP_NAME):"
	@ls -la $(TOMEE_WEBAPPS)/$(APP_NAME)* 2>/dev/null || echo "$(APP_NAME) is not deployed"

# Testing
test:
	@echo "Running unit tests for $(APP_NAME)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test

test-verbose:
	@echo "Running unit tests with verbose output..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn test -X

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn clean -q

# Development targets
dev-build: clean compile test
	@echo "$(APP_NAME) development build completed"

dev-deploy: clean deploy
	@echo "$(APP_NAME) clean deployment completed"

# Display help
help:
	@echo "$(APP_NAME) Webapp - Available targets:"
	@echo ""
	@echo "  quick-deploy  - Quick incremental deploy (compile + rsync) [DEFAULT]"
	@echo "  build         - Build WAR file (dev profile with reloadable=true)"
	@echo "  build-prod    - Build WAR file (prod profile with reloadable=false)"
	@echo "  compile       - Compile source code"
	@echo "  deploy        - Build and deploy to TomEE (full deployment)"
	@echo "  undeploy      - Remove webapp from TomEE"
	@echo "  redeploy      - Undeploy and deploy again"
	@echo "  status        - Check deployment status"
	@echo "  test          - Run unit tests"
	@echo "  test-verbose  - Run tests with verbose output"
	@echo "  clean         - Clean build artifacts"
	@echo ""
	@echo "  dev-build     - Full development build (clean + compile + test)"
	@echo "  dev-deploy    - Clean deployment (clean + build + deploy)"
	@echo ""
	@echo "  help          - Show this help message"
	@echo ""
	@echo "Access URL: http://localhost:9292/$(APP_NAME)"

.PHONY: build build-prod compile deploy quick-deploy undeploy redeploy status test test-verbose clean dev-build dev-deploy help
