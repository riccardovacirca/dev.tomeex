# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TomEEx is a Docker-based TomEE development environment for building Java web applications using Maven archetypes. It provides integrated database support (PostgreSQL, MariaDB, SQLite) and rapid application scaffolding through code generation.

**Key Architecture Principle**: Projects are organized by `groupId` (not `artifactId`) - this is critical when navigating directories.

**Technology Stack**:
- TomEE 9 (Jakarta EE 9+)
- Java 17
- Maven for build management
- Docker for containerization
- PostgreSQL/MariaDB/SQLite for database support

## Quick Start

```bash
# 1. Initial setup (creates .env)
./install.sh

# 2. Complete setup with database
./install.sh --postgres

# 3. Create a new webapp
make app id=com.example.myapp db=postgres

# 4. Navigate to project and develop
cd projects/com.example.myapp
# Edit Java code in src/main/java/
# Edit JSP/HTML in src/main/webapp/

# 5. Deploy changes
make

# 6. Access your webapp
# http://localhost:9292/myapp
```

## Quick Reference

**Most Common Tasks:**
```bash
# Create new webapp
make app id=com.example.myapp db=postgres

# Navigate to project
cd projects/com.example.myapp

# First-time deploy
make deploy

# Deploy changes
make

# Access database
make dbcli

# Run tests
make test

# Clean redeploy
make clean && make deploy
```

**Critical Paths:**
- Projects: `projects/{groupId}/` (e.g., `projects/com.example.myapp/`)
- TomEE webapps: `/usr/local/tomee/webapps/`
- Logs: `logs/catalina.out` or `docker logs tomeex`
- Container access: `docker exec -it tomeex bash`

**Code Navigation:**
When locating code, remember:
- Project directory = `groupId` (NOT artifactId)
- Example: `make app id=com.example.myapp` creates `projects/com.example.myapp/`
- Within project: `src/main/java/com/example/myapp/` for Java sources
- Database config: `projects/{groupId}/.env` and `META-INF/context-dev.xml`

## Core Commands

### Environment Setup
```bash
# Initialize TomEEx environment (first time)
./install.sh

# Install with specific database
./install.sh --postgres    # PostgreSQL (port 15432)
./install.sh --mariadb     # MariaDB (port 13306)
./install.sh --sqlite      # SQLite (file-based, no network port)

# Install Claude Code in container
./install.sh --claude

# Multiple databases can be installed
./install.sh --postgres --mariadb --sqlite
```

**First Run Process:**
1. First `./install.sh` creates `.env` file and exits
2. Edit `.env` if needed (optional Git configuration)
3. Run `./install.sh` again to complete setup
4. Optionally add database support with flags

**Environment Variables (.env):**
- `CONTAINER_NAME` - TomEE container name (default: tomeex)
- `HOST_PORT` - TomEE access port (default: 9292)
- `POSTGRES_PORT` - PostgreSQL external port (default: 15432)
- `MARIADB_PORT` - MariaDB external port (default: 13306)
- `GIT_USER`, `GIT_MAIL` - Git configuration (optional)

### Project Management
```bash
# Create new webapp (simple)
make app id=com.example.myapp

# Create webapp with database
make app id=com.example.myapp db=postgres

# Create library (JAR)
make lib id=com.example.mylib

# Create library with database support
make lib id=com.example.mylib db=true

# List all projects
make list

# Remove project
make remove id=com.example.myapp

# Rebuild all archetypes
make archetypes
```

