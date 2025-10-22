MAKEFLAGS += --no-print-directory

# ${artifactId} Webapp Build System
# Usage: make [target]

APP_NAME = ${artifactId}
CONTAINER_NAME = tomeex
TOMEE_WEBAPPS = /usr/local/tomee/webapps

# Git Configuration (optional - override if needed)
# GIT_USER = Your Name
# GIT_MAIL = your.email@example.com

.DEFAULT_GOAL := deploy

# Build targets
build:
	@echo "Building $(APP_NAME) webapp (dev profile)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P dev
	@echo "Build complete: target/$(APP_NAME).war"

release:
	@if [ -z "$(v)" ]; then \
		echo "Error: version required"; \
		echo "Usage: make release v=<version>"; \
		echo "Example: make release v=1.0.0"; \
		exit 1; \
	fi
	@echo "Building $(APP_NAME) release package v$(v)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn package -q -DskipTests -P prod
	@echo "Creating release distribution..."
	@rm -rf release-dist
	@mkdir -p release-dist/$(APP_NAME)-$(v)
	@mkdir -p release-dist/$(APP_NAME)-$(v)/database
	@cp target/$(APP_NAME).war release-dist/$(APP_NAME)-$(v)/
	@cp LICENSE.md release-dist/$(APP_NAME)-$(v)/
	@if [ -f database/${artifactId}.sql ]; then \
		cp database/${artifactId}.sql release-dist/$(APP_NAME)-$(v)/database/; \
		if [ -f .env ]; then \
			DB_TYPE_VAL=$$(grep "^DB_TYPE=" .env | cut -d= -f2); \
			if [ "$$DB_TYPE_VAL" = "sqlite" ]; then \
				echo "Generating fresh SQLite database for release..."; \
				rm -f release-dist/$(APP_NAME)-$(v)/database/$(APP_NAME).db; \
				sqlite3 release-dist/$(APP_NAME)-$(v)/database/$(APP_NAME).db < database/${artifactId}.sql; \
				echo "  ✓ Fresh SQLite database created: database/$(APP_NAME).db"; \
			fi; \
		fi; \
	fi
	@echo "Generating install.sh from template..."
	@if [ ! -f .env ]; then \
		echo "Warning: .env not found, using defaults"; \
		DB_TYPE_VAL="unknown"; \
	else \
		DB_TYPE_VAL=$$(grep "^DB_TYPE=" .env | cut -d= -f2); \
		if [ -z "$$DB_TYPE_VAL" ]; then DB_TYPE_VAL="unknown"; fi; \
	fi; \
	TOMEE_VER=$$(grep "^TOMEE_VERSION=" /workspace/.env 2>/dev/null | cut -d= -f2); \
	if [ -z "$$TOMEE_VER" ]; then TOMEE_VER="9-jre17-plume"; fi; \
	sed -e "s|ARTIFACT_ID_PLACEHOLDER|$(APP_NAME)|g" \
	    -e "s|RELEASE_VERSION_PLACEHOLDER|$(v)|g" \
	    -e "s|TOMEE_VERSION_PLACEHOLDER|$$TOMEE_VER|g" \
	    -e "s|DB_TYPE_PLACEHOLDER|$$DB_TYPE_VAL|g" \
	    install.sh.template > release-dist/$(APP_NAME)-$(v)/install.sh
	@chmod +x release-dist/$(APP_NAME)-$(v)/install.sh
	@echo "Generating README.md from template..."
	@if [ ! -f .env ]; then \
		DB_TYPE_VAL="unknown"; \
		DB_TYPE_UPPER="Unknown"; \
		DB_DRIVER="unknown"; \
		DB_URL="jdbc:unknown://localhost/dbname"; \
		DB_INIT_COMMAND="# Database initialization command"; \
	else \
		DB_TYPE_VAL=$$(grep "^DB_TYPE=" .env | cut -d= -f2); \
		if [ -z "$$DB_TYPE_VAL" ]; then DB_TYPE_VAL="unknown"; fi; \
		case "$$DB_TYPE_VAL" in \
			postgres) \
				DB_TYPE_UPPER="PostgreSQL"; \
				DB_DRIVER="org.postgresql.Driver"; \
				DB_URL="jdbc:postgresql://db-host:5432/dbname"; \
				DB_INIT_COMMAND="psql -h db-host -U db-user -d dbname -f database/$(APP_NAME).sql"; \
				;; \
			mariadb) \
				DB_TYPE_UPPER="MariaDB"; \
				DB_DRIVER="org.mariadb.jdbc.Driver"; \
				DB_URL="jdbc:mariadb://db-host:3306/dbname"; \
				DB_INIT_COMMAND="mysql -h db-host -u db-user -p dbname < database/$(APP_NAME).sql"; \
				;; \
			sqlite) \
				DB_TYPE_UPPER="SQLite"; \
				DB_DRIVER="org.sqlite.JDBC"; \
				DB_URL="jdbc:sqlite:/path/to/database.db"; \
				DB_INIT_COMMAND="sqlite3 /path/to/database.db < database/$(APP_NAME).sql"; \
				;; \
			*) \
				DB_TYPE_UPPER="Unknown"; \
				DB_DRIVER="unknown"; \
				DB_URL="jdbc:unknown://localhost/dbname"; \
				DB_INIT_COMMAND="# Database initialization command"; \
				;; \
		esac; \
	fi; \
	TOMEE_VER=$$(grep "^TOMEE_VERSION=" /workspace/.env 2>/dev/null | cut -d= -f2); \
	if [ -z "$$TOMEE_VER" ]; then TOMEE_VER="9-jre17-plume"; fi; \
	BUILD_DATE=$$(date +"%Y-%m-%d %H:%M:%S"); \
	LICENSE_INFO=$$(head -1 LICENSE.md 2>/dev/null || echo "See LICENSE.md"); \
	sed -e "s|ARTIFACT_ID_PLACEHOLDER|$(APP_NAME)|g" \
	    -e "s|RELEASE_VERSION_PLACEHOLDER|$(v)|g" \
	    -e "s|TOMEE_VERSION_PLACEHOLDER|$$TOMEE_VER|g" \
	    -e "s|DB_TYPE_PLACEHOLDER|$$DB_TYPE_VAL|g" \
	    -e "s|DB_TYPE_UPPER_PLACEHOLDER|$$DB_TYPE_UPPER|g" \
	    -e "s|DB_DRIVER_PLACEHOLDER|$$DB_DRIVER|g" \
	    -e "s|DB_URL_PLACEHOLDER|$$DB_URL|g" \
	    -e "s|DB_INIT_COMMAND_PLACEHOLDER|$$DB_INIT_COMMAND|g" \
	    -e "s|BUILD_DATE_PLACEHOLDER|$$BUILD_DATE|g" \
	    -e "s|LICENSE_INFO_PLACEHOLDER|$$LICENSE_INFO|g" \
	    -e "s|TOMEEX_VERSION_PLACEHOLDER|1.0.0|g" \
	    README-RELEASE.md.template > release-dist/$(APP_NAME)-$(v)/README.md
	@echo "Packaging release archive..."
	@cd release-dist && tar -czf $(APP_NAME)-$(v).tar.gz $(APP_NAME)-$(v)
	@mkdir -p /workspace/dist
	@mv release-dist/$(APP_NAME)-$(v).tar.gz /workspace/dist/
	@rm -rf release-dist
	@echo ""
	@echo "✓ Release package created: /workspace/dist/$(APP_NAME)-$(v).tar.gz"
	@echo ""
	@echo "Contents:"
	@echo "  - $(APP_NAME).war (production build)"
	@echo "  - install.sh (Docker deployment script)"
	@echo "  - database/$(APP_NAME).sql (database initialization)"
	@echo "  - README.md (deployment instructions)"
	@echo "  - LICENSE.md"
	@echo ""
	@echo "To deploy:"
	@echo "  1. cd /workspace/dist"
	@echo "  2. tar -xzf $(APP_NAME)-$(v).tar.gz"
	@echo "  3. cd $(APP_NAME)-$(v)"
	@echo "  4. ./install.sh --help"
	@echo ""

