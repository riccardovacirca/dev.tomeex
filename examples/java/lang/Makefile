# Java Examples Makefile
# Manages compilation and execution of Java example files
#
# Usage:
#   make <ExampleName>     - Compile specific example (e.g., make Example01)
#   make run <ExampleName> - Run specific example (e.g., make run Example01)
#   make clean             - Remove all compiled .class files
#   make list              - List all available examples
#   make all               - Compile all examples
#   make help              - Show this help

.PHONY: help clean list all run

# Java compiler settings
JAVAC = javac
JAVA = java
JAVA_FLAGS = -cp .

# Get all Java files (without .java extension)
JAVA_FILES := $(basename $(wildcard Example*.java))
CLASS_FILES := $(addsuffix .class, $(JAVA_FILES))

# Default target
help:
	@echo "Java Examples Makefile"
	@echo "======================"
	@echo ""
	@echo "Usage:"
	@echo "  make <ExampleName>     - Compile specific example (e.g., make Example01)"
	@echo "  make run <ExampleName> - Run specific example (e.g., make run Example01)"
	@echo "  make clean             - Remove all compiled .class files"
	@echo "  make list              - List all available examples"
	@echo "  make all               - Compile all examples"
	@echo "  make help              - Show this help"
	@echo ""
	@echo "Available examples:"
	@for example in $(sort $(JAVA_FILES)); do \
		echo "  $$example"; \
	done

# List all available examples
list:
	@echo "Available Java examples:"
	@for example in $(sort $(JAVA_FILES)); do \
		echo "  $$example"; \
	done

# Compile all examples
all: $(CLASS_FILES)

# Generic rule to compile any Example*.java file
Example%.class: Example%.java
	@echo "Compiling $<..."
	$(JAVAC) $<
	@echo "$@ compiled successfully"

# Individual compilation targets
$(JAVA_FILES): %: %.class
	@echo "$@ is ready to run"

# Run target - requires example name as parameter
run:
	@if [ -z "$(filter-out run,$(MAKECMDGOALS))" ]; then \
		echo "Error: Please specify an example to run"; \
		echo "Usage: make run <ExampleName>"; \
		echo "Example: make run Example01"; \
		exit 1; \
	fi
	@for target in $(filter-out run,$(MAKECMDGOALS)); do \
		if [ ! -f "$$target.class" ]; then \
			echo "Compiling $$target first..."; \
			$(MAKE) $$target.class; \
		fi; \
		echo "Running $$target..."; \
		echo "----------------------------------------"; \
		$(JAVA) $(JAVA_FLAGS) $$target; \
		echo "----------------------------------------"; \
	done

# Make run targets not fail when used as arguments
$(JAVA_FILES:%=run-%): run-%: %.class
	@echo "Running $*..."
	@echo "----------------------------------------"
	$(JAVA) $(JAVA_FLAGS) $*
	@echo "----------------------------------------"

# Alternative run syntax: make run-Example01
run-%: %.class
	@echo "Running $*..."
	@echo "----------------------------------------"
	$(JAVA) $(JAVA_FLAGS) $*
	@echo "----------------------------------------"

# Clean compiled files
clean:
	@echo "Removing compiled .class files..."
	rm -f *.class
	@echo "Clean complete"

# Debug target to show variables
debug:
	@echo "JAVA_FILES: $(JAVA_FILES)"
	@echo "CLASS_FILES: $(CLASS_FILES)"

# Prevent make from treating example names as file targets when used with 'run'
%:
	@: