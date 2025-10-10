# Sportello Scenario Maven Archetype

Maven archetype for creating JSON-Driven Scenario Architecture applications in Java.

**Ported from:** PHP Sportello SCAI system (`app/Helpers/SportelloHelpers.php`)

## 🎯 Overview

This archetype generates a complete Java web application that implements the **JSON-Driven Scenario Architecture** pattern, where:

- **Scenarios** define pages/views (LIST, FORM, DETAIL)
- **Schemas** define form fields and grid columns via JSON
- **Actions** define user interactions declaratively
- **Role-based authorization** filters content dynamically
- **Breadcrumb navigation** is automatically generated

All UI and business logic are **configured via JSON**, not hardcoded!

## 📦 Installation

### 1. Install Archetype to Local Maven Repository

```bash
cd maven-archetype
mvn clean install
```

This installs the archetype to `~/.m2/repository/`.

### 2. Generate New Project

```bash
mvn archetype:generate \
  -DarchetypeGroupId=com.olomedia.sportello \
  -DarchetypeArtifactId=sportello-scenario-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.mycompany \
  -DartifactId=my-scenario-app \
  -Dversion=1.0.0-SNAPSHOT \
  -Dpackage=com.mycompany.scenario \
  -DappServer=tomcat
```

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
| `appServer` | `tomcat` | Target server (`tomcat` or `tomee`) |

## 🏗️ Generated Project Structure

```
my-scenario-app/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/scenario/
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
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml
│   │       │   └── scenarios/                          (Place modules here)
│   │       │       └── MyModule/
│   │       │           └── Config/
│   │       │               └── json/
│   │       │                   ├── MyModule.json
│   │       │                   └── forms/
│   │       │                       ├── ENTITY_FORM.json
│   │       │                       └── ENTITY_LIST.json
│   │       └── index.html
│   └── test/
│       └── java/
└── README.md
```

## 🚀 Usage

### 1. Deploy to Tomcat/TomEE

```bash
cd my-scenario-app
mvn clean package

# For Tomcat
cp target/my-scenario-app.war $CATALINA_HOME/webapps/

# For TomEE
mvn tomee:run
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
- **Scenario Pattern**: See CLAUDE.md in Sportello SCAI project
- **JSON Schema**: [JSON Schema Specification](https://json-schema.org/)

## 🤝 Contributing

This archetype is maintained as part of the Sportello SCAI modernization effort.

## 📄 License

Same as Sportello SCAI project.

---

**Generated by Sportello Module Generator** 🚀
