# ${artifactId}

RESTful API application with ${dbType} database support.

## Quick Start

### Database Setup

Initialize the database table:
```bash
make init-db
```

### Build Commands

```bash
# Clean build artifacts
make clean

# Build the application
make build

# Deploy to TomEE
make deploy

# Check deployment status
make status
```

## API Endpoints

- **GET** `/api/items` - Get all items
- **GET** `/api/items/{id}` - Get item by ID  
- **POST** `/api/items` - Create new item

## Testing

After deployment:
- **Web UI**: http://localhost:9292/${artifactId}/
- **API Base**: http://localhost:9292/${artifactId}/api/items

### Example API Calls

```bash
# Get all items
curl http://localhost:9292/${artifactId}/api/items

# Create new item
curl -X POST http://localhost:9292/${artifactId}/api/items \
  -H "Content-Type: application/json" \
  -d '{"title":"Sample Item","description":"This is a test item"}'

# Get specific item
curl http://localhost:9292/${artifactId}/api/items/1
```

## Database Configuration

- **Type**: ${dbType}
- **Connection**: Configured via TomEE JNDI datasource
- **Pool**: Connection pooling enabled (max 20 connections)

## Project Structure

```
${artifactId}/
├── pom.xml                          # Maven configuration with ${dbType} driver
├── Makefile                         # Build, deploy, and database commands
├── README.md                        # This file
├── src/
│   └── main/
│       ├── java/
│       │   └── ${package}/
│       │       ├── model/
│       │       │   └── Item.java    # Data model
│       │       ├── repository/
│       │       │   ├── DatabaseManager.java    # DB connection
│       │       │   ├── BaseRepository.java     # Base repository
│       │       │   └── ItemRepository.java     # Item repository
│       │       └── servlet/
│       │           └── ItemServlet.java        # REST API servlet
│       ├── resources/
│       │   └── META-INF/
│       │       └── context.xml      # TomEE datasource config
│       └── webapp/
│           ├── index.html           # API documentation page
│           └── WEB-INF/
│               └── web.xml          # Web app configuration
└── target/
    └── ${artifactId}.war           # Generated WAR file
```

## Features

- ✅ **Modern stack**: Java 17, Jakarta EE, JDBI
- ✅ **Database ready**: Pre-configured for ${dbType}
- ✅ **REST API**: JSON endpoints with CORS support
- ✅ **Connection pooling**: TomEE managed datasource
- ✅ **Easy deployment**: Single command deploy
- ✅ **Database initialization**: `make init-db` command

---

Generated with TomEE Development Environment