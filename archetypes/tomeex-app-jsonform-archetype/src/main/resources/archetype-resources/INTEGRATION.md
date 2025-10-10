# ${artifactId} - Integration Guide

This JAR library provides **JSON-Driven Scenario Architecture** for TomEEx webapps.

## 📦 Installation

### Step 1: Add Dependency to Your Webapp

Add this dependency to your webapp's `pom.xml`:

```xml
<dependency>
    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
</dependency>
```

### Step 2: Configure web.xml

Add the following to your webapp's `src/main/webapp/WEB-INF/web.xml`:

```xml
<!-- Context Parameters for Scenario Support -->
<context-param>
    <param-name>app.name</param-name>
    <param-value>main</param-value>
    <description>Config filename (always "main" for main.json)</description>
</context-param>

<context-param>
    <param-name>scenarios.path</param-name>
    <param-value>${symbol_dollar}{catalina.base}/webapps/YOUR_WEBAPP_NAME/WEB-INF/scenarios</param-value>
    <description>Path to scenarios directory</description>
</context-param>
```

**Replace `YOUR_WEBAPP_NAME`** with your actual webapp artifact ID.

### Step 3: Copy Scenario Templates

Copy the example scenarios from this project to your webapp:

```bash
# From this JAR project directory
cp -r src/main/webapp/WEB-INF/scenarios YOUR_WEBAPP/src/main/webapp/WEB-INF/
```

This includes:
- `WEB-INF/scenarios/json/main.json` - Main configuration
- `WEB-INF/scenarios/json/forms/EXAMPLE_LIST.json` - Grid schema
- `WEB-INF/scenarios/json/forms/EXAMPLE_FORM.json` - Form schema

### Step 4: Build and Deploy

```bash
# Build this JAR library
mvn clean install

# Build your webapp (which now includes this dependency)
cd YOUR_WEBAPP
make deploy
```

## 🚀 Usage

### API Endpoints

Once integrated, your webapp will have these endpoints:

```bash
# Get full configuration
GET /api/scenario/config

# List all scenarios
GET /api/scenario/list

# Get specific scenario
GET /api/scenario/{scenarioKey}
```

### Example Request

```bash
curl http://localhost:9292/your-webapp/api/scenario/config
```

Response:
```json
{
  "name": "your-webapp",
  "scenarios": {
    "HOME": { ... },
    "LIST_EXAMPLE": { ... },
    "EXAMPLE_DETAIL": { ... }
  }
}
```

## 🎨 Customization

### Create New Scenarios

Edit `WEB-INF/scenarios/json/main.json`:

```json
{
  "scenarios": {
    "LIST_PRODUCTS": {
      "title": "Products",
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

### Create Schema Files

Create `WEB-INF/scenarios/json/forms/PRODUCT_LIST.json`:

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

## 📋 Feature Flags

This library was generated with these features:

- ✅ Custom Grid: ${enableCustomGrid}
- ✅ Custom Form: ${enableCustomForm}
- ✅ Custom View: ${enableCustomView}
- ✅ Role Authorization: ${enableRoleAuthorization}
- ✅ Breadcrumbs: ${enableBreadcrumbs}
- ✅ Schema Validation: ${enableSchemaValidation}
- ✅ Dynamic Enums: ${enableDynamicEnums}

## 🔧 Troubleshooting

### Scenario not loading

Check that:
1. `scenarios.path` in web.xml points to correct directory
2. `main.json` exists and is valid JSON
3. Schema files referenced exist in `forms/` directory

### Servlet not found

Ensure:
1. JAR dependency is in your webapp's `pom.xml`
2. Webapp is rebuilt after adding dependency
3. TomEE container restarted

## 📚 Documentation

See main README.md for full architecture documentation.
