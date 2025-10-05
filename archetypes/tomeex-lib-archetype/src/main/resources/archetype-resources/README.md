# ${artifactId}

Reusable JAR library for ${artifactId}

## Commands

**Development:**
```bash
make              # Compile + build JAR + install to Maven repo
make build        # Compile + build JAR to target/
make compile      # Compile source code only
```

**Testing:**
```bash
make test         # Run unit tests
make test-verbose # Run tests with detailed output
```

**Distribution:**
```bash
make install      # Install JAR to local Maven repository (~/.m2)
make coordinates  # Show Maven dependency for other projects
```

**Maintenance:**
```bash
make clean        # Remove build artifacts
make docs         # Generate Javadoc in target/site/
make check-repo   # Verify library in local Maven repo
```

## Usage in Other Projects

After `make install`, add to pom.xml:
```xml
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>
```

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
3. Rebuild: `make clean && make install`
4. License will be included in JAR at `META-INF/LICENSE.md`
