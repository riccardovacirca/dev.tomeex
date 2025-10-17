# TomEEx

Questo documento contiene una descrizione dettagliata delle specifiche del
progetto TomEEx e le linee guida per lo sviluppo di applicazioni e librerie

## Overview

TomEEx is a Docker-based TomEE development environment for building Java web
applications using Maven archetypes. It provides integrated database support
(PostgreSQL, MariaDB, SQLite) and rapid application scaffolding through code
generation.

**Technology Stack**:
- TomEE 9 (Jakarta EE 9+)
- Java 17
- Maven for build management
- Docker for containerization
- PostgreSQL/MariaDB/SQLite for database support

## Makefile

TomEEx utilizza un sistema centralizzato basato su **Makefile** per tutte le
operazioni di gestione progetti. Il Makefile principale si trova nella root
del progetto (`/workspace/Makefile`) e fornisce un'interfaccia unificata per
le operazioni più comuni.

### Convenzione Naming

I progetti sono identificati tramite `id=groupId.artifactId`:
- `groupId` è il nome completo del progetto (es. `com.example.myapp`)
- `artifactId` viene estratto automaticamente dall'ultima parte (es. `myapp`)
- La directory del progetto sarà `projects/{groupId}/`
  (es. `projects/com.example.myapp/`)

### Comandi Disponibili

#### Creare una Nuova Web Application

```bash
# Webapp semplice
make app id=com.example.myapp

# Webapp con database PostgreSQL
# `db` crea il database e l'utente e configura JNDI (`META-INF/context-dev.xml`)
make app id=com.example.myapp db=postgres

# Webapp con database MariaDB
# `db` crea il database e l'utente e configura JNDI (`META-INF/context-dev.xml`)
make app id=com.example.myapp db=mariadb

# Webapp con database SQLite
# `db` crea il database e configura JNDI (`META-INF/context-dev.xml`)
make app id=com.example.myapp db=sqlite
```

#### Creare una Nuova Libreria (JAR)

```bash
# Libreria semplice
make lib id=com.example.mylib

# Libreria con supporto multi-database
make lib id=com.example.mylib db=true
```

#### Altri comadi

```bash
# Elenca tutte le cartelle di progetto
make list

make remove id=com.example.myapp

make archetypes

# Commit e push
make push m="Messaggio di commit"

# Pull da repository remoto
make pull

make help
# oppure semplicemente
make
```

### Workflow Tipico

```bash
# 1. Creare una nuova webapp con database
make app id=com.mycompany.myapi db=postgres

# 2. Navigare nel progetto
cd projects/com.mycompany.myapi

# 3. Sviluppare (usare il Makefile locale del progetto)
make deploy          # Prima deployment
make                 # Deploy modifiche successive
make test            # Eseguire test
make dbcli           # Accedere al database

# 4. Tornare alla root per gestione progetti
cd /workspace

# 5. Vedere tutti i progetti
make list

# 6. Commit modifiche
make push m="Implementata nuova feature"
```
