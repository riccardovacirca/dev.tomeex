# ${artifactId}

RESTful API with database support

## Commands

```bash
make                      # Quick incremental deploy (compile + rsync) [DEFAULT]
make compile              # Compile source code only
make build                # Build WAR (dev profile, reloadable)
make release              # Build WAR (prod profile, non-reloadable)
make deploy               # Full build + deploy WAR to TomEE
make test                 # Run unit tests
make test-verbose         # Run tests with detailed output
make clean                # Clean build artifacts + remove from TomEE
make dbcli                # Connect to application database
make dbcli load=file.sql  # Execute SQL file in database
make help                 # Show all available targets
```

**Clean redeploy:** `make clean && make deploy`

## Database

**Configuration:**
- Edit `src/main/webapp/META-INF/context-dev.xml` for development database
- Edit `src/main/webapp/META-INF/context-prod.xml` for production database
- Connection available via JNDI: `java:comp/env/jdbc/MainDB`

**Connect to database:**
```bash
make dbcli                    # Interactive SQL shell
make dbcli load=schema.sql    # Execute SQL file
```

## Access

- **Application:** http://localhost:9292/${artifactId}
- **API Endpoint:** http://localhost:9292/${artifactId}/api/items
- **TomEE Manager:** http://localhost:9292/manager/html

## Quick Deploy vs Full Deploy

- `make` (quick-deploy): Fast incremental sync - compiles and syncs classes to exploded WAR
- `make deploy`: Full WAR build and deployment - use for first deployment or major changes

## License

PolyForm Noncommercial License 1.0.0 - See LICENSE.md

**To change license:**
1. Edit `LICENSE.md` with your license text
2. Update `pom.xml` section:
   ```xml
   <licenses>
       <license>
           <name>Your License Name</name>
           <url>https://your-license-url</url>
           <distribution>repo</distribution>
           <comments>Your license description</comments>
       </license>
   </licenses>
   ```
3. Rebuild: `make clean && make deploy`
4. License will be included in WAR at `META-INF/LICENSE.md`
