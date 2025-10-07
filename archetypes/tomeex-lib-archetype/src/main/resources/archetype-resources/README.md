# ${artifactId}

${artifactId} Library for TomEEx projects

## Overview

Reusable JAR library providing common functionality for TomEE webapps and libraries.

**Maven Coordinates:**
```xml
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>
```

## Install

After cloning this library into your TomEEx environment:

### 1. Clone into projects directory
```bash
cd /path/to/dev.tomeex/projects/
git clone <repository-url> ${groupId}
```

### 2. Build and install (inside TomEE container)
```bash
docker exec -it tomeex bash
cd /workspace/projects/${groupId}
make install
```

This will:
- Compile the library with Java 17
- Run tests
- Install to local Maven repository (`~/.m2/repository/${groupId}/${artifactId}/${version}/`)
- Copy JARs to `/workspace/lib/` for backup

### 3. Use in other projects

The library is now available to any Maven project in the TomEEx environment. Simply add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>
```

## Development

All development commands should be run **inside the TomEE container**:

```bash
docker exec -it tomeex bash
cd /workspace/projects/${groupId}
```

**Available commands:**
- `make install` - Build and install main JAR to Maven repo (default)
- `make install-sources` - Install sources JAR for IDE support
- `make install-javadoc` - Install Javadoc JAR for documentation
- `make install-full` - Install main + sources + javadoc JARs
- `make build` - Build JAR and copy to /workspace/lib/
- `make compile` - Compile source code only
- `make test` - Run unit tests
- `make clean` - Clean build artifacts
- `make docs` - Generate Javadoc HTML documentation
- `make check-repo` - Verify installation in Maven repo

## License

See LICENSE.md for licensing information.
