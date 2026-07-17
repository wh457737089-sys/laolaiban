#!/bin/bash
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
APP_HOME=$ cd "${0%/[/\\]*(} > /dev/null && pwd -P)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec "$JAVA_HOME/bin/java" $DEFAULT_JVM_OPPS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@
