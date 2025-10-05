#!/bin/bash
set -e

# ${artifactId} Production Deployment Script
# Deploys application to Docker with TomEE

APP_NAME="${artifactId}"
CONTAINER_NAME="${artifactId}-prod"
WAR_FILE="${artifactId}.war"
TOMEE_IMAGE="tomee:9-jre17-plume"
HOST_PORT=8080
DOCKER_NETWORK=""

# Usage profiles
USAGE_PROFILE="medium"

# Resource configurations
declare -A MEMORY_LIMITS=(
    [low]="700m"
    [medium]="1500m"
    [high]="4g"
)

declare -A MEMORY_SWAP=(
    [low]="700m"
    [medium]="1500m"
    [high]="4g"
)

declare -A XMS=(
    [low]="128m"
    [medium]="256m"
    [high]="512m"
)

declare -A XMX=(
    [low]="512m"
    [medium]="1024m"
    [high]="3072m"
)

declare -A METASPACE=(
    [low]="128m"
    [medium]="256m"
    [high]="512m"
)

# Show help
show_help() {
    cat << EOF
${artifactId} Production Deployment

USAGE:
    ./install.sh [OPTIONS]

OPTIONS:
    --usage <profile>    Set resource usage profile (default: medium)
                         Profiles: low, medium, high
    --port <port>        Set host port (default: 8080)
    --net <network>      Use specific Docker network (optional)
    --help              Show this help message

USAGE PROFILES:

  low (700MB RAM)
    - Memory: 700MB
    - Heap: 128-512MB
    - Metaspace: 128MB
    - Suitable for: VPS with 1-2GB RAM, light traffic

  medium (1.5GB RAM) [DEFAULT]
    - Memory: 1.5GB
    - Heap: 256MB-1GB
    - Metaspace: 256MB
    - Suitable for: VPS with 4GB RAM, moderate traffic

  high (4GB RAM)
    - Memory: 4GB
    - Heap: 512MB-3GB
    - Metaspace: 512MB
    - Suitable for: Dedicated server 8GB+ RAM, high traffic

EXAMPLES:
    ./install.sh                                    # Deploy with medium profile
    ./install.sh --usage low                        # Deploy with low profile
    ./install.sh --usage high --port 9090           # Deploy on port 9090
    ./install.sh --net myapp-net                    # Deploy on custom network
    ./install.sh --usage medium --port 8080 --net myapp-net

REQUIREMENTS:
    - Docker installed and running
    - Port 8080 available (or specify --port)
    - WAR file: ${WAR_FILE}

CONTAINER:
    Name: ${CONTAINER_NAME}
    Image: ${TOMEE_IMAGE}
    URL: http://localhost:8080/

EOF
}

# Parse arguments
while [[ \$# -gt 0 ]]; do
    case \$1 in
        --usage)
            USAGE_PROFILE="\$2"
            if [[ ! "\${MEMORY_LIMITS[\$USAGE_PROFILE]}" ]]; then
                echo "Error: Invalid usage profile '\$USAGE_PROFILE'"
                echo "Valid profiles: low, medium, high"
                exit 1
            fi
            shift 2
            ;;
        --port)
            HOST_PORT="\$2"
            shift 2
            ;;
        --net)
            DOCKER_NETWORK="\$2"
            shift 2
            ;;
        --help)
            show_help
            exit 0
            ;;
        *)
            echo "Error: Unknown option '\$1'"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Get configuration for selected profile
MEMORY="\${MEMORY_LIMITS[\$USAGE_PROFILE]}"
SWAP="\${MEMORY_SWAP[\$USAGE_PROFILE]}"
XMS_VAL="\${XMS[\$USAGE_PROFILE]}"
XMX_VAL="\${XMX[\$USAGE_PROFILE]}"
META_VAL="\${METASPACE[\$USAGE_PROFILE]}"

# Validate Docker
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed"
    echo "Please install Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

if ! docker info &> /dev/null; then
    echo "Error: Docker daemon is not running"
    echo "Please start Docker and try again"
    exit 1
fi

