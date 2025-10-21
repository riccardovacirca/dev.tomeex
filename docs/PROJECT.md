# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

TomEEx is a Docker-based TomEE development environment for building Java web applications using Maven archetypes. It provides integrated database support (PostgreSQL, MariaDB, SQLite) and rapid application scaffolding through code generation.

**Critical Architecture Principle**: Projects are organized by `groupId` (NOT `artifactId`). This is the most common source of navigation errors.

**Technology Stack**:
- TomEE 9 (Jakarta EE 9+) / Java 17
- Maven archetypes for code generation
- Docker containers for TomEE and databases
- JNDI datasources for database access (standard pattern)

## Running Inside Container

**CRITICAL**: We are already running INSIDE the `tomeex` Docker container. Never use `docker exec -it tomeex bash` - we're already there.

- **NEVER restart the container** during a Claude Code session - this will terminate the session and lose all context
- For testing webapps with `curl`, use `localhost:8080` (internal TomEE port), NOT `localhost:9292` (external host port)
- Container paths: `/workspace` (project root), `/usr/local/tomee` (TomEE installation), `~/.m2/repository` (Maven local repo)

## Essential Commands

### Environment Setup
```bash
# First run (creates .env and exits)
./install.sh

# Complete setup with database
./install.sh --postgres    # PostgreSQL (port 15432)
./install.sh --mariadb     # MariaDB (port 13306)
./install.sh --sqlite      # SQLite (file-based)
```

### Project Management (Root Level)
```bash
# Create webapp with database
make app id=com.example.myapp db=postgres

# Create library (JAR)
make lib id=com.example.mylib db=true

# List all projects
make list

# Remove project
make remove id=com.example.myapp

# Rebuild archetypes
make archetypes
```

### Project Development (Within Project Directory)
```bash
cd projects/{groupId}    # Navigate to project

make                     # Build and deploy (default)
make deploy              # Full build + deploy
make install             # First-time setup (creates DB + deploys) - for cloned projects
make clean               # Clean build artifacts + remove from TomEE
make test                # Run unit tests
make dbcli               # Connect to database
make dbcli f=file.sql    # Execute SQL file
make contextview         # Add ContextView add-on to webapp
```

## Critical Naming Convention

**This is the #1 source of errors when navigating the codebase:**

- **Command**: `make app id=com.example.myapp`
- **Creates directory**: `projects/com.example.myapp/` (uses full groupId)
- **groupId**: `com.example.myapp` (full id)
- **artifactId**: `myapp` (auto-extracted from last part)
- **WAR filename**: `myapp.war`
- **Context path**: `/myapp`
- **URL**: `http://localhost:9292/myapp`

**When navigating**: Always use `projects/{groupId}/`, NOT `projects/{artifactId}/`

## Directory Structure

```
/workspace/
├── install.sh              # Main setup script
├── Makefile                # Top-level commands
├── .env                    # Configuration (created on first run)
├── archetypes/             # Maven archetypes (5 total)
│   ├── tomeex-app/
│   ├── tomeex-app-database/
│   ├── tomeex-lib/
│   ├── tomeex-lib-database/
│   └── tomeex-addon-contextview/
├── projects/{groupId}/     # Generated projects (organized by groupId!)
│   ├── pom.xml
│   ├── Makefile
│   ├── .env                # Database config (if applicable)
│   └── src/
├── webapps/                # Deployed WAR files (TomEE auto-extracts)
├── conf/                   # TomEE configuration
└── lib/                    # JARs to install to Maven local repo
```

## Database Architecture

All database-enabled projects use **JNDI datasource** as the standard pattern:

- **JNDI name**: `java:comp/env/jdbc/MainDB`
- **Access**: Standard JDBC API (`Connection`, `PreparedStatement`, `ResultSet`)
- **Configuration**: `META-INF/context-dev.xml` (dev), `META-INF/context-prod.xml` (prod)
- **Credentials**: Project's `.env` file
- **Build profiles**: `-P dev` (reloadable=true) or `-P prod` (reloadable=false)

**Pattern**: Prefer JDBC over external ORMs to reduce dependency bloat. Use prepared statements and explicit transaction management.

## Maven Archetypes

All archetypes have:
- **groupId**: `dev.tomeex.archetypes`
- **version**: `1.0.0`

Available archetypes:
1. `tomeex-app` - Webapp without database
2. `tomeex-app-database` - Webapp with JNDI datasource
3. `tomeex-lib` - JAR library
4. `tomeex-lib-database` - JAR library with multi-database support
5. `tomeex-addon-contextview` - JSON-driven UI framework (add-on only)

**Archetype development**: Edit in `archetypes/{name}/`, rebuild with `make archetypes`, test by generating a new project. Uses Velocity templates with variables like `${groupId}`, `${artifactId}`.

## ContextView Add-on

