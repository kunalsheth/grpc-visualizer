# grpc-visualizer
CLI to visually inspect gRPC's `.proto` config files.
 
![Demo GIF](./demo/demo.gif)
(`>>>` indicates a streaming RPC call, `──>` indicates a singular value.)

grpc-visualizer can also generate directed graphs representing the structure of message data types.
For example:
```proto
message RecursiveType {
    // ...
    My code = 4;
}
message My {
    RecursiveType should = 1;
    // ...
    RecursiveType crash = 3;
    My code = 4;
}
message Person {
    // ...
    message PhoneNumber { /* ... */ }
    repeated PhoneNumber phones = 4;
}
message AddressBook {
    repeated Person people = 1;
}
```

gets transformed into:

![Demo Digraph](./demo/digraph.svg)



take a look at the [`samples`](https://github.com/kunalsheth/grpc-visualizer/blob/master/samples/) folder for testing input data.
`samples/` taken from the [gRPC java example repo](github.com/grpc/grpc-java/tree/master/examples/src/main/proto)
