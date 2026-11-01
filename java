#!/bin/sh
# Project JDK wrapper: runs java from the path in .mvn/jdk-home
jdk_home="$(head -n 1 "$(dirname "$0")/.mvn/jdk-home" | tr -d '\r')"
case "$(uname)" in
CYGWIN* | MINGW* | MSYS*) jdk_home="$(cygpath --unix "$jdk_home")" ;;
esac
exec "$jdk_home/bin/java" "$@"
