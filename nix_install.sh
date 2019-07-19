#!/usr/bin/env bash

echo "Building project"
./gradlew clean installDist
echo
echo "Uninstalling previous version from /opt"
sudo rm -r /opt/grpc-visualizer
echo "Installing into /opt"
sudo cp -r build/install/ /opt/

echo
echo -e "\033[1m*\033[0m Make sure the Protobuf Compiler (\033[1mprotoc\033[0m) is installed"
echo -e "\033[1m*\033[0m Make sure Graphviz (\033[1mdot\033[0m) is installed"
echo -e "\033[1m*\033[0m \033[1mAdd /opt/grpc-visualizer/bin/ to your PATH\033[0m if its not there already"