### Git Operations
```bash
# Commit and push changes
make push m="commit message"

# Pull changes from remote
make pull
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
- `docs/tools/Database.md` - Database utilities (JNDI-based database abstraction with transaction support)
- `docs/tools/JSON.md` - JSON utilities
- `projects/dev.tomeex.tools/docs/File.md` - File manipulation utilities

The library is built and installed to Maven local repo via `make` commands in its directory.

**Important:** As of January 2025, TomEEx uses `dev.tomeex.tools.Database` as the standard database access layer. JDBI has been completely removed from all archetypes.

**Key Features of Database class**:
- JNDI datasource integration
- Transaction management (begin, commit, rollback)
- Prepared statement support
- Memory-efficient cursors for large result sets
- Multi-database support (PostgreSQL, MariaDB, SQLite)

**CRITICAL - Before Adding Java Dependencies:**
- **ALWAYS check `dev.tomeex.tools` library FIRST** before importing external Java libraries
- The tools library (`projects/dev.tomeex.tools/`) provides common utilities for:
  - Database operations (JNDI-based abstraction)
  - JSON parsing and manipulation
  - File operations
- Check documentation: `docs/tools/Database.md`, `docs/tools/JSON.md`, `projects/dev.tomeex.tools/docs/File.md`
- Only add external dependencies if the required functionality is NOT available in tools.jar
- This reduces dependency bloat and maintains consistency across projects

### Example Projects in Repository

**dev.tomeex.mpi** (`projects/dev.tomeex.mpi/`)
- Master Patient Index (MPI) implementation
- RESTful API with PostgreSQL database support
- Complex database schema with stored procedures (`database/v6/`)
- Example of production-ready webapp structure
- Includes database migration scripts and dashboard utilities

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
make                      # Deploy WAR to TomEE (full build + deploy) [DEFAULT]
make build                # Build WAR (dev profile, reloadable)
make release              # Build production release package (tar.gz)
make deploy               # Full build + deploy WAR to TomEE
make install              # First-time deployment (setup DB + deploy) - for cloned projects
make clean                # Clean build artifacts + remove from TomEE
make test                 # Run unit tests
make dbcli                # Connect to application database
make dbcli f=file.sql     # Execute SQL file in database
make dbcli f=table.csv    # Load CSV file into table (filename must match table name)
make update               # Update database password from .env
make contextview          # Add ContextView functionality to webapp
make push m="message"     # Git add, commit, and push changes
make help                 # Show all available targets
```

**Important Notes:**
- **First deployment:** Always use `make deploy` to create exploded WAR directory
- **Incremental changes:** Use `make` for deployment (defaults to full deploy due to JNDI hot reload issues)
- **Clean redeploy:** `make clean && make deploy`
- **Known Issue:** Quick-deploy has been DISABLED due to JNDI context corruption after multiple hot reloads (see README.md Known Issues)
- **Cloned projects:** Use `make install` to set up database and deploy when cloning from a remote repository

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

**Requirements for JAR installation:**
- JAR must contain `META-INF/maven/{groupId}/{artifactId}/pom.properties`
- Properties file must define: `groupId`, `artifactId`, `version`
- Optional companion files: `{artifactId}-sources.jar`, `{artifactId}-javadoc.jar`

**Example workflow:**
```bash
# Place JAR in lib directory
cp custom-library-1.0.0.jar /workspace/lib/

# Run install to add to Maven local repo
./install.sh

# Use in pom.xml
<dependency>
    <groupId>com.custom</groupId>
    <artifactId>custom-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

# Check container status
docker ps | grep tomeex
```

**IMPORTANT - Claude Code Sessions:**
- **NEVER restart the container** (`docker restart tomeex`) during a Claude Code session
- Claude Code runs inside the container, and restarting it will terminate the active session
- All session context and conversation history will be lost
- If container restart is absolutely necessary, inform the user first and let them decide
- **We are already INSIDE the container** - no need to use `docker exec` to access it

**Testing and Development Inside Container:**
- For testing webapps with `curl`, use `localhost:8080` (internal TomEE port)
- External access (from host) is via `localhost:9292` (mapped from container port 8080)
- Example: `curl http://localhost:8080/myapp/api/endpoint`

**Key Paths in Container:**
- `/workspace` - Mounted project directory (bidirectional sync with host)
- `/usr/local/tomee/webapps` - Deployed webapps (volume-mounted from `webapps/`)
- `/usr/local/tomee/conf` - TomEE configuration (volume-mounted from `conf/`)
- `/usr/local/tomee/logs` - Server logs (volume-mounted from `logs/`)
- `~/.m2/repository` - Maven local repository

**Docker Network:**
- Network name: `tomeex-net` (from .env)
- All containers (TomEE, PostgreSQL, MariaDB) are on this network
- Containers can communicate using container names as hostnames
- Example: TomEE connects to PostgreSQL at `tomeex-postgres:5432`

## URLs and Access

- **TomEE Manager:** http://localhost:9292/manager/html
- **Host Manager:** http://localhost:9292/host-manager/html
- **Deployed Apps:** http://localhost:9292/{artifactId}

Default credentials from `.env`:
- Admin: `admin / secret`
- Manager: `manager / secret`

