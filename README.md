# grpc-visualizer
CLI to visually inspect gRPC's `.proto` config files.
 
![Demo GIF](https://raw.githubusercontent.com/kunalsheth/grpc-visualizer/master/demo/demo.gif)
(`>>>` indicates a streaming RPC call, `──>` indicates a singular value.)

grpc-visualizer can also generate directed graphs representing the structure of message data types.
For example:
> ```proto
syntax = "proto3";

message Point {
  int32 latitude = 1;
  int32 longitude = 2;
}
message Rectangle {
  Point lo = 1;
  Point hi = 2;
}
message Feature {
  string name = 1;
  Point location = 2;
}
message FeatureDatabase {
  repeated Feature feature = 1;
}
message RouteNote {
  Point location = 1;
  string message = 2;
}
message RouteSummary {
  // several int32s
}
```

gets transformed into:
> ![Demo Digraph](https://raw.githubusercontent.com/kunalsheth/grpc-visualizer/master/demo/digraph.svg)


take a look at the [`samples`](https://github.com/kunalsheth/grpc-visualizer/blob/master/samples/) folder for testing input data.
`samples/` taken from the [gRPC java example repo](github.com/grpc/grpc-java/tree/master/examples/src/main/proto)