compile:
	@echo "Compiling $(APP_NAME)..."
	@export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 && mvn compile -q

# Setup database from .env configuration
setup-database:
	@if [ ! -f .env ]; then \
		echo "No .env file found, skipping database setup"; \
		exit 0; \
	fi
	@DB_NAME=$$(grep "^DB_NAME=" .env | cut -d= -f2); \
	DB_TYPE=$$(grep "^DB_TYPE=" .env | cut -d= -f2); \
	DB_PASSWORD=$$(grep "^DB_PASSWORD=" .env | cut -d= -f2); \
	if [ -z "$$DB_TYPE" ] || [ -z "$$DB_NAME" ]; then \
		echo "No database configuration in .env, skipping"; \
		exit 0; \
	fi; \
	if [ -z "$$DB_PASSWORD" ]; then \
		DB_PASSWORD="secret"; \
	fi; \
	echo "Setting up $$DB_TYPE database '$$DB_NAME'..."; \
	cd /workspace && ./install.sh --setup-db "$$DB_NAME" "$$DB_TYPE" "$$DB_PASSWORD"

# Install (first-time deployment for cloned projects)
install: setup-database deploy
	@echo "Installation complete"

# Deployment
deploy: build
	@echo "Deploying $(APP_NAME) to $(CONTAINER_NAME)..."
	@rm -rf $(TOMEE_WEBAPPS)/$(APP_NAME)*
	@cp target/$(APP_NAME).war $(TOMEE_WEBAPPS)/
	@echo ""
	@echo "Deployed successfully!"
	@echo "  External (host):      http://localhost:9292/$(APP_NAME)"
	@echo "  Internal (container): http://localhost:8080/$(APP_NAME)"
	@echo ""
	@echo "Wait a few seconds for TomEE to extract and deploy the WAR file"

