#!/bin/sh

set -e

# Command line options
INSTALL_POSTGRES=false
INSTALL_MARIADB=false
INSTALL_SQLITE=false
INSTALL_CLAUDE=false
PURGE_POSTGRES=false
PURGE_MARIADB=false
CREATE_WEBAPP=""
CREATE_LIBRARY=""
REMOVE_WEBAPP=""
REMOVE_LIBRARY=""
DATABASE_TYPE=""
GROUP_ID=""
ARTIFACT_ID=""

print_info() {
  printf "[info] %s\n" "$1"
}

print_warn() {
  printf "[warn] %s\n" "$1"
}

print_error() {
  printf "[error] %s\n" "$1"
}

print_header() {
  printf "[tomeex] %s\n" "$1"
}

check_archetype_installed() {
  local groupId="$1"
  local artifactId="$2"
  local version="$3"
  # Convert groupId to path format (dev.tomeex.archetypes -> dev/tomeex/archetypes)
  local groupPath=$(echo "$groupId" | sed 's/\./\//g')
  local archetypePath="$HOME/.m2/repository/$groupPath/$artifactId/$version"
  [ -d "$archetypePath" ]
}

ensure_archetype_installed() {
  local archetype_name="$1"
  local archetypes_dir="archetypes"
  # Check if archetype is already installed
  if check_archetype_installed "dev.tomeex.archetypes" "$archetype_name" "1.0.0"; then
    print_info "Archetype '$archetype_name' already installed"
    return 0
  fi
  # Check if archetype directory exists
  if [ ! -d "$archetypes_dir/$archetype_name" ]; then
    print_error "Archetype directory not found: $archetypes_dir/$archetype_name"
    exit 1
  fi
  # Install the archetype
  print_info "Installing archetype: $archetype_name"
  cd "$archetypes_dir/$archetype_name" || exit 1
  mvn clean install -q || {
    print_error "Failed to install archetype: $archetype_name"
    exit 1
  }
  cd - > /dev/null || exit 1
  print_info "Archetype '$archetype_name' installed successfully"
}

create_env_file() {
  cat > ".env" << EOF
# Container Configuration
CONTAINER_NAME=tomeex
TOMEE_VERSION=9-jre17-plume

# Network Configuration
NETWORK_NAME=tomeex-net
HOST_PORT=9292

# Java Application Settings
HEAP_SIZE_MIN=256m
HEAP_SIZE_MAX=1024m

# Git Configuration (optional)
# GIT_USER=Your Name
# GIT_MAIL=your.email@example.com

# TomEE Manager Configuration
ADMIN_USER=admin
ADMIN_PASSWORD=secret
MANAGER_USER=manager
MANAGER_PASSWORD=secret

# PostgreSQL Configuration (optional)
POSTGRES_CONTAINER_NAME=tomeex-postgres
POSTGRES_VERSION=latest
POSTGRES_PORT=15432
POSTGRES_PASSWORD=devpass123

# MariaDB Configuration (optional)
MARIADB_CONTAINER_NAME=tomeex-mariadb
MARIADB_VERSION=latest
MARIADB_PORT=13306
MARIADB_ROOT_PASSWORD=secret

# SQLite Configuration (optional)
SQLITE_DATA_DIR=sqlite-data
EOF
}

check_docker() {
  if ! command -v docker > /dev/null 2>&1; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
  fi
  if ! docker info > /dev/null 2>&1; then
    print_error "Docker is not running. Please start Docker service."
    exit 1
  fi
}

create_basic_directories() {
  print_info "Creating basic project directories..."
  mkdir -p webapps conf logs work temp uploads
  print_info "Project directories created"
}

