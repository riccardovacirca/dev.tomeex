
MAKEFLAGS += --no-print-directory

PROJECTS_DIR = projects

.PHONY: help app lib remove archetypes list push pull default

default:
	@$(MAKE) help

help:
	@echo "Usage:"
	@echo "  make app id=<groupId.artifactId> [db=<db_type>]"
	@echo "  make lib id=<groupId.artifactId> [db=true]"
	@echo "  make remove id=<groupId.artifactId>"
	@echo "  make list"
	@echo "  make archetypes"
	@echo "  make push m=\"<commit message>\""
	@echo "  make pull"
	@echo ""
	@echo "Examples:"
	@echo "  make app id=com.example.myapi db=postgres"
	@echo "  make lib id=com.example.mylib db=true"
	@echo "  make remove id=com.example.myapi"
	@echo "  make push m=\"updated project\""
	@echo "  make pull"
	@echo ""
	@echo "Note: artifactId is extracted from the last part of id"
	@echo "      id=com.example.myapi → groupId=com.example.myapi, artifactId=myapi"

app:
	@if [ -z "$(id)" ]; then \
		echo "Error: id parameter required"; \
		echo "Usage: make app id=<groupId.artifactId> [db=<db_type>]"; \
		echo "Example: make app id=com.example.myapp db=postgres"; \
		exit 1; \
	fi
	@NAME=$$(echo "$(id)" | rev | cut -d. -f1 | rev); \
	if [ -z "$$NAME" ]; then \
		echo "Error: Cannot extract artifactId from id=$(id)"; \
		echo "Id must contain at least one dot (e.g., com.example.myapp)"; \
		exit 1; \
	fi; \
	if [ -n "$(db)" ]; then \
		./install.sh --create-webapp $$NAME --groupid $(id) --database $(db); \
	else \
		./install.sh --create-webapp $$NAME --groupid $(id); \
	fi

lib:
	@if [ -z "$(id)" ]; then \
		echo "Error: id parameter required"; \
		echo "Usage: make lib id=<groupId.artifactId> [db=true]"; \
		echo "Example: make lib id=com.example.mylib db=true"; \
		exit 1; \
	fi
	@NAME=$$(echo "$(id)" | rev | cut -d. -f1 | rev); \
	if [ -z "$$NAME" ]; then \
		echo "Error: Cannot extract artifactId from id=$(id)"; \
		echo "Id must contain at least one dot (e.g., com.example.mylib)"; \
		exit 1; \
	fi; \
	if [ "$(db)" = "true" ]; then \
		./install.sh --create-library $$NAME --groupid $(id) --with-database; \
	else \
		./install.sh --create-library $$NAME --groupid $(id); \
	fi

remove:
	@if [ -z "$(id)" ]; then \
		echo "Error: id parameter required"; \
		echo "Usage: make remove id=<groupId>"; \
		exit 1; \
	fi
	@if [ -d "$(PROJECTS_DIR)/$(id)" ]; then \
		if [ -f "$(PROJECTS_DIR)/$(id)/pom.xml" ]; then \
			artifactId=$$(grep -m1 "<artifactId>" "$(PROJECTS_DIR)/$(id)/pom.xml" | sed 's/.*<artifactId>\(.*\)<\/artifactId>.*/\1/' | xargs); \
			if grep -q "<packaging>war</packaging>" "$(PROJECTS_DIR)/$(id)/pom.xml"; then \
				./install.sh --remove-webapp $(id) --artifactid $$artifactId; \
			else \
				./install.sh --remove-library $(id); \
			fi; \
		else \
			echo "Error: pom.xml not found in $(PROJECTS_DIR)/$(id)/"; \
			exit 1; \
		fi; \
	else \
		echo "Error: Project '$(id)' not found in $(PROJECTS_DIR)/ directory"; \
		exit 1; \
	fi

archetypes:
	@echo "Cleaning archetype target directories..."
	@rm -rf archetypes/*/target
	@echo "Removing archetypes from local Maven repository..."
	@rm -rf ~/.m2/repository/com/example/archetypes/tomeex-* ~/.m2/repository/dev/tomeex/archetypes/tomeex-*
	@echo "Rebuilding and installing archetypes..."
	@cd archetypes && for archetype in */; do \
		echo "Installing $$archetype"; \
		(cd "$$archetype" && mvn clean install -q); \
	done
	@echo "Archetypes rebuilt and installed"