# Quick incremental deployment using rsync (DISABLED - JNDI issues with hot reload)
# WARNING: quick-deploy causes JNDI context corruption after multiple hot reloads
# This results in database connection failures. Use 'make deploy' instead.
quick-deploy: compile
	@echo "⚠️  WARNING: Quick deploy is currently disabled due to JNDI issues"
	@echo "⚠️  After multiple hot reloads, database connections fail"
	@echo "⚠️  Use 'make deploy' for reliable deployment"
	@echo ""
	@echo "Proceeding with full deployment..."
	@$(MAKE) deploy

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

# Configure Git safe directory
git-safe:
	@echo "Adding project directory to Git safe.directory..."
	@git config --global --add safe.directory /workspace/projects/${groupId}
	@echo "Git safe.directory configured"

# Git commit and push
push:
	@if [ -z "$(m)" ]; then \
		echo "Error: commit message required"; \
		echo "Usage: make push m=\"<commit message>\""; \
		echo "Example: make push m=\"updated webapp\""; \
		exit 1; \
	fi
	@echo "Configuring Git..."
	@git config --global --add safe.directory /workspace/projects/${groupId}
	@git config --global credential.helper store
	@USER="$(GIT_USER)"; \
	MAIL="$(GIT_MAIL)"; \
	if [ -z "$$USER" ] || [ -z "$$MAIL" ]; then \
		if [ -f /workspace/.env ]; then \
			USER=$$(grep "^GIT_USER=" /workspace/.env | cut -d= -f2 | sed 's/^#.*//'); \
			MAIL=$$(grep "^GIT_MAIL=" /workspace/.env | cut -d= -f2 | sed 's/^#.*//'); \
		fi; \
	fi; \
	if [ -z "$$USER" ] || [ -z "$$MAIL" ]; then \
		echo "Error: Git credentials not configured"; \
		echo "Please set GIT_USER and GIT_MAIL either:"; \
		echo "  1. In this Makefile (uncomment and edit lines 11-12)"; \
		echo "  2. In /workspace/.env file"; \
		exit 1; \
	fi; \
	git config user.name "$$USER"; \
	git config user.email "$$MAIL"; \
	echo "Git configured: $$USER <$$MAIL>"
	@echo "Adding files to Git..."
	@git add .
	@echo "Committing changes..."
	@git commit -m "$(m)"
	@echo "Pushing to remote repository..."
	@git push
	@echo "Push completed successfully"