create_gitignore() {
  if [ ! -f ".gitignore" ]; then
    print_info "Creating .gitignore file..."
    cat > ".gitignore" << 'EOF'
*
!archetypes/
!archetypes/**
!docs/
!docs/**
!lib/
!lib/**
!examples/
!examples/**
!install.sh
!Makefile
!README.md
!LICENSE.md

archetypes/*/target/
archetypes/*/target/**
EOF
    print_info ".gitignore file created"
  else
    print_info ".gitignore already exists, skipping creation"
  fi
}

create_projects_folder() {
  mkdir -p projects
}

create_docker_network() {
  NETWORK_NAME=$(grep NETWORK_NAME .env | cut -d= -f2)
  # Check if network already exists (idempotency)
  if docker network inspect "$NETWORK_NAME" > /dev/null 2>&1; then
    print_info "Docker network $NETWORK_NAME already exists, skipping creation"
    return 0
  fi
  print_info "Creating Docker network: $NETWORK_NAME"
  docker network create "$NETWORK_NAME"
  print_info "Docker network created successfully"
}

pull_tomee_image() {
  TOMEE_VERSION=$(grep TOMEE_VERSION .env | cut -d= -f2)
  # Check if image already exists (idempotency)
  if docker image inspect "tomee:$TOMEE_VERSION" > /dev/null 2>&1; then
    print_info "TomEE $TOMEE_VERSION image already exists, skipping pull"
    return 0
  fi
  print_info "Pulling TomEE $TOMEE_VERSION image..."
  docker pull "tomee:$TOMEE_VERSION"
  print_info "TomEE image pulled successfully"
}

create_tomee_config() {
  print_info "Creating TomEE configuration..."
  # Get credentials from .env
  ADMIN_USER=$(grep ADMIN_USER .env | cut -d= -f2)
  ADMIN_PASSWORD=$(grep ADMIN_PASSWORD .env | cut -d= -f2)
  MANAGER_USER=$(grep MANAGER_USER .env | cut -d= -f2)
  MANAGER_PASSWORD=$(grep MANAGER_PASSWORD .env | cut -d= -f2)
  # Create tomcat-users.xml for manager access (TomEE uses same file as Tomcat)
  if [ ! -f "conf/tomcat-users.xml" ]; then
    cat > "conf/tomcat-users.xml" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<tomcat-users xmlns="http://tomcat.apache.org/xml"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://tomcat.apache.org/xml tomcat-users.xsd"
              version="1.0">
  
  <!-- Define roles -->
  <role rolename="manager-gui"/>
  <role rolename="manager-script"/>
  <role rolename="manager-jmx"/>
  <role rolename="manager-status"/>
  <role rolename="admin-gui"/>
  <role rolename="admin-script"/>
  
  <!-- Define users -->
  <user username="$ADMIN_USER" password="$ADMIN_PASSWORD" roles="manager-gui,manager-script,manager-jmx,manager-status,admin-gui,admin-script"/>
  <user username="$MANAGER_USER" password="$MANAGER_PASSWORD" roles="manager-gui,manager-script,manager-status"/>
</tomcat-users>
EOF
  fi
  # Create manager context to allow access from any IP
  mkdir -p "conf/Catalina/localhost"
  if [ ! -f "conf/Catalina/localhost/manager.xml" ]; then
    cat > "conf/Catalina/localhost/manager.xml" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<Context antiResourceLocking="false" privileged="true" >
  <CookieProcessor className="org.apache.tomcat.util.http.Rfc6265CookieProcessor"
                   sameSiteCookies="strict" />
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="^.*$" />
  <Manager sessionAttributeValueClassNameFilter="java\.lang\.(?:Boolean|Integer|Long|Number|String)|org\.apache\.catalina\.filters\.CsrfPreventionFilter\$LruCache(?:\$1)?|java\.util\.(?:Linked)?HashMap"/>
</Context>
EOF
  fi
  print_info "TomEE configuration created"
}

copy_default_config() {
  TOMEE_VERSION=$(grep TOMEE_VERSION .env | cut -d= -f2)
  # Check if all config files already exist
  if [ -f "conf/server.xml" ] && [ -f "conf/web.xml" ] && \
     [ -f "conf/logging.properties" ] && [ -f "conf/catalina.properties" ] && \
     [ -d "webapps/manager" ]; then
    return 0
  fi
  print_info "Copying default TomEE configuration..."
  # Create temporary container to copy default configs
  temp_container_id=$(docker create "tomee:$TOMEE_VERSION")
  # Copy config files if they don't exist
  [ ! -f "conf/server.xml" ] && docker cp "$temp_container_id:/usr/local/tomee/conf/server.xml" "conf/" 2>/dev/null
  [ ! -f "conf/web.xml" ] && docker cp "$temp_container_id:/usr/local/tomee/conf/web.xml" "conf/" 2>/dev/null
  [ ! -f "conf/logging.properties" ] && docker cp "$temp_container_id:/usr/local/tomee/conf/logging.properties" "conf/" 2>/dev/null
  [ ! -f "conf/catalina.properties" ] && docker cp "$temp_container_id:/usr/local/tomee/conf/catalina.properties" "conf/" 2>/dev/null
  # Copy default webapps if needed
  [ ! -d "webapps/manager" ] && docker cp "$temp_container_id:/usr/local/tomee/webapps/." "webapps/" 2>/dev/null
  # Remove temporary container
  docker rm "$temp_container_id" > /dev/null
  print_info "Default TomEE configuration copied"
}

start_container() {
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Se il container esiste già (running o stopped), prova ad avviarlo
  if docker ps -a --format 'table {{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    print_info "Starting existing container: ${CONTAINER_NAME}"
    docker start "${CONTAINER_NAME}" > /dev/null 2>&1
    return 0
  fi
  # Il container non esiste, creane uno nuovo
  HOST_PORT=$(grep HOST_PORT .env | cut -d= -f2)
  TOMEE_VERSION=$(grep TOMEE_VERSION .env | cut -d= -f2)
  HEAP_SIZE_MIN=$(grep HEAP_SIZE_MIN .env | cut -d= -f2)
  HEAP_SIZE_MAX=$(grep HEAP_SIZE_MAX .env | cut -d= -f2)
  PROJECT_DIR=$(pwd)
  NETWORK_NAME=$(grep NETWORK_NAME .env | cut -d= -f2)
  docker run -d \
    --name "${CONTAINER_NAME}" \
    --network "${NETWORK_NAME}" \
    -p "${HOST_PORT}:8080" \
    -v "${PROJECT_DIR}:/workspace" \
    -w "/workspace" \
    -v "${PROJECT_DIR}/webapps:/usr/local/tomee/webapps" \
    -v "${PROJECT_DIR}/conf:/usr/local/tomee/conf" \
    -v "${PROJECT_DIR}/logs:/usr/local/tomee/logs" \
    -v "${PROJECT_DIR}/work:/usr/local/tomee/work" \
    -v "${PROJECT_DIR}/temp:/usr/local/tomee/temp" \
    -v /etc/localtime:/etc/localtime:ro \
    -v /etc/timezone:/etc/timezone:ro \
    -e CATALINA_OPTS="-Xmx${HEAP_SIZE_MAX} -Xms${HEAP_SIZE_MIN}" \
    -e JAVA_HOME="/opt/java/openjdk" \
    "tomee:${TOMEE_VERSION}"
  print_info "TomEE container started successfully"
}

wait_for_tomee() {
  HOST_PORT=$(grep HOST_PORT .env | cut -d= -f2)
  print_info "Waiting for TomEE to be ready..."
  for attempt in $(seq 1 30); do
    if curl -f -s "http://localhost:${HOST_PORT}" > /dev/null 2>&1; then
      print_info "TomEE is ready!"
      return 0
    fi
    printf "."
    sleep 2
  done
  echo ""
  print_warn "TomEE startup timeout. Check: docker logs $(grep "^CONTAINER_NAME=" .env | cut -d= -f2)"
  return 1
}

install_dev_tools() {
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  print_info "Installing development tools in container..."
  if docker exec "${CONTAINER_NAME}" sh -c "
    apt-get update -qq > /dev/null 2>&1 && \
    apt-get install -y --no-install-recommends \
      make openjdk-17-jdk-headless git maven wget curl rsync unzip postgresql-client default-mysql-client sqlite3 > /dev/null 2>&1 && \
    echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> /root/.bashrc && \
    apt-get clean > /dev/null 2>&1 && \
    rm -rf /var/lib/apt/lists/*
  " 2>/dev/null; then
    print_info "Development tools installed successfully"
  else
    print_error "Failed to install development tools"
    exit 1
  fi
}

configure_git() {
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Check if git configuration is available
  if ! grep -q "^GIT_USER=" .env || ! grep -q "^GIT_MAIL=" .env; then
    return 0
  fi
  GIT_USER=$(grep GIT_USER .env | cut -d= -f2)
  GIT_MAIL=$(grep GIT_MAIL .env | cut -d= -f2)
  if [ -n "$GIT_USER" ] && [ -n "$GIT_MAIL" ]; then
    docker exec "${CONTAINER_NAME}" sh -c "
      git config --global user.name '$GIT_USER' && \
      git config --global user.email '$GIT_MAIL' && \
      git config --global --add safe.directory /workspace
    "
    print_info "Git configured: $GIT_USER <$GIT_MAIL>"
  fi
}

configure_shell_aliases() {
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Check if cls alias already exists in .bashrc
  if docker exec "${CONTAINER_NAME}" grep -q "alias cls=" /root/.bashrc 2>/dev/null; then
    return 0
  fi
  # Add cls alias to .bashrc
  docker exec "${CONTAINER_NAME}" sh -c "cat >> /root/.bashrc << 'EOF'
# Custom aliases
alias cls=clear
EOF
"
  print_info "Shell aliases configured successfully"
}

pull_postgres_image() {
  POSTGRES_VERSION=$(grep POSTGRES_VERSION .env | cut -d= -f2)
  # Check if image already exists (idempotency)
  if docker image inspect "postgres:$POSTGRES_VERSION" > /dev/null 2>&1; then
    print_info "PostgreSQL $POSTGRES_VERSION image already exists, skipping pull"
    return 0
  fi
  print_info "Pulling PostgreSQL $POSTGRES_VERSION image..."
  docker pull "postgres:$POSTGRES_VERSION"
  print_info "PostgreSQL image pulled successfully"
}

create_postgres_volume() {
  POSTGRES_CONTAINER_NAME=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  VOLUME_NAME="${POSTGRES_CONTAINER_NAME}-data"
  # Check if volume already exists (idempotency)
  if docker volume inspect "$VOLUME_NAME" > /dev/null 2>&1; then
    print_info "PostgreSQL volume $VOLUME_NAME already exists, skipping creation"
    return 0
  fi
  print_info "Creating PostgreSQL data volume..."
  docker volume create "$VOLUME_NAME"
  print_info "PostgreSQL volume created: $VOLUME_NAME"
}

start_postgres_container() {
  POSTGRES_CONTAINER_NAME=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  # Se il container esiste già (running o stopped), prova ad avviarlo
  if docker ps -a --format 'table {{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
    docker start "${POSTGRES_CONTAINER_NAME}" > /dev/null 2>&1
    print_info "PostgreSQL container started successfully"
    return 0
  fi
  # Il container non esiste, creane uno nuovo
  POSTGRES_VERSION=$(grep POSTGRES_VERSION .env | cut -d= -f2)
  POSTGRES_PORT=$(grep POSTGRES_PORT .env | cut -d= -f2)
  POSTGRES_PASSWORD=$(grep POSTGRES_PASSWORD .env | cut -d= -f2)
  NETWORK_NAME=$(grep NETWORK_NAME .env | cut -d= -f2)
  VOLUME_NAME="${POSTGRES_CONTAINER_NAME}-data"
  if docker run -d \
    --name "${POSTGRES_CONTAINER_NAME}" \
    --network "${NETWORK_NAME}" \
    -p "${POSTGRES_PORT}:5432" \
    -e POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
    -v "${VOLUME_NAME}:/var/lib/postgresql/data" \
    "postgres:${POSTGRES_VERSION}" > /dev/null 2>&1; then
    print_info "PostgreSQL container started successfully"
  else
    print_error "Failed to start PostgreSQL container"
    return 1
  fi
}

wait_for_postgres() {
  POSTGRES_CONTAINER_NAME=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  for attempt in $(seq 1 30); do
    if docker exec "${POSTGRES_CONTAINER_NAME}" pg_isready -U postgres -d postgres > /dev/null 2>&1; then
      print_info "PostgreSQL is ready"
      return 0
    fi
    sleep 2
  done
  print_error "PostgreSQL startup timeout"
  return 1
}

setup_postgres() {
  print_header "PostgreSQL Database Setup"
  echo ""
  pull_postgres_image
  create_postgres_volume
  start_postgres_container
  wait_for_postgres
  POSTGRES_PORT=$(grep POSTGRES_PORT .env | cut -d= -f2)
  POSTGRES_CONTAINER_NAME=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  echo ""
  print_info "=== PostgreSQL Setup Complete ==="
  print_info "Admin User: postgres"
  print_info "Connection: localhost:${POSTGRES_PORT}/postgres"
  print_info "Network: ${POSTGRES_CONTAINER_NAME}"
  echo ""
}

pull_mariadb_image() {
  MARIADB_VERSION=$(grep MARIADB_VERSION .env | cut -d= -f2)
  # Check if image already exists (idempotency)
  if docker image inspect "mariadb:$MARIADB_VERSION" > /dev/null 2>&1; then
    print_info "MariaDB $MARIADB_VERSION image already exists, skipping pull"
    return 0
  fi
  print_info "Pulling MariaDB $MARIADB_VERSION image..."
  docker pull "mariadb:$MARIADB_VERSION"
  print_info "MariaDB image pulled successfully"
}

create_mariadb_volume() {
  MARIADB_CONTAINER_NAME=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  VOLUME_NAME="${MARIADB_CONTAINER_NAME}-data"
  # Check if volume already exists (idempotency)
  if docker volume inspect "$VOLUME_NAME" > /dev/null 2>&1; then
    print_info "MariaDB volume $VOLUME_NAME already exists, skipping creation"
    return 0
  fi
  print_info "Creating MariaDB data volume..."
  docker volume create "$VOLUME_NAME"
  print_info "MariaDB volume created: $VOLUME_NAME"
}

start_mariadb_container() {
  MARIADB_CONTAINER_NAME=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  # Se il container esiste già (running o stopped), prova ad avviarlo
  if docker ps -a --format 'table {{.Names}}' | grep -q "^${MARIADB_CONTAINER_NAME}$"; then
    docker start "${MARIADB_CONTAINER_NAME}" > /dev/null 2>&1
    print_info "MariaDB container started successfully"
    return 0
  fi
  # Il container non esiste, creane uno nuovo
  MARIADB_VERSION=$(grep MARIADB_VERSION .env | cut -d= -f2)
  MARIADB_PORT=$(grep MARIADB_PORT .env | cut -d= -f2)
  MARIADB_ROOT_PASSWORD=$(grep MARIADB_ROOT_PASSWORD .env | cut -d= -f2)
  NETWORK_NAME=$(grep NETWORK_NAME .env | cut -d= -f2)
  VOLUME_NAME="${MARIADB_CONTAINER_NAME}-data"
  if docker run -d \
    --name "${MARIADB_CONTAINER_NAME}" \
    --network "${NETWORK_NAME}" \
    -p "${MARIADB_PORT}:3306" \
    -e MARIADB_ROOT_PASSWORD="${MARIADB_ROOT_PASSWORD}" \
    -v "${VOLUME_NAME}:/var/lib/mysql" \
    "mariadb:${MARIADB_VERSION}" > /dev/null 2>&1; then
    print_info "MariaDB container started successfully"
  else
    print_error "Failed to start MariaDB container"
    return 1
  fi
}

wait_for_mariadb() {
  MARIADB_CONTAINER_NAME=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  for attempt in $(seq 1 30); do
    if docker exec "${MARIADB_CONTAINER_NAME}" mysqladmin ping -h localhost > /dev/null 2>&1; then
      print_info "MariaDB is ready"
      return 0
    fi
    sleep 2
  done
  print_error "MariaDB startup timeout"
  return 1
}

setup_mariadb() {
  print_header "MariaDB Database Setup"
  echo ""
  pull_mariadb_image
  create_mariadb_volume
  start_mariadb_container
  wait_for_mariadb
  MARIADB_PORT=$(grep MARIADB_PORT .env | cut -d= -f2)
  MARIADB_CONTAINER_NAME=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  echo ""
  print_info "=== MariaDB Setup Complete ==="
  print_info "Admin User: root"
  print_info "Connection: localhost:${MARIADB_PORT}/mysql"
  print_info "Network: ${MARIADB_CONTAINER_NAME}"
  echo ""
}

purge_postgres() {
  print_header "PostgreSQL Database Removal"
  echo ""
  POSTGRES_CONTAINER_NAME=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  VOLUME_NAME="tomeex-postgres-data"

  # Stop and remove container
  if docker ps -a --format 'table {{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
    print_info "Stopping PostgreSQL container..."
    docker stop "${POSTGRES_CONTAINER_NAME}" > /dev/null 2>&1 || true
    print_info "Removing PostgreSQL container..."
    docker rm "${POSTGRES_CONTAINER_NAME}" > /dev/null 2>&1
    print_info "PostgreSQL container removed"
  else
    print_info "PostgreSQL container not found, skipping container removal"
  fi

  # Remove volume
  if docker volume ls --format '{{.Name}}' | grep -q "^${VOLUME_NAME}$"; then
    print_info "Removing PostgreSQL data volume..."
    docker volume rm "${VOLUME_NAME}" > /dev/null 2>&1
    print_info "PostgreSQL volume removed"
  else
    print_info "PostgreSQL volume not found, skipping volume removal"
  fi

  echo ""
  print_info "=== PostgreSQL Removal Complete ==="
  echo ""
}

purge_mariadb() {
  print_header "MariaDB Database Removal"
  echo ""
  MARIADB_CONTAINER_NAME=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  VOLUME_NAME="tomeex-mariadb-data"

  # Stop and remove container
  if docker ps -a --format 'table {{.Names}}' | grep -q "^${MARIADB_CONTAINER_NAME}$"; then
    print_info "Stopping MariaDB container..."
    docker stop "${MARIADB_CONTAINER_NAME}" > /dev/null 2>&1 || true
    print_info "Removing MariaDB container..."
    docker rm "${MARIADB_CONTAINER_NAME}" > /dev/null 2>&1
    print_info "MariaDB container removed"
  else
    print_info "MariaDB container not found, skipping container removal"
  fi

  # Remove volume
  if docker volume ls --format '{{.Name}}' | grep -q "^${VOLUME_NAME}$"; then
    print_info "Removing MariaDB data volume..."
    docker volume rm "${VOLUME_NAME}" > /dev/null 2>&1
    print_info "MariaDB volume removed"
  else
    print_info "MariaDB volume not found, skipping volume removal"
  fi

  echo ""
  print_info "=== MariaDB Removal Complete ==="
  echo ""
}

create_sqlite_directories() {
  SQLITE_DATA_DIR=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)
  mkdir -p "$SQLITE_DATA_DIR"
}

install_sqlite_in_container() {
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Check if SQLite3 is already installed
  if docker exec "${CONTAINER_NAME}" which sqlite3 > /dev/null 2>&1; then
    print_info "SQLite3 already installed in container"
    return 0
  fi
  print_info "Installing SQLite3 in TomEE container..."
  if docker exec "${CONTAINER_NAME}" sh -c "
    apt-get update -qq > /dev/null 2>&1 && \
    apt-get install -y --no-install-recommends sqlite3 > /dev/null 2>&1 && \
    apt-get clean > /dev/null 2>&1 && \
    rm -rf /var/lib/apt/lists/*
  " 2>/dev/null; then
    print_info "SQLite3 installed successfully"
  else
    print_warn "SQLite3 installation may have failed"
  fi
}

setup_sqlite() {
  print_header "SQLite Database Setup"
  echo ""
  create_sqlite_directories
  install_sqlite_in_container
  SQLITE_DATABASE=$(grep SQLITE_DATABASE .env | cut -d= -f2)
  SQLITE_DATA_DIR=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Create SQLite database file
  print_info "Creating SQLite database..."
  touch "${SQLITE_DATA_DIR}/${SQLITE_DATABASE}"
  # Initialize database with a simple test table
  SQLITE_PATH="/workspace/${SQLITE_DATA_DIR}/${SQLITE_DATABASE}"
  docker exec "${CONTAINER_NAME}" sqlite3 "$SQLITE_PATH" "CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT);"
  echo ""
  print_info "=== SQLite Setup Complete ==="
  print_info "Database: ${SQLITE_DATABASE}"
  print_info "Location: ${SQLITE_DATA_DIR}/${SQLITE_DATABASE}"
  print_info "JDBC URL: jdbc:sqlite:/workspace/${SQLITE_DATA_DIR}/${SQLITE_DATABASE}"
  print_info "Access from container: sqlite3 /workspace/${SQLITE_DATA_DIR}/${SQLITE_DATABASE}"
  echo ""
}

install_claude_code() {
  print_info "Starting Claude Code installation..."
  CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  # Check if development container is running
  if ! docker ps --format 'table {{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    print_error "Development container '$CONTAINER_NAME' is not running"
    print_error "Cannot install Claude Code without development container"
    exit 1
  fi
  # Check if Claude Code is already installed
  if docker exec "$CONTAINER_NAME" bash -c "command -v claude >/dev/null 2>&1"; then
    print_info "Claude Code already installed"
    return 0
  fi
  # Execute installation inside container
  if ! docker exec -i "$CONTAINER_NAME" bash -c "
    export DEBIAN_FRONTEND=noninteractive && \

    # Install curl if needed
    if ! command -v curl >/dev/null 2>&1; then
      if command -v apt-get >/dev/null 2>&1; then apt-get update && apt-get install -y curl
      elif command -v yum >/dev/null 2>&1; then yum install -y curl
      elif command -v apk >/dev/null 2>&1; then apk add --no-cache curl
      else echo 'ERROR: Install curl manually' && exit 1; fi
    fi && \

    # Install NVM if not present
    export NVM_DIR=\"\$HOME/.nvm\" && \
    if [ ! -s \"\$NVM_DIR/nvm.sh\" ]; then
      echo 'INFO: Installing NVM...' && \
      curl -o- 'https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.4/install.sh' | bash || { echo 'ERROR: NVM install failed'; exit 1; }
    fi && \

    # Source NVM for this session
    export NVM_DIR=\"\$HOME/.nvm\" && \
    [ -s \"\$NVM_DIR/nvm.sh\" ] && . \"\$NVM_DIR/nvm.sh\" && \
    [ -s \"\$NVM_DIR/bash_completion\" ] && . \"\$NVM_DIR/bash_completion\" && \

    # Add NVM to shell profiles
    for profile in ~/.bashrc ~/.bash_profile ~/.zshrc ~/.profile; do
      if [ -f \"\$profile\" ] && ! grep -q 'NVM_DIR' \"\$profile\"; then
        echo '' >> \"\$profile\" && \
        echo '# NVM Configuration' >> \"\$profile\" && \
        echo 'export NVM_DIR=\"\$HOME/.nvm\"' >> \"\$profile\" && \
        echo '[ -s \"\$NVM_DIR/nvm.sh\" ] && . \"\$NVM_DIR/nvm.sh\"' >> \"\$profile\" && \
        echo '[ -s \"\$NVM_DIR/bash_completion\" ] && . \"\$NVM_DIR/bash_completion\"' >> \"\$profile\"
      fi
    done && \

    # Install Node.js 18
    echo 'INFO: Installing Node.js 18...' && \
    nvm install 18 && nvm use 18 && nvm alias default 18 && \

    # Verify Node installation
    command -v node >/dev/null 2>&1 || { echo 'ERROR: Node.js installation verification failed'; exit 1; } && \

    # Install Claude Code
    echo 'INFO: Installing Claude Code...' && \
    npm install -g @anthropic-ai/claude-code && \

    # Verify Claude Code installation
    command -v claude >/dev/null 2>&1 || { echo 'ERROR: Claude Code installation verification failed'; exit 1; }
  "; then
    print_error "Claude Code installation failed"
    exit 1
  fi
  print_info "Claude Code installation completed successfully!"
  print_info "Run 'source ~/.bashrc' or start a new shell session inside container to use 'claude'"

  # Create symbolic link for Claude Code documentation
  print_info "Creating documentation symbolic link..."
  if docker exec "$CONTAINER_NAME" test -f /workspace/docs/PROJECT.md; then
    docker exec "$CONTAINER_NAME" sh -c "ln -sf /workspace/docs/PROJECT.md /workspace/CLAUDE.md" 2>/dev/null || true
    print_info "Documentation link created: /workspace/CLAUDE.md -> /workspace/docs/PROJECT.md"
  else
    print_warn "docs/PROJECT.md not found, symbolic link not created"
  fi
}

create_webapp_env_file() {
  local group_id="$1"
  local app_name="$2"
  local db_type="$3"
  local project_version="$4"
  local env_file="projects/$group_id/.env"

  print_info "Creating .env file for webapp '$app_name'..."

  # Extract project metadata and Git config from root .env
  local git_user=$(grep "^GIT_USER=" .env 2>/dev/null | cut -d= -f2)
  local git_mail=$(grep "^GIT_MAIL=" .env 2>/dev/null | cut -d= -f2)
  local project_license="PolyForm Noncommercial License 1.0.0"
  local creation_date=$(date +"%Y-%m-%d")

  # Set defaults if Git config is not available
  if [ -z "$git_user" ]; then
    git_user=""
  fi
  if [ -z "$git_mail" ]; then
    git_mail=""
  fi

  # Write header with project metadata (always included)
  cat > "$env_file" << EOF
# Project Metadata
PROJECT_GROUP_ID=$group_id
PROJECT_ARTIFACT_ID=$app_name
PROJECT_VERSION=$project_version
PROJECT_CREATED=$creation_date
PROJECT_LICENSE=$project_license

# Git Configuration (inherited from root .env)
GIT_USER=$git_user
GIT_MAIL=$git_mail

EOF

  # Write database configuration section
  if [ -n "$db_type" ]; then
    case "$db_type" in
      postgres)
        local postgres_host=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
        local postgres_port=$(grep POSTGRES_PORT .env | cut -d= -f2)
        cat >> "$env_file" << EOF
# Database Configuration
DB_TYPE=postgres
DB_HOST=$postgres_host
DB_PORT=5432
DB_NAME=$app_name
DB_USER=$app_name
DB_PASSWORD=secret

# JDBC Connection String
JDBC_URL=jdbc:postgresql://$postgres_host:5432/$app_name

# External Connection (from host machine)
EXTERNAL_HOST=localhost
EXTERNAL_PORT=$postgres_port
EXTERNAL_JDBC_URL=jdbc:postgresql://localhost:$postgres_port/$app_name
EOF
        ;;
      mariadb)
        local mariadb_host=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
        local mariadb_port=$(grep MARIADB_PORT .env | cut -d= -f2)
        cat >> "$env_file" << EOF
# Database Configuration
DB_TYPE=mariadb
DB_HOST=$mariadb_host
DB_PORT=3306
DB_NAME=$app_name
DB_USER=$app_name
DB_PASSWORD=secret

# JDBC Connection String
JDBC_URL=jdbc:mariadb://$mariadb_host:3306/$app_name

# External Connection (from host machine)
EXTERNAL_HOST=localhost
EXTERNAL_PORT=$mariadb_port
EXTERNAL_JDBC_URL=jdbc:mariadb://localhost:$mariadb_port/$app_name
EOF
        ;;
      sqlite)
        local sqlite_data_dir=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)
        cat >> "$env_file" << EOF
# Database Configuration
DB_TYPE=sqlite
DB_FILE=${app_name}.sqlite
DB_PATH=/workspace/$sqlite_data_dir/${app_name}.sqlite

# JDBC Connection String
JDBC_URL=jdbc:sqlite:/workspace/$sqlite_data_dir/${app_name}.sqlite

# Local File System Path
LOCAL_DB_PATH=$sqlite_data_dir/${app_name}.sqlite
EOF
        ;;
      *)
        print_warn ".env file creation not implemented for database type: $db_type"
        # Still write empty database section
        cat >> "$env_file" << EOF
# Database Configuration (not configured)
DB_TYPE=
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

# JDBC Connection String
JDBC_URL=
EOF
        ;;
    esac
  else
    # No database - write empty database configuration section
    cat >> "$env_file" << EOF
# Database Configuration (no database)
DB_TYPE=
DB_HOST=
DB_PORT=
DB_NAME=
DB_USER=
DB_PASSWORD=

# JDBC Connection String
JDBC_URL=
EOF
  fi

  print_info ".env file created: $env_file"
}

create_webapp_database() {
  local app_name="$1"
  local db_type="$2"
  local db_password="${3:-secret}"
  print_info "Creating database and user for webapp '$app_name'..."
  case "$db_type" in
    postgres)
      create_postgres_webapp_database "$app_name" "$db_password"
      ;;
    mariadb)
      create_mariadb_webapp_database "$app_name" "$db_password"
      ;;
    sqlite)
      create_sqlite_webapp_database "$app_name"
      ;;
    *)
      print_warn "Database creation not implemented for type: $db_type"
      ;;
  esac
}

create_postgres_webapp_database() {
  local app_name="$1"
  local db_password="$2"
  local container_name=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  local admin_password=$(grep POSTGRES_PASSWORD .env | cut -d= -f2)
  export PGPASSWORD="$admin_password"

  # Check if user already exists
  if psql -h "$container_name" -p 5432 -U postgres -d postgres -t -c "SELECT 1 FROM pg_roles WHERE rolname='${app_name}';" 2>/dev/null | grep -q 1; then
    unset PGPASSWORD
    print_error "User '${app_name}' already exists"
    return 1
  fi

  # Create user, database and grant privileges (separate commands)
  if psql -h "$container_name" -p 5432 -U postgres -d postgres -c "CREATE USER ${app_name} WITH PASSWORD '${db_password}';" > /dev/null 2>&1 && \
     psql -h "$container_name" -p 5432 -U postgres -d postgres -c "CREATE DATABASE ${app_name} OWNER ${app_name};" > /dev/null 2>&1 && \
     psql -h "$container_name" -p 5432 -U postgres -d postgres -c "GRANT ALL PRIVILEGES ON DATABASE ${app_name} TO ${app_name};" > /dev/null 2>&1; then
    unset PGPASSWORD
    print_info "PostgreSQL database '$app_name' created successfully"
    print_info "Database: $app_name | User: $app_name | Password: ${db_password}"
  else
    unset PGPASSWORD
    print_error "Failed to create PostgreSQL database '$app_name'"
    return 1
  fi
}

create_mariadb_webapp_database() {
  local app_name="$1"
  local db_password="$2"
  local container_name=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  local root_password=$(grep MARIADB_ROOT_PASSWORD .env | cut -d= -f2)

  # Check if mysql is available
  if ! command -v mysql > /dev/null 2>&1; then
    print_error "mysql not available. Install MariaDB/MySQL client"
    return 1
  fi

  print_info "Creating MariaDB database '$app_name' with user '$app_name'..."

  # Test connection first
  if ! mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "SELECT 1;" > /dev/null 2>&1; then
    print_error "Cannot connect to MariaDB at '$container_name:3306'"
    print_error "Make sure MariaDB container is running: ./install.sh --mariadb"
    return 1
  fi

  # Create database, user and grant privileges (separate commands)
  if mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "CREATE DATABASE IF NOT EXISTS ${app_name};" 2>/dev/null && \
     mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "CREATE USER IF NOT EXISTS '${app_name}'@'%' IDENTIFIED BY '${db_password}';" 2>/dev/null && \
     mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "GRANT ALL PRIVILEGES ON ${app_name}.* TO '${app_name}'@'%';" 2>/dev/null && \
     mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "FLUSH PRIVILEGES;" 2>/dev/null; then
    print_info "MariaDB setup complete for webapp '$app_name'"
    print_info "Database: $app_name | User: $app_name | Password: ${db_password}"
  else
    print_error "Failed to create MariaDB database '$app_name'"
    return 1
  fi
}

create_sqlite_webapp_database() {
  local app_name="$1"
  local sqlite_data_dir=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)

  # Check if sqlite3 is available
  if ! command -v sqlite3 > /dev/null 2>&1; then
    print_warn "sqlite3 not available, skipping database initialization"
    return 0
  fi

  print_info "Creating SQLite database for webapp '$app_name'..."
  # Create directory if it doesn't exist
  mkdir -p "${sqlite_data_dir}"
  # Create and initialize database with a simple test table
  local sqlite_path="${sqlite_data_dir}/${app_name}.sqlite"
  sqlite3 "$sqlite_path" "CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY, name TEXT);" 2>/dev/null
  print_info "SQLite setup complete for webapp '$app_name'"
  print_info "Database: ${app_name}.sqlite | Location: ${sqlite_data_dir}/${app_name}.sqlite"
}

remove_postgres_webapp_database() {
  local app_name="$1"
  local container_name=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
  local admin_password=$(grep POSTGRES_PASSWORD .env | cut -d= -f2)
  local port=$(grep POSTGRES_PORT .env | cut -d= -f2)

  # Check if PostgreSQL is reachable
  if ! command -v psql > /dev/null 2>&1; then
    print_warn "psql not available, skipping database removal"
    return 0
  fi

  # Try to connect using container name on the network
  export PGPASSWORD="$admin_password"
  if psql -h "$container_name" -p 5432 -U postgres -d postgres -c "SELECT 1;" > /dev/null 2>&1; then
    # Terminate active connections
    psql -h "$container_name" -p 5432 -U postgres -d postgres -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '$app_name';" > /dev/null 2>&1 || true
    # Drop database and user separately (DROP DATABASE cannot run in transaction block)
    psql -h "$container_name" -p 5432 -U postgres -d postgres -c "DROP DATABASE IF EXISTS $app_name;" > /dev/null 2>&1 || true
    psql -h "$container_name" -p 5432 -U postgres -d postgres -c "DROP USER IF EXISTS $app_name;" > /dev/null 2>&1 || true
    print_info "PostgreSQL database '$app_name' removed"
  else
    print_warn "PostgreSQL not reachable, skipping database removal"
  fi
  unset PGPASSWORD
}

remove_mariadb_webapp_database() {
  local app_name="$1"
  local container_name=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
  local root_password=$(grep MARIADB_ROOT_PASSWORD .env | cut -d= -f2)

  # Check if mysql is available
  if ! command -v mysql > /dev/null 2>&1; then
    print_warn "mysql not available, skipping database removal"
    return 0
  fi

  # Try to connect using container name on the network
  if mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "SELECT 1;" > /dev/null 2>&1; then
    # Drop database and user separately
    mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "DROP DATABASE IF EXISTS $app_name;" > /dev/null 2>&1 || true
    mysql -h "$container_name" -P 3306 -u root -p"$root_password" -e "DROP USER IF EXISTS '$app_name'@'%';" > /dev/null 2>&1 || true
    print_info "MariaDB database '$app_name' removed"
  else
    print_warn "MariaDB not reachable, skipping database removal"
  fi
}

remove_sqlite_webapp_database() {
  local app_name="$1"
  local sqlite_data_dir=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)
  print_info "Removing SQLite database for webapp '$app_name'..."
  # Remove SQLite database file
  if [ -f "${sqlite_data_dir}/${app_name}.sqlite" ]; then
    rm -f "${sqlite_data_dir}/${app_name}.sqlite"
    print_info "Removed database file: ${sqlite_data_dir}/${app_name}.sqlite"
  else
    print_info "Database file not found: ${sqlite_data_dir}/${app_name}.sqlite"
  fi
  print_info "SQLite cleanup complete for webapp '$app_name'"
}

# Initialize database with sample data from SQL file
initialize_database_data() {
  local app_name="$1"
  local db_type="$2"
  local group_id="$3"

  local sql_file="projects/$group_id/database/init-data-${db_type}.sql"

  # Check if SQL initialization file exists
  if [ ! -f "$sql_file" ]; then
    print_info "No initialization SQL file found for $db_type, skipping data load"
    return 0
  fi

  print_info "Loading initial data from $sql_file..."

  case "$db_type" in
    postgres)
      local container_name=$(grep POSTGRES_CONTAINER_NAME .env | cut -d= -f2)
      export PGPASSWORD="secret"
      if psql -h "$container_name" -p 5432 -U "$app_name" -d "$app_name" -f "$sql_file" > /dev/null 2>&1; then
        print_info "PostgreSQL initial data loaded successfully"
      else
        print_warn "Failed to load initial data into PostgreSQL"
      fi
      unset PGPASSWORD
      ;;

    mariadb)
      local container_name=$(grep MARIADB_CONTAINER_NAME .env | cut -d= -f2)
      if mysql -h "$container_name" -P 3306 -u "$app_name" -psecret "$app_name" < "$sql_file" 2>/dev/null; then
        print_info "MariaDB initial data loaded successfully"
      else
        print_warn "Failed to load initial data into MariaDB"
      fi
      ;;

    sqlite)
      local sqlite_data_dir=$(grep SQLITE_DATA_DIR .env | cut -d= -f2)
      local sqlite_path="${sqlite_data_dir}/${app_name}.sqlite"
      if sqlite3 "$sqlite_path" < "$sql_file" 2>/dev/null; then
        print_info "SQLite initial data loaded successfully"
      else
        print_warn "Failed to load initial data into SQLite"
      fi
      ;;
  esac
}

# Create new Maven webapp
create_webapp() {
  local app_name="$1"
  local db_type="$2"
  local group_id="${GROUP_ID:-com.example}"
  if [ -z "$app_name" ]; then
    print_error "Application name is required"
    exit 1
  fi
  if [ -d "projects/$group_id" ]; then
    print_error "Application '$group_id' already exists in projects/ directory"
    exit 1
  fi
  if [ -n "$db_type" ]; then
    # Validate database type
    case "$db_type" in
      postgres|mariadb|sqlite)
        ;;
      *)
        print_error "Unsupported database type: $db_type. Supported: postgres, mariadb, sqlite"
        exit 1
        ;;
    esac
    # Install only the database archetype if needed
    ensure_archetype_installed "tomeex-app-database"
    print_info "Creating webapp '$app_name' (groupId: $group_id) with $db_type database..."
    cd projects || exit 1
    mvn archetype:generate \
      -DgroupId="$group_id" \
      -DartifactId="$app_name" \
      -DarchetypeGroupId=dev.tomeex.archetypes \
      -DarchetypeArtifactId=tomeex-app-database \
      -DarchetypeVersion=1.0.0 \
      -DdbType="$db_type" \
      -DinteractiveMode=false \
      -DarchetypeCatalog=local \
      -q
    # Rename directory from artifactId to groupId
    if [ -d "$app_name" ] && [ "$app_name" != "$group_id" ]; then
      mv "$app_name" "$group_id"
    fi
    cd ..
  else
    # Install only the simple webapp archetype if needed
    ensure_archetype_installed "tomeex-app"
    print_info "Creating webapp '$app_name' (groupId: $group_id)..."
    cd projects || exit 1
    mvn archetype:generate \
      -DgroupId="$group_id" \
      -DartifactId="$app_name" \
      -DarchetypeGroupId=dev.tomeex.archetypes \
      -DarchetypeArtifactId=tomeex-app \
      -DarchetypeVersion=1.0.0 \
      -DinteractiveMode=false \
      -DarchetypeCatalog=local \
      -q
    # Rename directory from artifactId to groupId
    if [ -d "$app_name" ] && [ "$app_name" != "$group_id" ]; then
      mv "$app_name" "$group_id"
    fi
    cd ..
  fi
  if [ ! -d "projects/$group_id" ]; then
    print_error "Failed to create webapp '$group_id'"
    exit 1
  fi

  # Extract project version from pom.xml
  local project_version=$(grep -m1 "<version>" "projects/$group_id/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/' | xargs)
  if [ -z "$project_version" ]; then
    project_version="1.0.0"  # Fallback if version extraction fails
  fi

  # Create database and user for webapp if database type is specified
  if [ -n "$db_type" ]; then
    # Read password from .env if exists, otherwise use default 'secret'
    local db_password="secret"
    if [ -f "projects/$group_id/.env" ]; then
      db_password=$(grep "^DB_PASSWORD=" "projects/$group_id/.env" | cut -d= -f2)
      if [ -z "$db_password" ]; then
        db_password="secret"
      fi
    fi
    create_webapp_database "$app_name" "$db_type" "$db_password"
    create_webapp_env_file "$group_id" "$app_name" "$db_type" "$project_version"
    initialize_database_data "$app_name" "$db_type" "$group_id"
  else
    # No database - create .env with empty database configuration
    create_webapp_env_file "$group_id" "$app_name" "" "$project_version"
  fi
  print_info "Created: projects/$group_id/ with artifactId: $app_name"
  # Build and deploy the webapp
  print_info "Building and deploying $app_name..."
  cd "projects/$group_id" || exit 1
  make deploy
  cd - > /dev/null || exit 1
  # Show webapp URLs
  echo ""
  print_info "Deployed: http://localhost:9292/$app_name"
}

remove_webapp() {
  local project_path="$1"
  local artifact_id="$2"
  if [ -z "$project_path" ]; then
    print_error "Project path is required"
    exit 1
  fi
  if [ ! -d "projects/$project_path" ]; then
    print_error "Project '$project_path' not found in projects/ directory"
    exit 1
  fi
  # Extract artifactId from pom.xml if not provided
  if [ -z "$artifact_id" ]; then
    artifact_id=$(grep -m1 "<artifactId>" "projects/$project_path/pom.xml" | sed 's/.*<artifactId>\(.*\)<\/artifactId>.*/\1/' | xargs)
  fi
  if [ -z "$artifact_id" ]; then
    print_error "Could not determine artifactId for webapp removal"
    exit 1
  fi
  local context_xml="projects/$project_path/src/main/resources/META-INF/context.xml"
  if [ -f "$context_xml" ]; then
    if grep -q "postgresql" "$context_xml"; then
      remove_postgres_webapp_database "$artifact_id"
    elif grep -q "mariadb\|mysql" "$context_xml"; then
      remove_mariadb_webapp_database "$artifact_id"
    elif grep -q "sqlite" "$context_xml"; then
      remove_sqlite_webapp_database "$artifact_id"
    fi
  fi
  rm -rf "/usr/local/tomee/webapps/${artifact_id}" 2>/dev/null || true
  rm -f "/usr/local/tomee/webapps/${artifact_id}.war" 2>/dev/null || true
  rm -rf "/usr/local/tomee/work/Catalina/localhost/${artifact_id}" 2>/dev/null || true
  rm -rf "projects/$project_path"
  print_info "Webapp '$project_path' (artifactId: $artifact_id) removed successfully"
}

