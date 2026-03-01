#!/bin/bash
# 下载 gradle-wrapper.jar
# 如果无法自动下载，请手动从以下地址下载：
# https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar

GRADLE_WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
OUTPUT_PATH="gradle/wrapper/gradle-wrapper.jar"

echo "Downloading gradle-wrapper.jar..."

if command -v curl &> /dev/null; then
    curl -L -o "$OUTPUT_PATH" "$GRADLE_WRAPPER_URL"
elif command -v wget &> /dev/null; then
    wget -O "$OUTPUT_PATH" "$GRADLE_WRAPPER_URL"
else
    echo "ERROR: Neither curl nor wget found."
    echo "Please download gradle-wrapper.jar manually from:"
    echo "$GRADLE_WRAPPER_URL"
    exit 1
fi

if [ -f "$OUTPUT_PATH" ]; then
    echo "Success! gradle-wrapper.jar downloaded to $OUTPUT_PATH"
else
    echo "ERROR: Failed to download gradle-wrapper.jar"
    exit 1
fi
