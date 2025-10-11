# ContextView Add-on for TomEEx Webapps

Add JSON-driven ContextView functionality to your existing TomEEx webapp.

## What is ContextView?

ContextView enables you to define forms, grids, views, and navigation flows through JSON configuration files, eliminating the need for repetitive UI code.

## Features

- **JSON-Driven Configuration**: Define application flows through JSON schemas
- **Dynamic Form Generation**: Automatically generate forms from JSON schemas with validation
- **Grid/List Views**: Configure data grids with sorting, filtering, and pagination
- **Role-Based Authorization**: Control access based on user roles
- **Schema Validation**: Validate JSON configurations against defined schemas
- **Breadcrumb Navigation**: Automatic breadcrumb generation
- **RESTful API**: Built-in servlet for serving contextview configurations

## Installation

This add-on is integrated via your webapp's Makefile. **Do not use this archetype directly.**

### Prerequisites

1. A TomEEx webapp already created (e.g., generated with `make app`)
2. Database support configured (optional but recommended)

### Add ContextView to Your Webapp

From your webapp directory:

```bash
cd /workspace/projects/<your-groupId>
make contextview
```

This will:
1. Generate ContextView addon code
2. Copy Java files to `src/main/java/contextview/`
3. Copy webapp resources to `src/main/webapp/WEB-INF/contextviews/`

### Complete Installation

After running `make contextview`, follow the displayed instructions:

#### 1. Add Dependencies to pom.xml

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.15.2</version>
</dependency>
<dependency>
    <groupId>com.github.erosb</groupId>
    <artifactId>everit-json-schema</artifactId>
    <version>1.14.2</version>
</dependency>
```

#### 2. Configure web.xml

Add to `src/main/webapp/WEB-INF/web.xml`:

```xml
<context-param>
    <param-name>contextviews.path</param-name>
    <param-value>${catalina.base}/webapps/YOUR-APP-NAME/WEB-INF/contextviews</param-value>
</context-param>
```

Replace `YOUR-APP-NAME` with your webapp's artifactId.

#### 3. Deploy

```bash
make deploy
```

## Usage

### API Endpoints

Once deployed, these endpoints are available:

```bash
# Get full configuration
GET /api/contextview/config

# List all contextviews
GET /api/contextview/list

# Get specific contextview
GET /api/contextview/{contextviewKey}
```

### Example Request

```bash
curl http://localhost:9292/your-webapp/api/contextview/config
```

## Configuration

### Main Configuration

Edit `src/main/webapp/WEB-INF/contextviews/json/main.json`:

```json
{
  "name": "myapp",
  "contextviews": {
    "LIST_PRODUCTS": {
      "title": "Product List",
      "route": "products",
      "component": "custom-grid",
      "roles": ["admin", "user"],
      "options": {
        "schema": "PRODUCT_LIST",
        "api": [{"method": "GET", "url": "api/products"}]
      }
    }
  }
}
```

### Schema Files

Create schema files in `src/main/webapp/WEB-INF/contextviews/json/forms/`:

**PRODUCT_LIST.json:**
```json
{
  "schema": {
    "id": {"type": "number", "title": "ID"},
    "name": {"type": "string", "title": "Product Name"},
    "price": {"type": "number", "title": "Price"}
  },
  "grid": [
    {"key": "id", "width": "10%"},
    {"key": "name", "width": "60%"},
    {"key": "price", "width": "30%"}
  ]
}
```

## Example ContextViews

The addon includes example contextviews:

- **HOME**: Landing page with navigation
- **EXAMPLE_LIST**: Data grid example
- **EXAMPLE_FORM**: Form example with validation

Access them at:
- `http://localhost:9292/your-webapp/#/home`
- `http://localhost:9292/your-webapp/#/example-list`
- `http://localhost:9292/your-webapp/#/example-form`

## Customization

All ContextView code is now part of your webapp:

- **Java classes**: `src/main/java/contextview/`
- **JSON configs**: `src/main/webapp/WEB-INF/contextviews/json/`
- **Schemas**: `src/main/webapp/WEB-INF/contextviews/json/forms/`

Modify them directly to suit your needs.

## Troubleshooting

### ContextView not loading

Check that:
1. `contextviews.path` in web.xml is correct
2. `main.json` exists and is valid JSON
3. Referenced schema files exist in `forms/` directory

### Servlet not found

Ensure:
1. Jackson and everit-json-schema dependencies in `pom.xml`
2. Webapp rebuilt after adding dependencies: `make deploy`
3. TomEE shows no deployment errors in logs

## License

This add-on inherits the license from your webapp project.
