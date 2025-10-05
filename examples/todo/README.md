# Todo API

Una webapp REST API completa per la gestione di Todo, costruita con Jakarta Servlets, JDBI e PostgreSQL.

## Architettura

```
Client → Servlet → Service → Repository → Database
```

- **Model**: Entità Todo con validazioni
- **Repository**: Pattern Repository con JDBI per accesso database
- **Service**: Logica business e validazioni
- **Servlet**: REST API endpoints con gestione JSON

## Struttura del progetto

```
src/
├── main/
│   ├── java/com/example/todo/
│   │   ├── model/Todo.java           # Entità Todo
│   │   ├── repository/               # Data Access Layer
│   │   │   ├── DatabaseManager.java # Singleton JDBI
│   │   │   ├── BaseRepository.java  # Repository base
│   │   │   └── TodoRepository.java  # Repository specifico
│   │   ├── service/                 # Business Logic Layer
│   │   │   ├── TodoService.java     # Servizio principale
│   │   │   └── ValidationException.java
│   │   └── servlet/                 # Web Layer
│   │       └── TodoServlet.java     # REST API endpoints
│   ├── resources/META-INF/
│   │   └── context.xml              # Configurazione DataSource
│   └── webapp/WEB-INF/
│       └── web.xml                  # Configurazione webapp
└── test/java/                       # Test unitari
```

## API Endpoints

### Tutti i todos
```bash
GET /api/todos
GET /api/todos?completed=true
GET /api/todos?category=work
GET /api/todos?priority=HIGH
GET /api/todos?search=groceries
```

### Todo specifico
```bash
GET /api/todos/{id}
PUT /api/todos/{id}
DELETE /api/todos/{id}
```

### Operazioni
```bash
POST /api/todos                # Crea nuovo todo
POST /api/todos/{id}/toggle    # Cambia stato completed
GET /api/todos/stats           # Statistiche
```

## Esempio JSON

### Creazione Todo
```json
{
  "title": "Buy groceries",
  "description": "Milk, bread, eggs",
  "priority": "HIGH",
  "category": "personal"
}
```

### Risposta Todo
```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, bread, eggs",
  "completed": false,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": null,
  "completedAt": null,
  "priority": "HIGH",
  "category": "personal"
}
```

## Database Setup

### Tabella PostgreSQL
```sql
CREATE DATABASE todo_db;
CREATE USER todo_user WITH PASSWORD 'todo_password';
GRANT ALL PRIVILEGES ON DATABASE todo_db TO todo_user;

\c todo_db

CREATE TABLE todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP,
    priority VARCHAR(20) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    category VARCHAR(100)
);

CREATE INDEX idx_todos_completed ON todos(completed);
CREATE INDEX idx_todos_category ON todos(category);
CREATE INDEX idx_todos_priority ON todos(priority);
CREATE INDEX idx_todos_created_at ON todos(created_at);
```

## Build e Deploy

### Con Maven (nel container)
```bash
# Nel container TomEE
cd /workspace/examples/todo

# Compila e crea WAR
mvn clean package

# Deploy su TomEE
cp target/todo-api.war /usr/local/tomee/webapps/

# Verifica deploy
curl http://localhost:9292/todo-api/api/todos/stats
```

### Configurazione DataSource

Il file `src/main/resources/META-INF/context.xml` configura la connessione database:

```xml
<Context>
    <Resource name="jdbc/TodoDB" 
              auth="Container"
              type="javax.sql.DataSource"
              maxTotal="20" 
              maxIdle="5"
              maxWaitMillis="10000"
              username="todo_user" 
              password="todo_password"
              driverClassName="org.postgresql.Driver"
              url="jdbc:postgresql://localhost:5432/todo_db"/>
</Context>
```

## Caratteristiche

- ✅ **CRUD completo** per Todo
- ✅ **Validazioni** complete su input
- ✅ **Filtri e ricerca** per categoria, priorità, stato
- ✅ **Statistiche** con contatori
- ✅ **Gestione errori** con status HTTP corretti
- ✅ **CORS** abilitato per frontend
- ✅ **JSON** serializzazione/deserializzazione
- ✅ **Connection pooling** con JDBI
- ✅ **Architettura layered** (MVC)
- ✅ **Pattern Repository** per data access
- ✅ **Singleton** per database manager

## Frontend Integration

L'API è progettata per essere consumata da un frontend JavaScript/React/Vue:

```javascript
// Esempio fetch JavaScript
const response = await fetch('/todo-api/api/todos', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        title: 'New Todo',
        priority: 'MEDIUM'
    })
});

const todo = await response.json();
```

## Testing

```bash
# Test con curl
curl -X POST http://localhost:9292/todo-api/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Todo","priority":"HIGH"}'

curl http://localhost:9292/todo-api/api/todos/stats
```