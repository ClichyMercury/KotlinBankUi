#!/usr/bin/env bash
# Build, installe et lance FinSim dans l'environnement demandé.
# Usage : scripts/run-android.sh dev|prod
set -euo pipefail

ENV="${1:-dev}"
case "$ENV" in
  dev)  VARIANT="DevDebug";  APP_ID="com.finsim.app.dev"; API="http://10.0.2.2:8080" ;;
  prod) VARIANT="ProdDebug"; APP_ID="com.finsim.app";     API="https://api-finsim.wharpe.com" ;;
  *) echo "Usage: $0 dev|prod" >&2; exit 2 ;;
esac

cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home}"

if [ -z "$(adb devices | sed '1d' | grep -w device || true)" ]; then
  echo "Aucun appareil/émulateur détecté. Démarre un émulateur ou branche un téléphone." >&2
  exit 1
fi

echo "== Environnement : $ENV  ($APP_ID → $API)"
./gradlew ":app:install$VARIANT"
adb shell am start -n "$APP_ID/com.finsim.app.MainActivity" >/dev/null
echo "== Lancé. Logs HTTP + erreurs (Ctrl+C pour arrêter) :"
PID="$(adb shell pidof -s "$APP_ID" | tr -d '\r')"
adb logcat --pid="$PID" -v brief -s System.out:I System.err:W AndroidRuntime:E
