# Microservice Architecture Makefile

PROJECT_NAME = microservice
CONTAINER_NAME = tomee-dev
TOMEE_WEBAPPS = /usr/local/tomee/webapps
WAR_NAME = microservice-webapp

.PHONY: clean build deploy status help test

help:
	@echo "Available targets:"
	@echo "  clean   - Clean build artifacts"
	@echo "  build   - Compile JAR and WAR"
	@echo "  test    - Run unit tests"
	@echo "  deploy  - Deploy to TomEE"
	@echo "  status  - Check deployment status"

clean:
	@echo "Cleaning $(PROJECT_NAME)..."
	@mvn clean -q
	@echo "Clean complete"

test:
	@echo "Running tests for $(PROJECT_NAME)..."
	@mvn test -q
	@echo "Tests complete"

build:
	@echo "Building $(PROJECT_NAME)..."
	@echo "Step 1/2: Building JAR library..."
	@cd microservice-lib && mvn package -q -DskipTests
	@echo "Step 2/2: Building WAR webapp..."
	@cd microservice-webapp && mvn package -q -DskipTests
	@echo "Build complete:"
	@echo "  JAR: microservice-lib/target/microservice-lib.jar"
	@echo "  WAR: microservice-webapp/target/$(WAR_NAME).war"

deploy: build
	@echo "Deploying $(WAR_NAME) to $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(WAR_NAME)*
	@cp microservice-webapp/target/$(WAR_NAME).war $(TOMEE_WEBAPPS)/
	@echo "Deployed: http://localhost:9292/$(WAR_NAME)"
	@echo "Test endpoints:"
	@echo "  http://localhost:9292/$(WAR_NAME)/"
	@echo "  http://localhost:9292/$(WAR_NAME)/api/service"
	@echo "  http://localhost:9292/$(WAR_NAME)/api/service?name=Developer"

status:
	@echo "Checking deployment status..."
	@ls -la $(TOMEE_WEBAPPS)/$(WAR_NAME)* 2>/dev/null || echo "Not deployed"
	@echo ""
	@echo "Checking JAR inclusion in WAR:"
	@if [ -f "$(TOMEE_WEBAPPS)/$(WAR_NAME)/WEB-INF/lib/microservice-lib.jar" ]; then \
		echo "✓ JAR library found in deployed WAR"; \
		ls -la $(TOMEE_WEBAPPS)/$(WAR_NAME)/WEB-INF/lib/; \
	else \
		echo "✗ JAR library not found in deployed WAR"; \
	fi

# Target di sviluppo aggiuntivi
dev-build-jar:
	@echo "Building only JAR library..."
	@cd microservice-lib && mvn package -q
	@echo "JAR ready: microservice-lib/target/microservice-lib.jar"

dev-build-war:
	@echo "Building only WAR webapp..."
	@cd microservice-webapp && mvn package -q
	@echo "WAR ready: microservice-webapp/target/$(WAR_NAME).war"

# Utility per debug
check-war-contents:
	@echo "Checking WAR file contents..."
	@jar -tf microservice-webapp/target/$(WAR_NAME).war | grep -E "(\.jar|Microservice)" | sort

test-endpoints:
	@echo "Testing deployed endpoints..."
	@echo "Testing base endpoint:"
	@curl -s http://localhost:9292/$(WAR_NAME)/api/service | head -5
	@echo ""
	@echo "Testing personalized endpoint:"
	@curl -s "http://localhost:9292/$(WAR_NAME)/api/service?name=Maven" | head -5