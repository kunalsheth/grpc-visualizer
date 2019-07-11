# grpc-visualizer

CLI to visually inspect gRPC's `.proto` config files.

<style type="text/css">
pre {
  font-family:Courier New;
  font-size:10pt;
}

.af_line {
  color: gray;
  text-decoration: none;
}
</style>

<pre>
$ grpc-visualizer <a href='https://github.com/kunalsheth/grpc-visualizer/blob/master/samples/helloworld.proto'>helloworld.proto</a>
<span style="color:#00cd00;">___  </span>                                 <span style="color:#cd00cd;">  ___
</span><span style="color:#00cd00;">C  |                                  </span><span style="color:#cd00cd;"> |  S</span>
<span style="color:#00cd00;">L  | </span><span style="font-weight:bold;color:#7f7f7f;">SayHello</span>:                        <span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">I  | </span>   --&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.helloworld.HelloRequest</span>  <span style="color:#cd00cd;"> |  R</span>
<span style="color:#00cd00;">E  | </span>   &lt;-- <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.helloworld.HelloReply</span>    <span style="color:#cd00cd;"> |  V</span>
<span style="color:#00cd00;">N  |                                  </span><span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">T  |                                  </span><span style="color:#cd00cd;"> |  R</span>

$ grpc-visualizer <a href='https://github.com/kunalsheth/grpc-visualizer/blob/master/samples/hello_streaming.proto'>hello_streaming.proto</a>
<span style="color:#00cd00;">___  </span>                                        <span style="color:#cd00cd;">  ___
</span><span style="color:#00cd00;">C  |                                         </span><span style="color:#cd00cd;"> |  S</span>
<span style="color:#00cd00;">L  | </span><span style="font-weight:bold;color:#7f7f7f;">SayHelloStreaming</span>:                      <span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">I  | </span>   &gt;&gt;&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.manualflowcontrol.HelloRequest</span>  <span style="color:#cd00cd;"> |  R</span>
<span style="color:#00cd00;">E  | </span>   &lt;&lt;&lt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.manualflowcontrol.HelloReply</span>    <span style="color:#cd00cd;"> |  V</span>
<span style="color:#00cd00;">N  |                                         </span><span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">T  |                                         </span><span style="color:#cd00cd;"> |  R</span>

$ grpc-visualizer <a href='https://github.com/kunalsheth/grpc-visualizer/blob/master/samples/route_guide.proto'>route_guide.proto</a>
<span style="color:#00cd00;">___  </span>                                 <span style="color:#cd00cd;">  ___
</span><span style="color:#00cd00;">C  |                                  </span><span style="color:#cd00cd;"> |  S</span>
<span style="color:#00cd00;">L  | </span><span style="font-weight:bold;color:#7f7f7f;">GetFeature</span>:                      <span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">I  | </span>   --&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.routeguide.Point</span>         <span style="color:#cd00cd;"> |  R</span>
<span style="color:#00cd00;">E  | </span>   &lt;-- <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.routeguide.Feature</span>       <span style="color:#cd00cd;"> |  V</span>
<span style="color:#00cd00;">N  |                                  </span><span style="color:#cd00cd;"> |  E</span>
<span style="color:#00cd00;">T  | </span><span style="font-weight:bold;color:#7f7f7f;">RecordRoute</span>:                     <span style="color:#cd00cd;"> |  R</span>
<span style="color:#00cd00;">   | </span>   &gt;&gt;&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.routeguide.Point</span>         <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span>   &lt;-- <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.routeguide.RouteSummary</span>  <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   |                                  </span><span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span><span style="font-weight:bold;color:#7f7f7f;">RouteChat</span>:                       <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span>   &gt;&gt;&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.routeguide.RouteNote</span>     <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span>   &lt;&lt;&lt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.routeguide.RouteNote</span>     <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   |                                  </span><span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span><span style="font-weight:bold;color:#7f7f7f;">ListFeatures</span>:                    <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span>   --&gt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#00cd00;">.routeguide.Rectangle</span>     <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   | </span>   &lt;&lt;&lt; <span style="font-style:italic;"></span><span style="font-style:italic;color:#cd00cd;">.routeguide.Feature</span>       <span style="color:#cd00cd;"> |   </span>
<span style="color:#00cd00;">   |                                  </span><span style="color:#cd00cd;"> |   </span>
</pre>

`samples/` taken from https://github.com/grpc/grpc-java/tree/master/examples/src/main/proto