list:
	@echo ""
	@if [ -d "$(PROJECTS_DIR)" ] && [ -n "$$(ls -A $(PROJECTS_DIR) 2>/dev/null)" ]; then \
		echo "WEBAPPS:"; \
		for project in $(PROJECTS_DIR)/*; do \
			if [ -f "$$project/pom.xml" ]; then \
				if grep -q "<packaging>war</packaging>" "$$project/pom.xml" 2>/dev/null; then \
					name=$$(basename "$$project"); \
					groupId=$$(grep -m1 "<groupId>" "$$project/pom.xml" | sed 's/.*<groupId>\(.*\)<\/groupId>.*/\1/' | xargs); \
					version=$$(grep -m1 "<version>" "$$project/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | xargs); \
					printf "  %-20s (version: %s)\n" "$$name" "$$version"; \
				fi; \
			fi; \
		done; \
		echo ""; \
		echo "LIBRARIES:"; \
		for project in $(PROJECTS_DIR)/*; do \
			if [ -f "$$project/pom.xml" ]; then \
				if grep -q "<packaging>jar</packaging>" "$$project/pom.xml" 2>/dev/null; then \
					name=$$(basename "$$project"); \
					groupId=$$(grep -m1 "<groupId>" "$$project/pom.xml" | sed 's/.*<groupId>\(.*\)<\/groupId>.*/\1/' | xargs); \
					version=$$(grep -m1 "<version>" "$$project/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | xargs); \
					printf "  %-20s (version: %s)\n" "$$name" "$$version"; \
				fi; \
			fi; \
		done; \
		echo ""; \
	else \
		echo "No projects found in $(PROJECTS_DIR)/"; \
		echo ""; \
	fi

push:
	@if [ -z "$(m)" ]; then \
		echo "Error: commit message required"; \
		echo "Usage: make push m=\"<commit message>\""; \
		echo "Example: make push m=\"updated project\""; \
		exit 1; \
	fi
	@echo "Configuring Git..."
	@git config --global --add safe.directory /workspace
	@git config --global credential.helper store
	@if [ -f .env ]; then \
		GIT_USER=$$(grep "^GIT_USER=" .env | cut -d= -f2); \
		GIT_MAIL=$$(grep "^GIT_MAIL=" .env | cut -d= -f2); \
		if [ -n "$$GIT_USER" ] && [ -n "$$GIT_MAIL" ]; then \
			git config user.name "$$GIT_USER"; \
			git config user.email "$$GIT_MAIL"; \
			echo "Git configured: $$GIT_USER <$$GIT_MAIL>"; \
		else \
			echo "Warning: GIT_USER or GIT_MAIL not set in .env"; \
			echo "Please configure Git credentials in .env file"; \
			exit 1; \
		fi; \
	else \
		echo "Error: .env file not found"; \
		exit 1; \
	fi
	@echo "Adding files to Git..."
	@git add .
	@echo "Committing changes..."
	@git commit -m "$(m)"
	@echo "Pushing to remote repository..."
	@git push
	@echo "Push completed successfully"

pull:
	@echo "Configuring Git..."
	@git config --global --add safe.directory /workspace
	@git config --global credential.helper store
	@if [ -f .env ]; then \
		GIT_USER=$$(grep "^GIT_USER=" .env | cut -d= -f2); \
		GIT_MAIL=$$(grep "^GIT_MAIL=" .env | cut -d= -f2); \
		if [ -n "$$GIT_USER" ] && [ -n "$$GIT_MAIL" ]; then \
			git config user.name "$$GIT_USER"; \
			git config user.email "$$GIT_MAIL"; \
			echo "Git configured: $$GIT_USER <$$GIT_MAIL>"; \
		else \
			echo "Warning: GIT_USER or GIT_MAIL not set in .env"; \
			echo "Please configure Git credentials in .env file"; \
			exit 1; \
		fi; \
	else \
		echo "Error: .env file not found"; \
		exit 1; \
	fi
	@echo "Pulling changes from remote repository..."
	@git pull
	@echo "Pull completed successfully"