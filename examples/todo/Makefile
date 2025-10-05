# Todo Makefile

APP_NAME = todo
CONTAINER_NAME = tomee-dev
TOMEE_WEBAPPS = /usr/local/tomee/webapps

.PHONY: clean build deploy status help

help:
	@echo "Targets: clean, build, deploy, status"

clean:
	@mvn clean -q

build:
	@echo "Building $(APP_NAME)..."
	@mvn package -q -DskipTests
	@echo "Build complete: target/$(APP_NAME).war"

deploy: build
	@echo "Deploying to $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@cp target/$(APP_NAME).war $(TOMEE_WEBAPPS)/
	@echo "Deployed: http://localhost:9292/$(APP_NAME)"

status:
	@ls -la $(TOMEE_WEBAPPS)/$(APP_NAME)* 2>/dev/null || echo "Not deployed"