remove_library() {
  local project_path="$1"
  if [ -z "$project_path" ]; then
    print_error "Project path is required"
    exit 1
  fi
  if [ ! -d "projects/$project_path" ]; then
    print_error "Library '$project_path' not found in projects/ directory"
    exit 1
  fi
  rm -rf "projects/$project_path"
  print_info "Library '$project_path' removed successfully"
}

create_library() {
  local lib_name="$1"
  local with_db="$2"
  local group_id="${GROUP_ID:-com.example}"
  if [ -z "$lib_name" ]; then
    print_error "Library name is required"
    exit 1
  fi
  if [ -d "projects/$group_id" ]; then
    print_error "Library '$group_id' already exists in projects/ directory"
    exit 1
  fi
  if [ "$with_db" = "true" ]; then
    ensure_archetype_installed "tomeex-lib-database"
    print_info "Creating library '$lib_name' (groupId: $group_id) with multi-database support..."
    cd projects || exit 1
    mvn archetype:generate \
      -DgroupId="$group_id" \
      -DartifactId="$lib_name" \
      -DarchetypeGroupId=dev.tomeex.archetypes \
      -DarchetypeArtifactId=tomeex-lib-database \
      -DarchetypeVersion=1.0.0 \
      -DinteractiveMode=false \
      -DarchetypeCatalog=local \
      -q
    # Rename directory from artifactId to groupId
    if [ -d "$lib_name" ] && [ "$lib_name" != "$group_id" ]; then
      mv "$lib_name" "$group_id"
    fi
    cd ..
  else
    ensure_archetype_installed "tomeex-lib"
    print_info "Creating library '$lib_name' (groupId: $group_id)..."
    cd projects || exit 1
    mvn archetype:generate \
      -DgroupId="$group_id" \
      -DartifactId="$lib_name" \
      -DarchetypeGroupId=dev.tomeex.archetypes \
      -DarchetypeArtifactId=tomeex-lib \
      -DarchetypeVersion=1.0.0 \
      -DinteractiveMode=false \
      -DarchetypeCatalog=local \
      -q
    # Rename directory from artifactId to groupId
    if [ -d "$lib_name" ] && [ "$lib_name" != "$group_id" ]; then
      mv "$lib_name" "$group_id"
    fi
    cd ..
  fi
  if [ ! -d "projects/$group_id" ]; then
    print_error "Failed to create library '$group_id'"
    exit 1
  fi
  print_info "Created: projects/$group_id/ with artifactId: $lib_name"
}

