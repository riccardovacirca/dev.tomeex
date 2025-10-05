.PHONY: clean all run

# Java compiler and runtime settings
JAVAC = javac
JAVA = java
JAVAC_FLAGS = 
JAVA_FLAGS = 

# Collect all Example*.java files and their corresponding .class files
JAVA_FILES := $(basename $(wildcard Example*.java))
CLASS_FILES := $(addsuffix .class, $(JAVA_FILES))

# Compile all examples
all: $(CLASS_FILES)

# Rule to compile individual Example*.java files
Example%.class: Example%.java
	@echo "Compiling $<..."
	$(JAVAC) $(JAVAC_FLAGS) $<

# Convenience targets for individual examples
$(JAVA_FILES): %: %.class

# Run specified examples - usage: make run Example01 Example02
run:
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
		echo "========================================"; \
		$(JAVA) $(JAVA_FLAGS) $$target; \
		echo "========================================"; \
		echo ""; \
	done

# Alternative syntax: make run-Example01
run-%: %.class
	@echo "Running $*..."
	@echo "========================================"
	$(JAVA) $(JAVA_FLAGS) $*
	@echo "========================================"

# Remove all compiled .class files
clean:
	rm -f *.class

# Allow example names to be used as make targets without file conflicts
%:
	@: