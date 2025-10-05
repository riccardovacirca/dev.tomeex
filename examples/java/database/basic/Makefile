.PHONY: clean all run setup test-db

# PostgreSQL JDBC driver configuration
JDBC_VERSION = 42.7.3
JDBC_JAR = postgresql-$(JDBC_VERSION).jar
JDBC_URL = https://jdbc.postgresql.org/download/$(JDBC_JAR)

# Java compiler and runtime settings
JAVAC = javac
JAVA = java
JAVA_FLAGS = -cp .:$(JDBC_JAR)
JAVAC_FLAGS = -cp $(JDBC_JAR)

# Load database connection parameters from .env file
ifneq (,$(wildcard ../../../.env))
	include ../../../.env
	DB_HOST = localhost
	DB_PORT = $(POSTGRES_PORT)
	DB_NAME = $(POSTGRES_DB)
	DB_USER = $(POSTGRES_USER)
	DB_PASS = $(POSTGRES_PASSWORD)
else
	# Fallback values if .env file not found
	DB_HOST = localhost
	DB_PORT = 5433
	DB_NAME = devdb
	DB_USER = devuser
	DB_PASS = devpass123
endif

# Collect all Example*.java files and their corresponding .class files
JAVA_FILES := $(basename $(wildcard Example*.java))
CLASS_FILES := $(addsuffix .class, $(JAVA_FILES))

# Download PostgreSQL JDBC driver if not present
setup:
	@if [ ! -f $(JDBC_JAR) ]; then \
		echo "Downloading PostgreSQL JDBC driver..."; \
		wget -q $(JDBC_URL) || curl -s -O $(JDBC_URL); \
		if [ ! -f $(JDBC_JAR) ]; then \
			echo "Failed to download JDBC driver"; \
			echo "Please download manually: $(JDBC_URL)"; \
			exit 1; \
		fi; \
	fi

# Test database connection using psql or Java fallback
test-db: setup
	@echo "Testing database connection..."
	@if command -v psql >/dev/null 2>&1; then \
		PGPASSWORD=$(DB_PASS) psql -h $(DB_HOST) -p $(DB_PORT) -U $(DB_USER) -d $(DB_NAME) -c "SELECT version();" 2>/dev/null || \
		echo "Direct psql connection failed - container may not be running"; \
	else \
		if [ -f Example01.class ]; then \
			$(JAVA) $(JAVA_FLAGS) Example01; \
		else \
			$(MAKE) Example01 && $(JAVA) $(JAVA_FLAGS) Example01; \
		fi; \
	fi

# Verify JDBC driver exists before compilation
check-driver:
	@if [ ! -f $(JDBC_JAR) ]; then \
		echo "PostgreSQL JDBC driver not found - run 'make setup'"; \
		exit 1; \
	fi

# Compile all Example*.java files after ensuring JDBC driver is available
all: setup $(CLASS_FILES)

# Rule to compile individual Example*.java files
Example%.class: Example%.java check-driver
	@echo "Compiling $<..."
	$(JAVAC) $(JAVAC_FLAGS) $<

# Convenience targets for individual examples 
$(JAVA_FILES): %: %.class

# Run specified examples - usage: make run Example01 Example02
run: check-driver
	@if [ -z "$(filter-out run,$(MAKECMDGOALS))" ]; then \
		echo "Error: Please specify an example to run"; \
		echo "Usage: make run <ExampleName>"; \
		exit 1; \
	fi
	@for target in $(filter-out run,$(MAKECMDGOALS)); do \
		if [ ! -f "$$target.class" ]; then \
			$(MAKE) $$target.class; \
		fi; \
		echo "Running $$target..."; \
		$(JAVA) $(JAVA_FLAGS) $$target; \
		echo ""; \
	done

# Alternative syntax: make run-Example01
run-%: %.class
	@echo "Running $*..."
	$(JAVA) $(JAVA_FLAGS) $*

# Remove all compiled .class files and JDBC driver
clean:
	rm -f *.class
	rm -f $(JDBC_JAR)

# Allow example names to be used as make targets without file conflicts
%:
	@: