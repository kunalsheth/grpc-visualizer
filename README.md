# grpc-visualizer
CLI to visually inspect gRPC's `.proto` config files.
 
![Demo GIF](./demo/demo.gif)
(`>>>` indicates a streaming RPC call, `──>` indicates a singular value.)

## Directional Graphs  
Input:  
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
Command: `digraph svg`  
Output:  
![Demo Digraph](./demo/digraph.svg)

## Cyclic Dependency Detection
Input:  
```proto
message A {
    B aHasB = 1;
    C aAlsoHasC = 2;
}
message B {
    D bHasD = 1;
}
message C {
    D cHasD = 1;
}
message D {
    A dMischievouslyHasA = 1;
}
```
Command: `digraph svg A`  
Output:  
![Demo Cyclic Dependency Detection](./demo/huans_first_drawing_AFTER.svg)
