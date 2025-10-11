# TomEEx ContextView Library Archetype

Maven archetype for creating a **JAR library add-on** that provides JSON-Driven Scenario Architecture to TomEEx webapps.

## 🎯 Overview

This archetype generates a **reusable JAR library** that implements the **JSON-Driven Scenario Architecture** pattern, where:

- **Scenarios** define pages/views (LIST, FORM, DETAIL)
- **Schemas** define form fields and grid columns via JSON
- **Actions** define user interactions declaratively
- **Role-based authorization** filters content dynamically
- **Breadcrumb navigation** is automatically generated

All UI and business logic are **configured via JSON**, not hardcoded!

## 📦 Installation

### 1. Install Archetype to Local Maven Repository

```bash
cd /workspace/archetypes/tomeex-lib-contextview-archetype
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn clean install
```

This installs the archetype to `~/.m2/repository/`.

### 2. Generate ContextView Library

```bash
mvn archetype:generate \
  -DarchetypeGroupId=dev.tomeex.archetypes \
  -DarchetypeArtifactId=tomeex-lib-contextview-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=dev.tomeex.addons \
  -DartifactId=contextview-addon \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=dev.tomeex.contextview
```

### 3. Build and Install JAR

```bash
cd contextview-addon
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
mvn clean install
```

This creates:
- `contextview-addon-1.0.0-SNAPSHOT.jar` → Main library
- `contextview-addon-1.0.0-SNAPSHOT-sources.jar` → Sources

### 4. Add to Your TomEEx Webapp

In your webapp's `pom.xml`:

```xml
<dependency>
    <groupId>dev.tomeex.addons</groupId>
    <artifactId>contextview-addon</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

See **INTEGRATION.md** in the generated project for complete integration instructions.

### 3. Feature Selection via -D Parameters

Control which features to include:

```bash
mvn archetype:generate \
  ... \
  -DenableCustomGrid=true \
  -DenableCustomForm=true \
  -DenableCustomView=false \
  -DenableRoleAuthorization=true \
  -DenableBreadcrumbs=true \
  -DenableSchemaValidation=true \
  -DenableDynamicEnums=true \
  -DappServer=tomcat
```

**Available Parameters:**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `enableCustomGrid` | `true` | Data grid/table support |
| `enableCustomForm` | `true` | Dynamic form generation |
| `enableCustomView` | `false` | Custom view components |
| `enableRoleAuthorization` | `true` | Role-based access control |
| `enableBreadcrumbs` | `true` | Auto-generated breadcrumbs |
| `enableSchemaValidation` | `true` | JSON Schema validation |
| `enableDynamicEnums` | `true` | Dynamic select/enum population |
| `appServer` | `tomee` | Target server (`tomcat` or `tomee`) |

## 🏗️ Generated JAR Library Structure

```
contextview-addon/
├── pom.xml                                             (JAR packaging)
├── INTEGRATION.md                                       (Integration guide)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── dev/tomeex/contextview/
│   │   │       ├── core/
│   │   │       │   ├── Scenario.java
│   │   │       │   └── ScenarioOptions.java
│   │   │       ├── processor/
│   │   │       │   ├── ScenarioProcessor.java          (Core processing)
│   │   │       │   ├── RoleAuthorizationFilter.java    (if enabled)
│   │   │       │   ├── BreadcrumbGenerator.java        (if enabled)
│   │   │       │   └── SchemaValidator.java            (if enabled)
│   │   │       └── servlet/
│   │   │           └── ScenarioServlet.java            (REST API)
│   │   ├── resources/
│   │   │   └── logback.xml
│   │   └── webapp/                                      (TEMPLATES - copy to your webapp)
│   │       └── WEB-INF/
│   │           ├── web.xml                             (Configuration example)
│   │           └── scenarios/
│   │               └── json/
│   │                   ├── main.json                   (Example config)
│   │                   └── forms/
│   │                       ├── EXAMPLE_FORM.json
│   │                       └── EXAMPLE_LIST.json
│   └── test/
│       └── java/
└── target/
    ├── contextview-addon-1.0.0-SNAPSHOT.jar               (Main library)
    └── contextview-addon-1.0.0-SNAPSHOT-sources.jar       (Sources)
```

**Note:** Files in `src/main/webapp/` are templates to copy to your target webapp, not packaged in the JAR.

## 🚀 Integration in TomEEx Webapp

### 1. Add JAR Dependency

Edit your webapp's `pom.xml`:

```xml
<dependency>
    <groupId>dev.tomeex.addons</groupId>
    <artifactId>contextview-addon</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Copy Configuration Templates

```bash
# From JAR library project
cd contextview-addon
cp -r src/main/webapp/WEB-INF/scenarios /workspace/projects/YOUR_WEBAPP/src/main/webapp/WEB-INF/

# Copy web.xml snippet (merge manually)
cat src/main/webapp/WEB-INF/web.xml
```