# Git pull from remote repository
pull:
	@echo "Configuring Git..."
	@git config --global --add safe.directory /workspace/projects/${groupId}
	@git config --global credential.helper store
	@USER="$(GIT_USER)"; \
	MAIL="$(GIT_MAIL)"; \
	if [ -z "$$USER" ] || [ -z "$$MAIL" ]; then \
		if [ -f /workspace/.env ]; then \
			USER=$$(grep "^GIT_USER=" /workspace/.env | cut -d= -f2 | sed 's/^#.*//'); \
			MAIL=$$(grep "^GIT_MAIL=" /workspace/.env | cut -d= -f2 | sed 's/^#.*//'); \
		fi; \
	fi; \
	if [ -z "$$USER" ] || [ -z "$$MAIL" ]; then \
		echo "Error: Git credentials not configured"; \
		echo "Please set GIT_USER and GIT_MAIL either:"; \
		echo "  1. In this Makefile (uncomment and edit lines 11-12)"; \
		echo "  2. In /workspace/.env file"; \
		exit 1; \
	fi; \
	git config user.name "$$USER"; \
	git config user.email "$$MAIL"; \
	echo "Git configured: $$USER <$$MAIL>"
	@echo "Pulling changes from remote repository..."
	@git pull
	@echo "Pull completed successfully"

# Database CLI - Connect to application database or execute SQL/CSV file
dbcli:
	@if [ ! -f .env ]; then \
		echo "Error: .env file not found"; \
		exit 1; \
	fi
	@. ./.env && \
	if [ -n "$(f)" ]; then \
		if [ ! -f "$(f)" ]; then \
			echo "Error: File not found: $(f)"; \
			exit 1; \
		fi; \
		FILE_EXT=$$(echo "$(f)" | grep -o '\.[^.]*$$'); \
		if [ "$$FILE_EXT" = ".csv" ]; then \
			TABLE_NAME=$$(basename "$(f)" .csv); \
			COLUMNS=$$(head -1 "$(f)" | sed 's/"//g' | sed 's/;/,/g'); \
			if [ "$$DB_TYPE" = "postgres" ]; then \
				echo "Loading CSV file into PostgreSQL table: $$TABLE_NAME"; \
				echo "Columns: $$COLUMNS"; \
				PGPASSWORD=$$DB_PASSWORD psql -h $$DB_HOST -p $$DB_PORT -U $$DB_USER -d $$DB_NAME -c "\\COPY $$TABLE_NAME ($$COLUMNS) FROM '$(f)' WITH (FORMAT CSV, HEADER true, DELIMITER ';', QUOTE '\\\"', NULL '\\\\N')"; \
				echo "CSV file loaded successfully"; \
			elif [ "$$DB_TYPE" = "mariadb" ]; then \
				echo "Loading CSV file into MariaDB table: $$TABLE_NAME"; \
				mysql -h $$DB_HOST -P $$DB_PORT -u $$DB_USER -p$$DB_PASSWORD $$DB_NAME -e "LOAD DATA LOCAL INFILE '$(f)' INTO TABLE $$TABLE_NAME FIELDS TERMINATED BY ';' ENCLOSED BY '\\\"' LINES TERMINATED BY '\\\\n' IGNORE 1 LINES;"; \
				echo "CSV file loaded successfully"; \
			elif [ "$$DB_TYPE" = "sqlite" ]; then \
				echo "Loading CSV file into SQLite table: $$TABLE_NAME"; \
				sqlite3 $$DB_PATH ".mode csv" ".separator ';'" ".import $(f) $$TABLE_NAME"; \
				echo "CSV file loaded successfully"; \
			else \
				echo "Error: Unknown database type: $$DB_TYPE"; \
				exit 1; \
			fi; \
		elif [ "$$FILE_EXT" = ".sql" ]; then \
			if [ "$$DB_TYPE" = "postgres" ]; then \
				echo "Loading SQL file into PostgreSQL database: $$DB_NAME"; \
				PGPASSWORD=$$DB_PASSWORD psql -h $$DB_HOST -p $$DB_PORT -U $$DB_USER -d $$DB_NAME -f "$(f)"; \
			elif [ "$$DB_TYPE" = "mariadb" ]; then \
				echo "Loading SQL file into MariaDB database: $$DB_NAME"; \
				mysql -h $$DB_HOST -P $$DB_PORT -u $$DB_USER -p$$DB_PASSWORD $$DB_NAME < "$(f)"; \
			elif [ "$$DB_TYPE" = "sqlite" ]; then \
				echo "Loading SQL file into SQLite database: $$DB_PATH"; \
				sqlite3 $$DB_PATH < "$(f)"; \
			else \
				echo "Error: Unknown database type: $$DB_TYPE"; \
				exit 1; \
			fi; \
			echo "SQL file loaded successfully"; \
		else \
			echo "Error: Unsupported file type: $$FILE_EXT"; \
			echo "Supported: .sql, .csv"; \
			exit 1; \
		fi; \
	else \
		if [ "$$DB_TYPE" = "postgres" ]; then \
			echo "Connecting to PostgreSQL database: $$DB_NAME"; \
			echo "Use \\q to quit"; \
			PGPASSWORD=$$DB_PASSWORD psql -h $$DB_HOST -p $$DB_PORT -U $$DB_USER -d $$DB_NAME; \
		elif [ "$$DB_TYPE" = "mariadb" ]; then \
			echo "Connecting to MariaDB database: $$DB_NAME"; \
			echo "Use 'exit' or 'quit' to quit"; \
			mysql -h $$DB_HOST -P $$DB_PORT -u $$DB_USER -p$$DB_PASSWORD $$DB_NAME; \
		elif [ "$$DB_TYPE" = "sqlite" ]; then \
			echo "Connecting to SQLite database: $$DB_PATH"; \
			echo "Use .quit to quit"; \
			sqlite3 $$DB_PATH; \
		else \
			echo "Error: Unknown database type: $$DB_TYPE"; \
			exit 1; \
		fi; \
	fi

