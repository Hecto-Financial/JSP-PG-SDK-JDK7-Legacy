#!/bin/sh
set -eu

JAVA_HOME="${JAVA_HOME:?Set JAVA_HOME to an actual JDK 7 installation}"
BASE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BUILD_DIR="$BASE_DIR/build/test-classes"
LIB_DIR="$BASE_DIR/WebContent/WEB-INF/lib"

rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

"$JAVA_HOME/bin/javac" \
  -source 1.7 \
  -target 1.7 \
  -encoding UTF-8 \
  -cp "$LIB_DIR/*" \
  -d "$BUILD_DIR" \
  "$BASE_DIR/src/com/settle/pg/EncryptUtil.java" \
  "$BASE_DIR/src/com/settle/pg/HttpClientUtil.java" \
  "$BASE_DIR/src/com/settle/pg/StringUtil.java" \
  "$BASE_DIR/src/com/settle/pg/Tls12OrHigherSocketFactory.java" \
  "$BASE_DIR/test/com/settle/pg/EncryptUtilCompatibilityTest.java" \
  "$BASE_DIR/test/com/settle/pg/Tls12OrHigherSocketFactoryTest.java"

"$JAVA_HOME/bin/java" \
  -cp "$BUILD_DIR:$LIB_DIR/*" \
  com.settle.pg.EncryptUtilCompatibilityTest

"$JAVA_HOME/bin/java" \
  -cp "$BUILD_DIR:$LIB_DIR/*" \
  com.settle.pg.Tls12OrHigherSocketFactoryTest
