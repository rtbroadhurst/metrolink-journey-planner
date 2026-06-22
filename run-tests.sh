#!/usr/bin/env bash
# Compiles the project and runs the JUnit 5 test suite.
# Fetches the JUnit console-standalone jar on first run (single dependency, cached in lib/).
set -euo pipefail

JUNIT_VERSION="1.10.2"
JAR="lib/junit-platform-console-standalone-${JUNIT_VERSION}.jar"
URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar"

mkdir -p lib out test-out

if [ ! -f "$JAR" ]; then
  echo "Downloading JUnit ${JUNIT_VERSION}..."
  curl -fsSL -o "$JAR" "$URL"
fi

echo "Compiling main sources..."
javac -d out $(find src -name "*.java")

echo "Compiling tests..."
javac -cp "out:${JAR}" -d test-out $(find test -name "*.java")

echo "Running tests..."
java -jar "$JAR" --class-path "out:test-out" --scan-class-path --details=tree --disable-banner
