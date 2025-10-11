# ContextView Integration Guide

Complete step-by-step guide to add ContextView functionality to your TomEEx webapp.

## Quick Start

```bash
# From your webapp directory
cd /workspace/projects/<your-groupId>
make contextview
```

Follow the instructions displayed after the command completes.

## Manual Integration Steps

### 1. Add Jackson Dependencies

Add to your webapp's `pom.xml` in the `<dependencies>` section:

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

### 2. Configure web.xml

Add this context parameter to `src/main/webapp/WEB-INF/web.xml`:

```xml
<context-param>
    <param-name>contextviews.path</param-name>
    <param-value>${catalina.base}/webapps/YOUR-APP-NAME/WEB-INF/contextviews</param-value>
</context-param>
```

**Important:** Replace `YOUR-APP-NAME` with your actual webapp artifactId.

### 3. Deploy

```bash
make deploy
```

Wait for TomEE to deploy the webapp.

### 4. Test

```bash
# Test the API endpoint
curl http://localhost:9292/YOUR-APP-NAME/api/contextview/config

# Should return JSON configuration
```

## What Gets Installed

### Java Classes

Located in `src/main/java/contextview/`:

- **core/ContextView.java** - Core contextview representation
- **core/ContextViewOptions.java** - Configuration options
- **servlet/ContextViewServlet.java** - RESTful API servlet
- **processor/ContextViewProcessor.java** - Main processor
- **processor/BreadcrumbGenerator.java** - Breadcrumb support
- **processor/RoleAuthorizationFilter.java** - Role-based access
- **processor/SchemaValidator.java** - JSON schema validation

### Web Resources

Located in `src/main/webapp/WEB-INF/contextviews/`:

- **json/main.json** - Main configuration with contextview definitions
- **json/forms/EXAMPLE_LIST.json** - Example grid schema
- **json/forms/EXAMPLE_FORM.json** - Example form schema

## API Reference

### Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/contextview/config` | GET | Get full configuration |
| `/api/contextview/list` | GET | List all contextviews |
| `/api/contextview/{key}` | GET | Get specific contextview |

### Example Responses

**GET /api/contextview/config:**
```json
{
  "name": "myapp",
  "contextviews": {
    "HOME": {
      "title": "Home",
      "route": "home",
      "component": "custom-view",
      "options": { ... }
    }
  }
}
```

**GET /api/contextview/list:**
```json
{
  "contextviews": ["HOME", "EXAMPLE_LIST", "EXAMPLE_FORM"]
}
```

## Configuration Examples

### Example 1: Simple Grid

**main.json:**
```json
{
  "contextviews": {
    "LIST_USERS": {
      "title": "Users",
      "route": "users",
      "component": "custom-grid",
      "roles": ["admin"],
      "options": {
        "schema": "USER_LIST",
        "api": [{"method": "GET", "url": "api/users"}]
      }
    }
  }
}
```

**forms/USER_LIST.json:**
```json
{
  "schema": {
    "id": {"type": "number", "title": "ID"},
    "username": {"type": "string", "title": "Username"},
    "email": {"type": "string", "title": "Email"}
  },
  "grid": [
    {"key": "id", "width": "10%"},
    {"key": "username", "width": "40%"},
    {"key": "email", "width": "50%"}
  ]
}
```

### Example 2: Form with Validation

**main.json:**
```json
{
  "contextviews": {
    "CREATE_USER": {
      "title": "New User",
      "route": "users/new",
      "component": "custom-form",
      "roles": ["admin"],
      "options": {
        "schema": "USER_FORM",
        "api": [{"method": "POST", "url": "api/users"}]
      }
    }
  }
}
```

**forms/USER_FORM.json:**
```json
{
  "schema": {
    "username": {
      "type": "string",
      "title": "Username",
      "minLength": 3,
      "maxLength": 20
    },
    "email": {
      "type": "string",
      "title": "Email",
      "format": "email"
    },
    "password": {
      "type": "string",
      "title": "Password",
      "minLength": 8
    }
  },
  "required": ["username", "email", "password"]
}
```

### Example 3: Navigation with Actions

**main.json:**
```json
{
  "contextviews": {
    "USER_DETAIL": {
      "title": "User Details",
      "route": "users/:id",
      "component": "custom-view",
      "roles": ["admin", "user"],
      "options": {
        "schema": "USER_DETAIL",
        "api": [{"method": "GET", "url": "api/users/:id"}],
        "actions": [
          {
            "label": "Edit",
            "goto-contextview": "EDIT_USER",
            "roles": ["admin"]
          },
          {
            "label": "Back to List",
            "goto-contextview": "LIST_USERS"
          }
        ]
      }
    }
  }
}
```

## Customization

### Modifying Existing ContextViews

1. Edit JSON files in `src/main/webapp/WEB-INF/contextviews/json/`
2. Save changes
3. Run `make quick-deploy` (changes hot-reload automatically)

### Adding New ContextViews

1. Add contextview definition to `main.json`
2. Create schema file in `forms/` directory
3. Run `make quick-deploy`

### Extending Java Code

Modify Java classes in `src/main/java/contextview/`:
- Add custom processors
- Extend validation logic
- Add new servlet endpoints

After modifications: `make quick-deploy`

## Troubleshooting

### Issue: 404 on /api/contextview/*

**Solution:**
- Verify Jackson dependencies in `pom.xml`
- Check servlet mapping in generated code
- Rebuild: `make deploy`

### Issue: "contextviews.path not found"

**Solution:**
- Check `web.xml` has `contextviews.path` parameter
- Verify path points to correct directory
- Ensure webapp name matches in path

### Issue: JSON parsing errors

**Solution:**
- Validate JSON syntax in `main.json` and schema files
- Use online JSON validator
- Check TomEE logs for detailed error messages

### Issue: Roles not working

**Solution:**
- Verify user roles are set in session
- Check role names match in contextview config
- Review `RoleAuthorizationFilter.java` logic

## Advanced Usage

### Dynamic Enums

Load select options from API:

```json
{
  "schema": {
    "category": {
      "type": "string",
      "title": "Category",
      "enum-api": "api/categories"
    }
  }
}
```

### Conditional Fields

Show fields based on other field values:

```json
{
  "schema": {
    "type": {
      "type": "string",
      "enum": ["person", "company"]
    },
    "tax_id": {
      "type": "string",
      "title": "Tax ID",
      "show-if": {"field": "type", "value": "company"}
    }
  }
}
```

### Custom Validation

Add custom validation in `SchemaValidator.java`:

```java
public void validateCustom(Map<String, Object> data) {
    // Custom validation logic
    if (data.get("password") != data.get("confirmPassword")) {
        throw new ValidationException("Passwords do not match");
    }
}
```

## Next Steps

1. Review example contextviews in `WEB-INF/contextviews/json/`
2. Create your first custom contextview
3. Connect to your database via API endpoints
4. Customize the UI components in your frontend

For more information, see the main README.md.