### 3. Update web.xml

Add to your webapp's `web.xml`:

```xml
<context-param>
    <param-name>app.name</param-name>
    <param-value>main</param-value>
</context-param>

<context-param>
    <param-name>scenarios.path</param-name>
    <param-value>${catalina.base}/webapps/YOUR_WEBAPP/WEB-INF/scenarios</param-value>
</context-param>
```

### 4. Build and Deploy

```bash
cd /workspace/projects/YOUR_WEBAPP
make deploy
```

### 2. Create Scenario Modules

Place modules in `${catalina.base}/scenarios/` or `webapp/WEB-INF/scenarios/`:

**Example: MyModule/Config/json/MyModule.json**

```json
{
  "name": "MyModule",
  "scenarios": {
    "LIST_PRODUCTS": {
      "title": "Products",
      "route": "products",
      "component": "custom-grid",
      "roles": ["admin", "user"],
      "options": {
        "schema": "PRODUCT_LIST",
        "api": [
          {
            "method": "GET",
            "url": "api/products"
          }
        ],
        "actions-row": {
          "items": [
            {
              "code": "detail",
              "title": "Details",
              "action": {"type": "goto-page"},
              "goto-scenario": "PRODUCT_DETAIL"
            }
          ]
        }
      }
    },
    "PRODUCT_DETAIL": {
      "title": "Product Details",
      "route": "products/detail",
      "component": "custom-form",
      "roles": ["admin", "user"],
      "options": {
        "schema": "PRODUCT_FORM",
        "schema_mode": "update",
        "api": [
          {
            "method": "GET",
            "url": "api/products/<master_id>"
          }
        ]
      }
    }
  }
}
```

**Example: MyModule/Config/json/forms/PRODUCT_FORM.json**

```json
{
  "schema": {
    "id": {
      "type": "number",
      "title": "ID",
      "readOnly": true
    },
    "name": {
      "type": "string",
      "title": "Product Name",
      "required": true,
      "maxLength": 255
    },
    "price": {
      "type": "number",
      "title": "Price",
      "required": true
    },
    "active": {
      "type": "boolean",
      "title": "Active",
      "default": true
    }
  },
  "form": [
    {"key": "id"},
    {"key": "name"},
    {"key": "price"},
    {"key": "active"}
  ]
}
```

### 3. Access API Endpoints

```bash
# Get full configuration
curl http://localhost:8080/my-scenario-app/api/scenario/config/MyModule?role=admin

# List scenarios
curl http://localhost:8080/my-scenario-app/api/scenario/list/MyModule

# Get specific scenario
curl http://localhost:8080/my-scenario-app/api/scenario/MyModule/LIST_PRODUCTS
```

## 🔧 Architecture

### Processing Pipeline

Equivalent to PHP `SportelloHelpers::readConfiguration()`:

1. **Load Module JSON** - Read configuration file
2. **Process Scenarios** - For each scenario:
   - Load and inline schema files
   - Apply transformations (insert/update mode)
   - Process dynamic fields (enums, selects)
3. **Apply Role Authorization** - Filter by user role
4. **Generate Breadcrumbs** - Build navigation hierarchy
5. **Return Processed JSON** - Ready for frontend

### Java Classes Mapping

| PHP Function | Java Class | Description |
|--------------|------------|-------------|
| `SportelloHelpers::readConfiguration()` | `ScenarioProcessor.processConfiguration()` | Main processing |
| `SportelloHelpers::fixRoleAuthorization()` | `RoleAuthorizationFilter.filterByRole()` | Role filtering |
| `SportelloHelpers::breadcrumb()` | `BreadcrumbGenerator.generateBreadcrumb()` | Breadcrumb generation |
| `SportelloHelpers::addAngularJSONClasses()` | `ScenarioProcessor.applySchemaTransformations()` | Schema transformation |
| `SportelloHelpers::prepareJsonSchema()` | `ScenarioProcessor.prepareJsonSchema()` | Mode-based preparation |

## 📝 Configuration

### Web Application (web.xml)

```xml
<context-param>
    <param-name>scenarios.path</param-name>
    <param-value>${catalina.base}/scenarios</param-value>
</context-param>
```

### Role Management

Roles are checked from:
1. Query parameter: `?role=admin`
2. HTTP session: `active_role` attribute
3. Security context: `request.isUserInRole("admin")`

## 🧪 Testing

```bash
cd my-scenario-app
mvn test
```

## 📚 Documentation

- **Original PHP Implementation**: `app/Helpers/SportelloHelpers.php:324-424`
- **JSON Schema**: [JSON Schema Specification](https://json-schema.org/)
