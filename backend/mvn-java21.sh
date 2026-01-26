#!/bin/bash
# Maven wrapper script to ensure Java 21 is used
# Usage: ./mvn-java21.sh [maven arguments]
#
# This script sets JAVA_HOME to Java 21 before running Maven.
# Maven will use this Java version for compilation, tests, and all operations.

JAVA_21_HOME="/opt/homebrew/Cellar/openjdk@21/21.0.10/libexec/openjdk.jdk/Contents/Home"
MVN_SCRIPT="/opt/homebrew/Cellar/maven/3.9.12/libexec/bin/mvn"

if [ ! -d "$JAVA_21_HOME" ]; then
    echo "Error: Java 21 not found at $JAVA_21_HOME"
    echo "Please update the JAVA_21_HOME path in this script."
    exit 1
fi

if [ ! -f "$MVN_SCRIPT" ]; then
    echo "Error: Maven script not found at $MVN_SCRIPT"
    echo "Please update the MVN_SCRIPT path in this script."
    exit 1
fi

export JAVA_HOME="$JAVA_21_HOME"
# Add Java 21 to PATH but keep original PATH intact
export PATH="$JAVA_21_HOME/bin:$PATH"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Maven with Java 21"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Java Home: $JAVA_HOME"
echo "Java Version: $($JAVA_21_HOME/bin/java -version 2>&1 | head -1)"
echo "Maven Command: $MVN_SCRIPT $*"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Call Maven script directly, bypassing the wrapper that sets JAVA_HOME
exec "$MVN_SCRIPT" "$@"