The `tomeex-addon-contextview` archetype is a JSON-driven scenario architecture (see `archetypes/tomeex-addon-contextview/AI_AGENT_MEMORY.md`):

- **Single Module Model**: One webapp = one `main.json` configuration
- **Schema Inlining**: External JSON schemas automatically merged
- **Role-Based Auth**: Wildcards `*` (any), `!` (guest), `@` (unvalidated)
- **Configuration**: `WEB-INF/contextviews/json/main.json` (always this name)
- **API Endpoints**:
  - `GET /api/contextview/config?role=admin`
  - `GET /api/contextview/list`
  - `GET /api/contextview/{scenarioKey}`

**Integration**: `cd projects/{groupId} && make contextview` (follow prompts to merge dependencies and web.xml)

## Library Installation (`/workspace/lib/`)

JAR files in `lib/` are auto-installed to Maven local repo when running `./install.sh`:
- Must contain `META-INF/maven/{groupId}/{artifactId}/pom.properties` with `groupId`, `artifactId`, `version`
- Optional: `{artifactId}-sources.jar`, `{artifactId}-javadoc.jar`
- After placing JAR, run `./install.sh` to install

## Deployment Strategy

**KNOWN ISSUE**: Quick-deploy (incremental rsync) is **DISABLED** due to JNDI context corruption after multiple hot reloads.

**Current behavior**: `make` defaults to full `deploy` (not `quick-deploy`)

**Standard deploy flow**:
1. Builds complete WAR with Maven
2. Removes old deployment from TomEE webapps
3. Copies new WAR to webapps/
4. TomEE auto-extracts and deploys

**First deployment**: Always use `make deploy` to create exploded WAR directory

## Working with Cloned Projects

When cloning from remote repository:
```bash
git clone <repository-url>
cd tomeex
./install.sh --postgres        # Initialize environment
cd projects/{groupId}
make install                   # First-time setup (creates DB + deploys)
make                           # Subsequent deployments
```

## Key Files to Check

When working with projects:
- `pom.xml` - Maven config, dependencies, build profiles
- `Makefile` - Build and deployment commands
- `.env` - Database configuration (project-level)
- `src/main/webapp/WEB-INF/web.xml` - Servlet configuration
- `src/main/webapp/META-INF/context-dev.xml` - JNDI datasource (dev)
- `src/main/resources/META-INF/context.xml` - Packaged with WAR

## URLs and Access

- **TomEE Manager**: http://localhost:9292/manager/html
- **Host Manager**: http://localhost:9292/host-manager/html
- **Deployed Apps**: http://localhost:9292/{artifactId}
- **Default credentials** (from `.env`): `admin/secret`, `manager/secret`

## Docker Network

- **Network name**: `tomeex-net`
- **Containers**: TomEE, PostgreSQL (`tomeex-postgres:5432`), MariaDB (`tomeex-mariadb:3306`)
- **Internal communication**: Use container names as hostnames
- **TomEE ports**: Internal `8080`, external `9292` (mapped)

## Common Troubleshooting

### "Database already exists" error
Database was created but project generation failed. Remove manually:
```bash
# PostgreSQL
psql -h tomeex-postgres -p 5432 -U postgres
DROP DATABASE myapp; DROP USER myapp;

# MariaDB
mysql -h tomeex-mariadb -P 3306 -u root -p
DROP DATABASE myapp; DROP USER 'myapp'@'%';
```

### WAR not deploying
- Check logs: `tail -f logs/catalina.out` or `docker logs tomeex`
- Verify WAR exists: `ls -lh webapps/`
- Check for Maven build errors: `cd projects/{groupId} && mvn clean package`

### Database connection failures
- Check container running: `docker ps | grep postgres`
- Verify JNDI resource in `META-INF/context.xml`
- Ensure `DB_HOST` in `.env` matches container name

### Maven build failures
```bash
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn clean package

# If needed, clear cache
rm -rf ~/.m2/repository
cd /workspace && make archetypes
```

### Hot Reload / JNDI Issues
**Symptom**: `ClassCastException: JNDI lookup returned IvmContext instead of DataSource`

**Cause**: TomEE's JNDI context corruption after multiple hot reloads without container restart.

**Status**: As of 2025-01-11, quick-deploy is disabled. All deployments use full build + deploy cycle.

**Reference**: See README.md "Known Issues" section and test project at `projects/com.testqd/`

## Documentation

- `docs/PROJECT.md` - Source for CLAUDE.md (synchronized)
- `docs/TOMEEX.md` - TomEE environment overview
- `docs/SERVLET.md` - Jakarta Servlet development guide
- `archetypes/tomeex-addon-contextview/AI_AGENT_MEMORY.md` - ContextView architecture details

## License

Projects inherit PolyForm Noncommercial License 1.0.0 from TomEEx. Generated projects include LICENSE.md which should be updated if using a different license.
