#!/bin/bash

set -e

APP_DIR="/home/ubuntu/app"
COMPOSE_FILE="$APP_DIR/docker-compose.dev.yml"
DOCKER_USERNAME="${DOCKER_USERNAME:-}"
IMAGE_TAG="${IMAGE_TAG:-latest}"
LETSENCRYPT_EMAIL="${LETSENCRYPT_EMAIL:-admin@mykku.kr}"

echo "=== MyKKU Docker Deployment ==="
echo "Image: ${DOCKER_USERNAME}/mykku-be:${IMAGE_TAG}"
echo "Compose file: ${COMPOSE_FILE}"

if [ -z "$DOCKER_USERNAME" ]; then
    echo "Error: DOCKER_USERNAME is not set"
    exit 1
fi

if [ ! -f "$COMPOSE_FILE" ]; then
    echo "Error: docker-compose.dev.yml not found at $COMPOSE_FILE"
    exit 1
fi

export DOCKER_USERNAME
export IMAGE_TAG
export LETSENCRYPT_EMAIL

echo "Pulling new app image..."
docker pull "${DOCKER_USERNAME}/mykku-be:${IMAGE_TAG}"

echo "Stopping app container with graceful shutdown..."
if docker ps -q -f name=mykku-app | grep -q .; then
    docker stop --time=30 mykku-app || true
fi

echo "Removing old app container..."
docker rm -f mykku-app 2>/dev/null || true

echo "Starting/updating all services..."
cd "$APP_DIR"
docker compose -f "$COMPOSE_FILE" up -d

echo "Waiting for application to be healthy..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    HTTP_STATUS=$(curl -sk -o /dev/null -w "%{http_code}" https://api.dev.mykku.kr/actuator/health/liveness 2>/dev/null || echo "000")
    if [ "$HTTP_STATUS" = "200" ]; then
        echo "Application is healthy via nginx-proxy! (HTTPS $HTTP_STATUS)"
        break
    fi

    DIRECT_STATUS=$(docker exec mykku-app wget -q --spider http://localhost:8080/actuator/health/liveness 2>/dev/null && echo "200" || echo "000")
    if [ "$DIRECT_STATUS" = "200" ]; then
        echo "App container is healthy. Waiting for nginx-proxy routing... (HTTPS $HTTP_STATUS)"
    else
        echo "Waiting for health check... ($((RETRY_COUNT + 1))/$MAX_RETRIES) - HTTPS: $HTTP_STATUS"
    fi

    sleep 5
    RETRY_COUNT=$((RETRY_COUNT + 1))
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "Warning: Health check timeout. Check container logs."
    echo "=== mykku-app logs ==="
    docker logs mykku-app --tail 30
    echo "=== nginx-proxy logs ==="
    docker logs nginx-proxy --tail 20 2>/dev/null || echo "nginx-proxy not running"
fi

echo "Cleaning up old images..."
docker image prune -f --filter "until=24h"

echo "=== Deployment Complete ==="
echo "Container status:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "To view logs:"
echo "  App: docker logs -f mykku-app"
echo "  Nginx: docker logs -f nginx-proxy"
echo "  SSL: docker logs -f letsencrypt"
echo "  File: tail -f /home/ubuntu/app/logs/mykku.log"