# Validate WAR file
if [ ! -f "\$WAR_FILE" ]; then
    echo "Error: WAR file not found: \$WAR_FILE"
    echo "Expected file in current directory"
    exit 1
fi

# Validate Docker network if specified
if [ -n "\$DOCKER_NETWORK" ]; then
    if ! docker network inspect "\$DOCKER_NETWORK" >/dev/null 2>&1; then
        echo "Error: Docker network '\$DOCKER_NETWORK' does not exist"
        echo "Create it with: docker network create \$DOCKER_NETWORK"
        exit 1
    fi
fi

# Check if container already exists
if docker ps -a --format '{{.Names}}' | grep -q "^\${CONTAINER_NAME}\$"; then
    echo "Container '\${CONTAINER_NAME}' already exists"
    echo -n "Remove existing container? [y/N] "
    read -r response
    if [[ "\$response" =~ ^[Yy]\$ ]]; then
        echo "Stopping and removing existing container..."
        docker stop "\$CONTAINER_NAME" 2>/dev/null || true
        docker rm "\$CONTAINER_NAME" 2>/dev/null || true
    else
        echo "Deployment cancelled"
        exit 1
    fi
fi

# Check if port is in use
if command -v lsof &> /dev/null && lsof -Pi :\${HOST_PORT} -sTCP:LISTEN -t >/dev/null 2>&1; then
    echo "Warning: Port \${HOST_PORT} is already in use"
    echo "Use --port to specify a different port"
    exit 1
fi

# Pull Docker image
echo "Checking Docker image: \${TOMEE_IMAGE}"
if ! docker image inspect "\$TOMEE_IMAGE" >/dev/null 2>&1; then
    echo "Pulling TomEE image (this may take a few minutes)..."
    docker pull "\$TOMEE_IMAGE" || {
        echo "Error: Failed to pull Docker image"
        exit 1
    }
else
    echo "Image already present"
fi

# Prepare network argument
NETWORK_ARG=""
if [ -n "\$DOCKER_NETWORK" ]; then
    NETWORK_ARG="--network \$DOCKER_NETWORK"
fi

# Deploy
echo ""
echo "==================================================================="
echo "  Deploying \${APP_NAME}"
echo "==================================================================="
echo "  Profile:     \${USAGE_PROFILE}"
echo "  Memory:      \${MEMORY}"
echo "  Heap:        \${XMS_VAL} - \${XMX_VAL}"
echo "  Metaspace:   \${META_VAL}"
echo "  Port:        \${HOST_PORT}"
echo "  Container:   \${CONTAINER_NAME}"
if [ -n "\$DOCKER_NETWORK" ]; then
    echo "  Network:     \${DOCKER_NETWORK}"
fi
echo "==================================================================="
echo ""

docker run -d \
  --name "\$CONTAINER_NAME" \
  \$NETWORK_ARG \
  -p \${HOST_PORT}:8080 \
  --memory="\$MEMORY" \
  --memory-swap="\$SWAP" \
  --restart=unless-stopped \
  -e CATALINA_OPTS="-Xms\${XMS_VAL} -Xmx\${XMX_VAL} -XX:MaxMetaspaceSize=\${META_VAL} -XX:+UseG1GC -server" \
  -v "\$(pwd)/\${WAR_FILE}:/usr/local/tomee/webapps/ROOT.war:ro" \
  "\$TOMEE_IMAGE"

echo ""
echo "✓ Deployment successful!"
echo ""
echo "Application URL: http://localhost:\${HOST_PORT}/"
echo "Container name:  \${CONTAINER_NAME}"
echo ""
echo "Wait 10-30 seconds for TomEE to deploy the application"
echo ""
echo "Useful commands:"
echo "  docker logs \${CONTAINER_NAME}           # View logs"
echo "  docker logs -f \${CONTAINER_NAME}        # Follow logs"
echo "  docker stop \${CONTAINER_NAME}           # Stop container"
echo "  docker start \${CONTAINER_NAME}          # Start container"
echo "  docker restart \${CONTAINER_NAME}        # Restart container"
echo "  docker rm -f \${CONTAINER_NAME}          # Remove container"
echo ""