# Database initialization - Execute SQL file for database setup
db:
	@if [ ! -f database/${artifactId}.sql ]; then \
		echo "Error: Database initialization file not found: database/${artifactId}.sql"; \
		exit 1; \
	fi
	@echo "Initializing database with database/${artifactId}.sql..."
	@$(MAKE) dbcli f=database/${artifactId}.sql
	@echo "Database initialization complete"

# Add ContextView functionality to this webapp
contextview:
	@echo "Adding ContextView functionality to $(APP_NAME)..."
	@cd /tmp && rm -rf contextview-addon && \
	mvn archetype:generate -B \
		-DarchetypeGroupId=dev.tomeex.archetypes \
		-DarchetypeArtifactId=tomeex-addon-contextview \
		-DarchetypeVersion=1.0.0 \
		-DgroupId=${groupId} \
		-DartifactId=contextview-addon \
		-Dversion=1.0.0-SNAPSHOT \
		-DtargetWebappGroupId=${groupId} \
		-DtargetWebappArtifactId=$(APP_NAME) \
		-DtargetWebappPath=/workspace/projects/${groupId} \
		-DinteractiveMode=false
	@echo "Copying ContextView files to webapp..."
	@if [ -d "/tmp/contextview-addon/src/main/java" ]; then \
		cp -r /tmp/contextview-addon/src/main/java/* src/main/java/; \
		echo "  ✓ Java files copied"; \
	fi
	@if [ -d "/tmp/contextview-addon/src/main/webapp" ]; then \
		cp -r /tmp/contextview-addon/src/main/webapp/* src/main/webapp/; \
		echo "  ✓ Webapp files copied"; \
	fi
	@rm -rf /tmp/contextview-addon
	@echo ""
	@echo "✓ ContextView addon integrated successfully!"
	@echo ""
	@echo "Next steps:"
	@echo "1. Add Jackson dependencies to pom.xml (see below)"
	@echo "2. Update src/main/webapp/WEB-INF/web.xml with contextviews.path"
	@echo "3. Run: make deploy"
	@echo ""
	@echo "Required dependencies for pom.xml:"
	@echo ""
	@echo "<dependency>"
	@echo "    <groupId>com.fasterxml.jackson.core</groupId>"
	@echo "    <artifactId>jackson-databind</artifactId>"
	@echo "    <version>2.15.2</version>"
	@echo "</dependency>"
	@echo "<dependency>"
	@echo "    <groupId>com.github.erosb</groupId>"
	@echo "    <artifactId>everit-json-schema</artifactId>"
	@echo "    <version>1.14.2</version>"
	@echo "</dependency>"
	@echo ""
	@echo "web.xml configuration:"
	@echo ""
	@echo "<context-param>"
	@echo "    <param-name>contextviews.path</param-name>"
	@echo "    <param-value>\$${symbol_dollar}{catalina.base}/webapps/$(APP_NAME)/WEB-INF/contextviews</param-value>"
	@echo "</context-param>"

# Update project configuration
update:
	@echo "Updating project configuration..."
	@if [ ! -f .env ]; then \
		echo "Error: .env file not found"; \
		exit 1; \
	fi
	@. ./.env && \
	if [ -z "$$DB_TYPE" ] || [ -z "$$DB_NAME" ] || [ -z "$$DB_USER" ]; then \
		echo "No database configuration found in .env, nothing to update"; \
		exit 0; \
	fi; \
	if [ -z "$$DB_PASSWORD" ]; then \
		echo "Error: DB_PASSWORD not found in .env"; \
		exit 1; \
	fi; \
	echo "Updating $$DB_TYPE database password for user: $$DB_USER"; \
	if [ "$$DB_TYPE" = "postgres" ]; then \
		POSTGRES_CONTAINER=$$(grep "^POSTGRES_CONTAINER_NAME=" /workspace/.env | cut -d= -f2); \
		POSTGRES_PASSWORD=$$(grep "^POSTGRES_PASSWORD=" /workspace/.env | cut -d= -f2); \
		if [ -z "$$POSTGRES_CONTAINER" ] || [ -z "$$POSTGRES_PASSWORD" ]; then \
			echo "Error: PostgreSQL configuration not found in /workspace/.env"; \
			exit 1; \
		fi; \
		echo "Connecting to PostgreSQL..."; \
		PGPASSWORD=$$POSTGRES_PASSWORD psql -h $$POSTGRES_CONTAINER -p 5432 -U postgres -d postgres -c "ALTER USER $$DB_USER WITH PASSWORD '$$DB_PASSWORD';" 2>/dev/null && \
		echo "✓ Password updated successfully" || \
		echo "✗ Failed to update password"; \
	elif [ "$$DB_TYPE" = "mariadb" ]; then \
		MARIADB_CONTAINER=$$(grep "^MARIADB_CONTAINER_NAME=" /workspace/.env | cut -d= -f2); \
		MARIADB_ROOT_PASSWORD=$$(grep "^MARIADB_ROOT_PASSWORD=" /workspace/.env | cut -d= -f2); \
		if [ -z "$$MARIADB_CONTAINER" ] || [ -z "$$MARIADB_ROOT_PASSWORD" ]; then \
			echo "Error: MariaDB configuration not found in /workspace/.env"; \
			exit 1; \
		fi; \
		echo "Connecting to MariaDB..."; \
		mysql -h $$MARIADB_CONTAINER -P 3306 -u root -p$$MARIADB_ROOT_PASSWORD -e "ALTER USER '$$DB_USER'@'%' IDENTIFIED BY '$$DB_PASSWORD';" 2>/dev/null && \
		echo "✓ Password updated successfully" || \
		echo "✗ Failed to update password"; \
	elif [ "$$DB_TYPE" = "sqlite" ]; then \
		echo "SQLite does not use passwords, nothing to update"; \
	else \
		echo "Error: Unknown database type: $$DB_TYPE"; \
		exit 1; \
	fi

# Display help
help:
	@echo "$(APP_NAME) Webapp - Available targets:"
	@echo ""
	@echo "  deploy        - Build and deploy to TomEE (full deployment) [DEFAULT]"
	@echo "  install       - First-time deployment (setup DB + deploy)"
	@echo "  setup-database - Create database from .env configuration"
	@echo "  build         - Build WAR file (dev profile with reloadable=true)"
	@echo "  release v=<version> - Create production release package (requires version)"
	@echo "  compile       - Compile source code"
	@echo "  quick-deploy  - [DISABLED] Use 'make deploy' instead"
	@echo "  test          - Run unit tests"
	@echo "  test-verbose  - Run tests with verbose output"
	@echo "  clean         - Clean build artifacts and remove from TomEE"
	@echo "  git-safe      - Add project to Git safe.directory"
	@echo "  push          - Git add, commit and push (requires m=\"message\")"
	@echo "  pull          - Git pull changes from remote repository"
	@echo "  dbcli         - Connect to application database"
	@echo "  dbcli f=<file.sql> - Execute SQL file in database"
	@echo "  dbcli f=<table_name.csv> - Load CSV into table (filename = table name)"
	@echo "  db            - Initialize database (executes database/${artifactId}.sql)"
	@echo "  update        - Update project configuration (e.g., database password)"
	@echo "  contextview   - Add ContextView functionality to this webapp"
	@echo ""
	@echo "  help          - Show this help message"
	@echo ""
	@echo "Access URL: http://localhost:9292/$(APP_NAME)"
	@echo ""
	@echo "For clean redeploy: make clean && make deploy"

.PHONY: build release compile setup-database install deploy quick-deploy test test-verbose clean git-safe push pull dbcli db update contextview help
