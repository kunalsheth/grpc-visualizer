#!/usr/bin/env bash

rm -r build/distributions
./gradlew build
cd build/distributions

yes A | unzip grpc-visualizer-0.0.1.zip

cd ../../samples

echo "$ grpc-visualizer helloworld.proto"
../build/distributions/grpc-visualizer-0.0.1/bin/grpc-visualizer helloworld.proto

echo "$ grpc-visualizer hello_streaming.proto"
../build/distributions/grpc-visualizer-0.0.1/bin/grpc-visualizer hello_streaming.proto

echo "$ grpc-visualizer route_guide.proto"
../build/distributions/grpc-visualizer-0.0.1/bin/grpc-visualizer route_guide.proto