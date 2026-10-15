#!/usr/bin/env bash
set -euo pipefail

apt-get update -qq && apt-get install -y curl unzip java-common

curl -fsSLO https://download.oracle.com/java/27/archive/jdk-27_linux-x64_bin.deb
apt-get install -y ./jdk-27_linux-x64_bin.deb
update-java-alternatives --set jdk-27-oracle-x64

curl -fsSLO https://services.gradle.org/distributions/gradle-9.8.1-bin.zip
unzip -oq gradle-9.8.1-bin.zip -d /opt/gradle
ln -sfn /opt/gradle/gradle-9.8.1/bin/gradle /usr/local/bin/gradle
