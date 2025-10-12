# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TomEEx is a Docker-based TomEE development environment for building Java web applications using Maven archetypes. It provides integrated database support (PostgreSQL, MariaDB, SQLite) and rapid application scaffolding through code generation.

**Key Architecture Principle**: Projects are organized by `groupId` (not `artifactId`) - this is critical when navigating directories.

## Quick Reference

**Most Common Tasks:**
```bash
# Create new webapp
make app name=myapp id=com.example db=postgres

# Navigate to project (use groupId!)
cd projects/com.example

# First-time deploy
make deploy

# Incremental changes (fast)
make

# Access database
make dbcli

# Run tests
make test

# Clean redeploy
make clean && make deploy
```

**Critical Paths:**
- Projects: `projects/{groupId}/` (NOT `projects/{artifactId}/`)
- TomEE webapps: `/usr/local/tomee/webapps/`
- Logs: `logs/catalina.out` or `docker logs tomeex`
- Container access: `docker exec -it tomeex bash`

## Core Commands

### Environment Setup
```bash
# Initialize TomEEx environment (first time)
./install.sh

# Install with specific database
./install.sh --postgres    # PostgreSQL
./install.sh --mariadb     # MariaDB
./install.sh --sqlite      # SQLite

# Install Claude Code in container
./install.sh --claude
```

### Project Management
```bash
# Create new webapp (simple)
make app name=myapp id=com.example

# Create webapp with database
make app name=myapp id=com.example db=postgres

# Create library (JAR)
make lib name=mylib id=com.example.lib

# Create library with database support
make lib name=mylib id=com.example.lib db=true

# List all projects
make list

# Remove project
make remove id=com.example

# Rebuild all archetypes
make archetypes
```

### Git Operations
```bash
# Commit and push changes
make push m="commit message"
```

## Architecture

### Directory Structure

**Root Level:**
- `install.sh` - Main setup script for Docker environment
- `Makefile` - Top-level project management commands
- `.env` - Configuration file (created on first install)
- `archetypes/` - Maven archetypes for code generation
- `projects/` - Generated projects (organized by groupId)
- `webapps/` - Deployed WAR files
- `conf/` - TomEE configuration
- `lib/` - JAR libraries to install to Maven local repo

**Generated Projects:**
- Located in `projects/{groupId}/` (NOT `projects/{artifactId}/`)
- Each project has its own `Makefile` and `README.md`
- Projects are self-contained with build and deployment scripts

### Maven Archetypes

The repository contains 5 main archetypes:

1. **tomeex-app** - Simple webapp without database
2. **tomeex-app-database** - Webapp with JNDI datasource configuration
3. **tomeex-lib** - Simple JAR library
4. **tomeex-lib-database** - JAR library with multi-database support
5. **tomeex-addon-contextview** - JSON-driven UI framework add-on

All archetypes use:
- **groupId:** `dev.tomeex.archetypes`
- **version:** `1.0.0`

### TomEEx Tools Library

**Location:** `projects/dev.tomeex.tools/`

This is a JAR library providing utility classes for TomEEx projects. See documentation:
- `projects/dev.tomeex.tools/docs/Database.md` - Database utilities
- `projects/dev.tomeex.tools/docs/File.md` - File manipulation utilities

The library is built and installed to Maven local repo via `make` commands in its directory.

### ContextView Add-on Architecture

The `tomeex-addon-contextview` archetype implements a JSON-driven scenario architecture pattern (see `archetypes/tomeex-addon-contextview/AI_AGENT_MEMORY.md` for complete details):

**Key Concepts:**
- **Single Module Model**: One webapp = one configuration file (`main.json`)
- **Schema Inlining**: External schema files automatically merged into scenarios
- **Role-Based Authorization**: Wildcards `*` (any), `!` (guest), `@` (unvalidated)
- **Breadcrumb Generation**: Automatic hierarchical navigation
- **Add-on Integration**: Not a standalone webapp, but added to existing projects

**Configuration Structure:**
```
WEB-INF/contextviews/
├── json/
│   ├── main.json           # Master configuration (always this name)
│   └── forms/
│       ├── SCHEMA_NAME.json
│       └── ...
```

**API Endpoints:**
- `GET /api/contextview/config?role=admin` - Full processed configuration
- `GET /api/contextview/list` - List all scenarios
- `GET /api/contextview/{scenarioKey}` - Specific scenario

**Integration with Existing Webapp:**
```bash
cd projects/com.example
make contextview
# Follow on-screen instructions to merge dependencies and web.xml
```

## Working with Projects

### Project Makefile Targets

Each generated project includes these common targets:

```bash
make                      # Quick incremental deploy (compile + rsync) [DEFAULT]
make build                # Build WAR (dev profile, reloadable)
make release              # Build production release package (tar.gz)
make deploy               # Full build + deploy WAR to TomEE
make install              # First-time deployment (setup DB + deploy)
make clean                # Clean build artifacts + remove from TomEE
make test                 # Run unit tests
make dbcli                # Connect to application database
make dbcli load=file.sql  # Execute SQL file in database
make contextview          # Add ContextView functionality to webapp
make help                 # Show all available targets
```

**Important Notes:**
- **First deployment:** Always use `make deploy` to create exploded WAR directory
- **Incremental changes:** Use `make` (quick-deploy) for fast hot-reload (compiles + rsync)
- **Clean redeploy:** `make clean && make deploy`
- **Known Issue:** Quick-deploy requires the exploded WAR to exist; if it fails, run `make deploy` first

### Database Configuration

Projects with database support have:
- `.env` file with database connection details
- `META-INF/context-dev.xml` for development (JNDI datasource)
- `META-INF/context-prod.xml` for production
- JNDI resource name: `java:comp/env/jdbc/MainDB`