install_libs_from_workspace() {
  local CONTAINER_NAME=$(grep "^CONTAINER_NAME=" .env | cut -d= -f2)
  local lib_dir="/workspace/lib"
  # Check if container is running
  if ! docker ps --format 'table {{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    print_warn "Container ${CONTAINER_NAME} is not running, skipping library installation"
    return 0
  fi
  # Check if lib directory exists and count JAR files in a single call
  local jar_count=$(docker exec "${CONTAINER_NAME}" sh -c "
    [ -d '$lib_dir' ] || exit 0
    find '$lib_dir' -maxdepth 1 -name '*.jar' ! -name '*-sources.jar' ! -name '*-javadoc.jar' 2>/dev/null | wc -l
  ")
  if [ "$jar_count" -eq 0 ]; then
    return 0
  fi
  print_info "Installing $jar_count JAR libraries from /workspace/lib to Maven local repository..."
  # Execute entire installation process in a single docker exec call
  docker exec "${CONTAINER_NAME}" bash -c '
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
lib_dir="/workspace/lib"

# Find all main JAR files
find "$lib_dir" -maxdepth 1 -name "*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" 2>/dev/null | while IFS= read -r jar_file; do
  # Create temp directory for extraction
  temp_dir=$(mktemp -d)
  cd "$temp_dir"

  # Extract META-INF directory using jar command
  jar -xf "$jar_file" META-INF/ 2>/dev/null || continue

  # Find pom.properties file
  pom_props_file=$(find META-INF/maven -name "pom.properties" 2>/dev/null | head -1)

  if [ -n "$pom_props_file" ] && [ -f "$pom_props_file" ]; then
    # Extract Maven coordinates from pom.properties
    groupId=$(grep "^groupId=" "$pom_props_file" | cut -d= -f2 | tr -d "\r\n ")
    artifactId=$(grep "^artifactId=" "$pom_props_file" | cut -d= -f2 | tr -d "\r\n ")
    version=$(grep "^version=" "$pom_props_file" | cut -d= -f2 | tr -d "\r\n ")

    if [ -n "$groupId" ] && [ -n "$artifactId" ] && [ -n "$version" ]; then
      echo "[info] Installing $artifactId-$version.jar..."

      # Install main JAR
      if mvn install:install-file \
        -Dfile="$jar_file" \
        -DgroupId="$groupId" \
        -DartifactId="$artifactId" \
        -Dversion="$version" \
        -Dpackaging=jar \
        -DgeneratePom=true \
        -DcreateChecksum=true \
        -q 2>/dev/null; then

        # Get base name for sources and javadoc
        base_name=$(basename "$jar_file" .jar)

        # Install sources JAR if exists
        sources_jar="$lib_dir/${base_name}-sources.jar"
        if [ -f "$sources_jar" ]; then
          echo "[info] Installing $artifactId-$version-sources.jar..."
          mvn install:install-file \
            -Dfile="$sources_jar" \
            -DgroupId="$groupId" \
            -DartifactId="$artifactId" \
            -Dversion="$version" \
            -Dpackaging=jar \
            -Dclassifier=sources \
            -DgeneratePom=false \
            -DcreateChecksum=true \
            -q 2>/dev/null
        fi

        # Install javadoc JAR if exists
        javadoc_jar="$lib_dir/${base_name}-javadoc.jar"
        if [ -f "$javadoc_jar" ]; then
          echo "[info] Installing $artifactId-$version-javadoc.jar..."
          mvn install:install-file \
            -Dfile="$javadoc_jar" \
            -DgroupId="$groupId" \
            -DartifactId="$artifactId" \
            -Dversion="$version" \
            -Dpackaging=jar \
            -Dclassifier=javadoc \
            -DgeneratePom=false \
            -DcreateChecksum=true \
            -q 2>/dev/null
        fi
      else
        echo "[warn] Failed to install $jar_file"
      fi
    else
      echo "[warn] Could not extract Maven coordinates from $jar_file"
    fi
  else
    echo "[warn] No pom.properties found in $jar_file, skipping installation"
  fi

  # Cleanup temp directory and return to original directory
  cd /
  rm -rf "$temp_dir" 2>/dev/null || true
done
'
  print_info "Library installation from /workspace/lib completed"
}

parse_args() {
  while [ $# -gt 0 ]; do
    case $1 in
      --postgres)
        INSTALL_POSTGRES=true
        shift
        ;;
      --mariadb)
        INSTALL_MARIADB=true
        shift
        ;;
      --sqlite)
        INSTALL_SQLITE=true
        shift
        ;;
      --claude)
        INSTALL_CLAUDE=true
        shift
        ;;
      --purge-postgres)
        PURGE_POSTGRES=true
        shift
        ;;
      --purge-mariadb)
        PURGE_MARIADB=true
        shift
        ;;
      --create-webapp)
        if [ -z "$2" ]; then
          print_error "--create-webapp requires an application name"
          exit 1
        fi
        CREATE_WEBAPP="$2"
        shift 2
        ;;
      --create-library)
        if [ -z "$2" ]; then
          print_error "--create-library requires a library name"
          exit 1
        fi
        CREATE_LIBRARY="$2"
        shift 2
        ;;
      --remove-webapp)
        if [ -z "$2" ]; then
          print_error "--remove-webapp requires an application name"
          exit 1
        fi
        REMOVE_WEBAPP="$2"
        shift 2
        ;;
      --remove-library)
        if [ -z "$2" ]; then
          print_error "--remove-library requires a library name"
          exit 1
        fi
        REMOVE_LIBRARY="$2"
        shift 2
        ;;
      --database)
        if [ -z "$2" ]; then
          print_error "--database requires a database type (postgres, mariadb, sqlite)"
          exit 1
        fi
        DATABASE_TYPE="$2"
        shift 2
        ;;
      --setup-db)
        if [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
          print_error "--setup-db requires: <db_name> <db_type> <db_password>"
          exit 1
        fi
        SETUP_DB_NAME="$2"
        SETUP_DB_TYPE="$3"
        SETUP_DB_PASSWORD="$4"
        shift 4
        ;;
      --with-database)
        WITH_DATABASE=true
        shift
        ;;
      --groupid)
        if [ -z "$2" ]; then
          print_error "--groupid requires a groupId value"
          exit 1
        fi
        GROUP_ID="$2"
        shift 2
        ;;
      --artifactid)
        if [ -z "$2" ]; then
          print_error "--artifactid requires an artifactId value"
          exit 1
        fi
        ARTIFACT_ID="$2"
        shift 2
        ;;
      --help|-h)
        show_usage
        exit 0
        ;;
      *)
        print_error "Unknown option: $1"
        show_usage
        exit 1
        ;;
    esac
  done
}

