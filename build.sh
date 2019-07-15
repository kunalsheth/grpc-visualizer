#!/usr/bin/env bash

rm -r build/distributions
./gradlew build
cd build/distributions

yes A | unzip grpc-visualizer-0.0.1.zip