## Key Implementation Patterns

### Project Naming Convention (CRITICAL)
- **Directory:** `projects/{groupId}/` (e.g., `projects/com.example.myapp/`)
- **artifactId:** Extracted from last part of groupId (e.g., `myapp`)
- **groupId:** Full id parameter (e.g., `com.example.myapp`)
- **WAR/JAR filename:** Uses artifactId (e.g., `myapp.war`)
- **Context path:** Uses artifactId (e.g., `/myapp`)

**Example:**
- Command: `make app id=com.example.myapp`
- Creates: `projects/com.example.myapp/`
- groupId: `com.example.myapp`
- artifactId: `myapp` (auto-extracted)
- Deployed as: `http://localhost:9292/myapp`

### Deployment Strategy

**Note:** Quick-deploy (incremental rsync) is currently DISABLED due to JNDI context corruption issues after multiple hot reloads. See README.md for details.

**Standard Deploy (make or make deploy):**
1. Builds complete WAR file with Maven
2. Removes old deployment from TomEE webapps
3. Copies new WAR to webapps/ directory
4. TomEE automatically extracts and deploys the WAR
5. Use for all deployments until hot reload issues are resolved

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
make app id=com.mycompany.myapi db=postgres

# 2. Navigate to project
cd projects/com.mycompany.myapi

# 3. Develop your application
# Edit Java sources in src/main/java
# Edit webapp resources in src/main/webapp

# 4. Deploy during development
make

# 5. Run tests
make test

# 6. Access database
make dbcli

# 7. Clean redeploy if needed
make clean && make deploy
```

### Working with Cloned Projects

When cloning an existing project from a remote Git repository:

```bash
# 1. Clone the TomEEx environment
git clone <repository-url>
cd tomeex

# 2. Initialize environment
./install.sh --postgres  # or --mariadb, --sqlite

# 3. Navigate to cloned project
cd projects/{groupId}

# 4. First-time setup (creates database + deploys)
make install

# 5. Subsequent deployments
make
```

**Note:** The `make install` target:
- Reads database configuration from project's `.env` file
- Creates database and user automatically
- Deploys the webapp to TomEE
- Only needed once after cloning

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

### Common Issues

#### Container not starting
```bash
# Check if Docker is running
docker info

# Check for port conflicts
lsof -i :9292  # or netstat -tulpn | grep 9292

# Check container logs
docker logs tomeex

# Restart container
docker restart tomeex
```

#### "Database already exists" error
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

#### WAR not deploying
- Check TomEE logs: `docker logs tomeex` or `tail -f logs/catalina.out`
- Verify WAR was copied: `ls -lh webapps/`
- Ensure no port conflicts: `docker ps`
- Check container is running: `docker ps | grep tomeex`
- Look for compilation errors in project: `cd projects/{groupId} && mvn clean package`

#### Database connection failures
```bash
# Check database container is running
docker ps | grep postgres  # or mariadb

# Check network connectivity
docker exec -it tomeex ping tomeex-postgres

# Verify JNDI resource in META-INF/context.xml
# Ensure DB_HOST in project .env matches container name
```

#### Maven build failures
```bash
# Inside container, ensure JAVA_HOME is set
docker exec -it tomeex bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn clean package

# Clear Maven cache if needed
rm -rf ~/.m2/repository

# Reinstall dev.tomeex.tools if needed
cd /workspace/projects/dev.tomeex.tools && make
```

### Hot Reload / JNDI Issues
**Symptom:** Database connection failures after multiple deployments with error: `ClassCastException: JNDI lookup returned IvmContext instead of DataSource`

**Cause:** TomEE's JNDI context becomes corrupted during hot reload when webapps are reloaded multiple times without a full container restart. Quick-deploy has been DISABLED as a workaround.

**Current Status:** As of 2025-01-11, quick-deploy is disabled. All deployments use full build + deploy cycle.

**Future Solutions:** May require disabling `reloadable="true"`, implementing JNDI context cleanup, or using alternative connection pooling.

**Reference:** See README.md "Known Issues" section and test project at `projects/com.testqd/`

### Java version issues
Always set JAVA_HOME in container:
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

This is already configured in project Makefiles.

## License

Projects inherit PolyForm Noncommercial License 1.0.0 from TomEEx. Generated projects include LICENSE.md which should be updated if using a different license.