show_usage() {
  echo "Usage: $0 [OPTIONS]"
  echo ""
  echo "Sets up TomEEx development environment with Docker container."
  echo ""
  echo "Options:"
  echo "  --postgres               Also install and start PostgreSQL container"
  echo "  --mariadb                Also install and start MariaDB container"
  echo "  --sqlite                 Also install SQLite3 in TomEE container"
  echo "  --claude                 Install Claude Code with NVM and Node.js 18"
  echo "  --purge-postgres         Stop and remove PostgreSQL container and volume"
  echo "  --purge-mariadb          Stop and remove MariaDB container and volume"
  echo "  --create-webapp  <name>  Create new Maven webapp with Makefile and README"
  echo "  --create-library <name>  Create new JAR library with Makefile and README"
  echo "  --remove-webapp  <name>  Remove webapp and associated database"
  echo "  --remove-library <name>  Remove JAR library"
  echo "  --database       <type>  Add database support (postgres, mariadb, sqlite)"
  echo "  --help, -h               Show this help"
  echo ""
}

main() {
  # Parse command line arguments
  parse_args "$@"
  if [ "$PURGE_POSTGRES" = "true" ]; then
    purge_postgres
    exit 0
  fi
  if [ "$PURGE_MARIADB" = "true" ]; then
    purge_mariadb
    exit 0
  fi
  if [ -n "$SETUP_DB_NAME" ]; then
    create_webapp_database "$SETUP_DB_NAME" "$SETUP_DB_TYPE" "$SETUP_DB_PASSWORD"
    exit 0
  fi
  if [ -n "$CREATE_WEBAPP" ]; then
    create_webapp "$CREATE_WEBAPP" "$DATABASE_TYPE"
    exit 0
  fi
  if [ -n "$REMOVE_WEBAPP" ]; then
    remove_webapp "$REMOVE_WEBAPP" "$ARTIFACT_ID"
    exit 0
  fi
  if [ -n "$CREATE_LIBRARY" ]; then
    create_library "$CREATE_LIBRARY" "$WITH_DATABASE"
    exit 0
  fi
  if [ -n "$REMOVE_LIBRARY" ]; then
    remove_library "$REMOVE_LIBRARY"
    exit 0
  fi
  print_header "TomEEx Environment Setup"
  echo ""
  #Check if .env exists - if not, create it and exit
  if [ ! -f ".env" ]; then
    print_info ".env file not found, creating configuration file..."
    create_env_file
    echo ""
    print_info "The .env file has been created."
    print_warn "Run './install.sh' again to continue."
    return 0
  fi
  # .env exists, proceed with full installation
  check_docker
  create_basic_directories
  create_gitignore
  create_projects_folder
  create_docker_network
  pull_tomee_image
  copy_default_config
  create_tomee_config
  start_container
  wait_for_tomee
  install_dev_tools
  configure_git
  configure_shell_aliases
  install_libs_from_workspace
  echo ""
  print_info "=== Setup Complete ==="
  HOST_PORT=$(grep HOST_PORT .env | cut -d= -f2)
  print_info "Container: $(grep "^CONTAINER_NAME=" .env | cut -d= -f2) running"
  # Installation completed - no more automatic webapp setup
  echo ""
  print_info "Installation completed successfully"
  echo ""
  echo "TomEE Manager URLs:"
  echo "  http://localhost:${HOST_PORT}/manager/html"
  echo "  http://localhost:${HOST_PORT}/host-manager/html"
  echo ""
  echo "Generate webapps with:"
  echo "  make app name=<app_name>           # Simple webapp"
  echo "  make app name=<app_name> db=<type> # Database webapp"
  echo ""
  ADMIN_USER=$(grep ADMIN_USER .env | cut -d= -f2)
  ADMIN_PASSWORD=$(grep ADMIN_PASSWORD .env | cut -d= -f2)
  MANAGER_USER=$(grep MANAGER_USER .env | cut -d= -f2)
  MANAGER_PASSWORD=$(grep MANAGER_PASSWORD .env | cut -d= -f2)
  echo "Login: $ADMIN_USER / $ADMIN_PASSWORD (or $MANAGER_USER / $MANAGER_PASSWORD)"
  # Setup databases if requested
  if [ "$INSTALL_POSTGRES" = "true" ]; then
    setup_postgres
  fi
  if [ "$INSTALL_MARIADB" = "true" ]; then
    setup_mariadb
  fi
  if [ "$INSTALL_SQLITE" = "true" ]; then
    setup_sqlite
  fi
  if [ "$INSTALL_CLAUDE" = "true" ]; then
    install_claude_code
  fi
}

main "$@"