**Build Profiles:**
- Development: `mvn package -P dev` (uses context-dev.xml, reloadable=true)
- Production: `mvn package -P prod` (uses context-prod.xml, reloadable=false)

### Library Installation from `/workspace/lib`

JAR files placed in `/workspace/lib/` are automatically installed to Maven local repository when running `./install.sh`. The script:
1. Extracts Maven coordinates from JAR's `META-INF/maven/*/pom.properties`
2. Installs main JAR, sources JAR, and javadoc JAR (if present)
3. Makes them available as Maven dependencies

## Container Access

The TomEE container runs as `tomeex` (or name from `.env`):

```bash
# Access container shell
docker exec -it tomeex bash

# Inside container
cd /workspace              # Project root
cd /usr/local/tomee        # TomEE installation
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# View logs
docker logs tomeex
tail -f logs/catalina.out
```

**Key Paths in Container:**
- `/workspace` - Mounted project directory
- `/usr/local/tomee/webapps` - Deployed webapps
- `/usr/local/tomee/conf` - TomEE configuration
- `/usr/local/tomee/logs` - Server logs

## URLs and Access

- **TomEE Manager:** http://localhost:9292/manager/html
- **Host Manager:** http://localhost:9292/host-manager/html
- **Deployed Apps:** http://localhost:9292/{artifactId}

Default credentials from `.env`:
- Admin: `admin / secret`
- Manager: `manager / secret`

## Key Implementation Patterns

### Project Naming Convention (CRITICAL)
- **Directory:** `projects/{groupId}/` (e.g., `projects/com.example/`)
- **artifactId:** Used for WAR filename and context path
- **groupId:** Used for directory organization and Java package

**Always use `groupId` for directory paths, not `artifactId`**. Example: webapp with `artifactId=myapp` and `groupId=com.example` lives in `projects/com.example/`.

### Hot Reload Strategy

**Quick Deploy (make):**
1. Compiles Java sources to `target/classes/`
2. Uses `rsync` to sync classes to exploded WAR at `/usr/local/tomee/webapps/{app}/`
3. Touches `web.xml` to trigger TomEE hot reload
4. Fast iteration - seconds not minutes

**Full Deploy (make deploy):**
1. Builds complete WAR file
2. Removes old deployment
3. Copies new WAR to webapps/
4. TomEE extracts and deploys
5. Use for major changes or first deployment

### Database Setup Flow

For database-enabled projects:
1. `install.sh --create-webapp` generates project with database archetype
2. Creates database and user automatically
3. Generates `.env` file with connection details
4. `context-dev.xml` configured with JNDI datasource
5. Application accesses via `java:comp/env/jdbc/MainDB`

## Development Workflow

### Creating a New Webapp

```bash
# 1. Create webapp with database
make app name=myapi id=com.mycompany db=postgres

# 2. Navigate to project
cd projects/com.mycompany

# 3. Develop your application
# Edit Java sources in src/main/java
# Edit webapp resources in src/main/webapp

# 4. Quick deploy during development
make

# 5. Run tests
make test

# 6. Access database
make dbcli

# 7. Clean redeploy if needed
make clean && make deploy
```

### Adding ContextView to Existing Webapp

```bash
cd projects/com.mycompany
make contextview
# Follow instructions to add dependencies and web.xml configuration
make deploy
```

### Creating Production Release

```bash
cd projects/com.mycompany
make release
# Creates myapi-release.tar.gz with WAR, LICENSE, and install.sh
```

## Important Files to Check

When working with projects:
- `pom.xml` - Maven configuration, dependencies, build profiles
- `Makefile` - Build and deployment commands
- `.env` - Database configuration (if present)
- `src/main/webapp/WEB-INF/web.xml` - Servlet configuration
- `src/main/webapp/META-INF/context-dev.xml` - JNDI datasource (dev)
- `src/main/resources/META-INF/context.xml` - Packaged with WAR

## Archetype Development

When modifying archetypes:

1. Edit archetype sources in `archetypes/{archetype-name}/`
2. Rebuild: `make archetypes`
3. Test by generating a new project
4. Archetypes use Velocity templates with variables like `${groupId}`, `${artifactId}`

**Important:** The generated project directory is named by `groupId`, not `artifactId`. This is by design.

## Troubleshooting

### "Database already exists" error
If you see user/database exists errors when creating webapps, the database may have been created but project generation failed. Remove manually:
```bash
# PostgreSQL
psql -h tomeex-postgres -p 5432 -U postgres
DROP DATABASE myapp;
DROP USER myapp;

# MariaDB
mysql -h tomeex-mariadb -P 3306 -u root -p
DROP DATABASE myapp;
DROP USER 'myapp'@'%';
```

### WAR not deploying
- Check TomEE logs: `docker logs tomeex` or `tail -f logs/catalina.out`
- Verify WAR was copied: `ls -lh webapps/`
- Ensure no port conflicts: `docker ps`
- Check container is running: `docker ps | grep tomeex`

### Quick deploy fails
**Symptom:** `Error: WAR not yet extracted. Run 'make deploy' first`

**Cause:** Quick-deploy uses rsync to sync compiled classes to an exploded WAR directory at `/usr/local/tomee/webapps/{app}/`. This directory only exists after TomEE extracts the WAR file.

**Solution:** Run `make deploy` first to create the exploded WAR, then use `make` for subsequent changes.

### Java version issues
Always set JAVA_HOME in container:
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

This is already configured in project Makefiles.

## License

Projects inherit PolyForm Noncommercial License 1.0.0 from TomEEx. Generated projects include LICENSE.md which should be updated if using a different license.
