# TomEEx

TomEE development environment for Java web applications with database support.

## Known Issues

### Hot Reload / Quick Deploy JNDI Problem

**Status:** DISABLED (as of 2025-01-11)

**Problem:** The quick-deploy feature (incremental rsync deployment) causes JNDI context corruption in TomEE after multiple hot reloads. This results in database connection failures with the error:

```
ClassCastException: JNDI lookup returned IvmContext instead of DataSource
Database connection failed after 3 attempts due to JNDI context corruption
```

**Symptoms:**
- First deployment works correctly
- First quick-deploy (hot reload) works correctly
- Second and subsequent quick-deploys fail with database connection errors

**Root Cause:**
TomEE's JNDI context becomes corrupted during hot reload when the webapp is reloaded multiple times without a full container restart. The JNDI lookup returns an `IvmContext` object instead of the expected `DataSource`.

**Current Workaround:**
The `make` command (default goal) has been changed from `quick-deploy` to `deploy` in all project Makefiles. Running `make quick-deploy` now displays a warning and automatically redirects to `make deploy`.

**Attempted Solutions:**
- Added retry logic with exponential backoff in `Database.java` (detects but doesn't solve the issue)
- Context refresh attempts (ineffective)

**Future Solutions to Explore:**
1. Disable `reloadable="true"` in `context.xml` (loses hot reload capability)
2. Implement JNDI context cleanup on webapp reload
3. Use alternative connection pooling (HikariCP, etc.)
4. Investigate TomEE-specific JNDI reset mechanisms

**Workaround:**
Always use `make deploy` for reliable deployment. For faster iteration during development, consider using `make clean && make deploy` instead of quick-deploy.

**References:**
- Test project: `projects/com.testqd/`
- Database class with retry logic: `projects/dev.tomeex.tools/src/main/java/dev/tomeex/tools/Database.java`
