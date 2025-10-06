# Main Project Makefile

MAKEFLAGS += --no-print-directory

PROJECTS_DIR = projects

.PHONY: build deploy help app lib remove dbcli install clean list default

# Default target - shows help
default:
	@$(MAKE) help

help:
	@echo "Usage:"
	@echo "  make app name=<project_name> id=<groupId> [db=<db_type>]"
	@echo "  make lib name=<project_name> id=<groupId> [db=true]"
	@echo "  make remove name=<project_name>"
	@echo "  make list"
	@echo "  make build name=<project_name>"
	@echo "  make deploy name=<project_name>"
	@echo "  make dbcli name=<project_name>"
	@echo "  make install"
	@echo "  make clean"
	@echo ""
	@echo "Examples:"
	@echo "  make app name=myapi id=com.example db=postgres"
	@echo "  make lib name=mylib id=com.example db=true"
	@echo "  make dbcli name=myapi"

build:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required. Usage: make build name=<project_name>"; \
		exit 1; \
	fi
	@if [ ! -d "$(PROJECTS_DIR)/$(name)" ]; then \
		echo "Error: Project '$(name)' not found in $(PROJECTS_DIR)/ directory"; \
		exit 1; \
	fi
	@if [ ! -f "$(PROJECTS_DIR)/$(name)/Makefile" ]; then \
		echo "Error: No Makefile found in $(PROJECTS_DIR)/$(name)/ directory"; \
		exit 1; \
	fi
	@cd $(PROJECTS_DIR)/$(name) && $(MAKE) build

deploy:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required. Usage: make deploy name=<project_name>"; \
		exit 1; \
	fi
	@if [ ! -d "$(PROJECTS_DIR)/$(name)" ]; then \
		echo "Error: Project '$(name)' not found in $(PROJECTS_DIR)/ directory"; \
		exit 1; \
	fi
	@if [ ! -f "$(PROJECTS_DIR)/$(name)/Makefile" ]; then \
		echo "Error: No Makefile found in $(PROJECTS_DIR)/$(name)/ directory"; \
		exit 1; \
	fi
	@cd $(PROJECTS_DIR)/$(name) && $(MAKE) deploy

app:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required"; \
		echo "Usage: make app name=<app_name> id=<groupId> [db=<db_type>]"; \
		exit 1; \
	fi
	@if [ -z "$(id)" ]; then \
		echo "Error: id parameter required for groupId"; \
		echo "Usage: make app name=<app_name> id=<groupId> [db=<db_type>]"; \
		echo "Example: make app name=my-webapp id=com.mycompany"; \
		exit 1; \
	fi
	@if [ -n "$(db)" ]; then \
		./install.sh --create-webapp $(name) --groupid $(id) --database $(db); \
	else \
		./install.sh --create-webapp $(name) --groupid $(id); \
	fi

lib:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required"; \
		echo "Usage: make lib name=<lib_name> id=<groupId> [db=true]"; \
		exit 1; \
	fi
	@if [ -z "$(id)" ]; then \
		echo "Error: id parameter required for groupId"; \
		echo "Usage: make lib name=<lib_name> id=<groupId> [db=true]"; \
		echo "Example: make lib name=my-library id=com.mycompany.lib"; \
		exit 1; \
	fi
	@if [ "$(db)" = "true" ]; then \
		./install.sh --create-library $(name) --groupid $(id) --with-database; \
	else \
		./install.sh --create-library $(name) --groupid $(id); \
	fi

remove:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required"; \
		echo "Usage: make remove name=<project_name>"; \
		exit 1; \
	fi
	@if [ -d "$(PROJECTS_DIR)/$(name)" ]; then \
		if [ -f "$(PROJECTS_DIR)/$(name)/pom.xml" ] && grep -q "<packaging>war</packaging>" "$(PROJECTS_DIR)/$(name)/pom.xml"; then \
			./install.sh --remove-webapp $(name); \
		else \
			./install.sh --remove-library $(name); \
		fi; \
	else \
		echo "Error: Project '$(name)' not found in $(PROJECTS_DIR)/ directory"; \
		exit 1; \
	fi

clean:
	@echo "Cleaning archetype target directories..."
	@rm -rf archetypes/*/target
	@echo "Archetype target directories cleaned"

install:
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
					printf "  %-20s (groupId: %-30s version: %s)\n" "$$name" "$$groupId" "$$version"; \
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
					printf "  %-20s (groupId: %-30s version: %s)\n" "$$name" "$$groupId" "$$version"; \
				fi; \
			fi; \
		done; \
		echo ""; \
	else \
		echo "No projects found in $(PROJECTS_DIR)/"; \
		echo ""; \
	fi

dbcli:
	@if [ -z "$(name)" ]; then \
		echo "Error: name parameter required. Usage: make dbcli name=<project_name>"; \
		exit 1; \
	fi
	@if [ ! -d "$(PROJECTS_DIR)/$(name)" ]; then \
		echo "Error: Project '$(name)' not found in $(PROJECTS_DIR)/ directory"; \
		exit 1; \
	fi
	@if [ ! -f "$(PROJECTS_DIR)/$(name)/Makefile" ]; then \
		echo "Error: No Makefile found in $(PROJECTS_DIR)/$(name)/ directory"; \
		exit 1; \
	fi
	@cd $(PROJECTS_DIR)/$(name) && $(MAKE) dbcli