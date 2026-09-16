#!/usr/bin/env bash

set -euo pipefail

export SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://localhost:5433/store}"
export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-admin}"

if [[ -z "${SPRING_DATASOURCE_PASSWORD:-}" ]]; then
    read -r -s -p "Database password: " SPRING_DATASOURCE_PASSWORD
    printf '\n'
    export SPRING_DATASOURCE_PASSWORD
fi

exec ./gradlew bootRun
