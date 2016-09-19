Lucida on this branch uses gRPC
===============================

The thrift rpc interface is currently being removed and replaced. This is work in progress.
Thrift is stable but the async C++ version provided by Facebook is not. FBThrift is broken,
you cannot build apache thrift from FB's fork, and the latest official release is Jan 2015. 
If you checkout master then the tests fail. In order to solve these issues one must checkout
a particular commit that is verifed to work with a particular commit of FB folly etc. Folly
is very active but FBthrift has to use an outdated version to work.

This version of Lucida uses gRPC. This is the same code Google uses for their infrastructure.
It's bullet proof tested, production ready, stable, with excellent integration into Gradle.
The conversion to gRPC should be completed soon.

## Building All Services

This directory contains all of the back-end services and the command center.
To build all services (assuming you are in this directory):

```
pushd ../deps
make
popd
gradle build
```

## Project Structure

The top level root project builds the gRPC intefaces for Java, Python, and C++. The generated
code can be found in `./build/generated`. Asynchronous RPC is supported in all languages
thanks to gRPC.

## TODO List

If you would like more features let us know. Here is what we have on the near horizon.
The items below are just ideas and are not fully vetted.

- Streaming support for learn and infer.
- Common sense reasoning added to OpenEphra.
- True support for credentials. LUCID is a hack that needs to be removed.
- Change interface to use gRPC oneof rather than relying on the server and client to have
  an agreement on how data should be formatted.
- Add option to use Googles speech and vision API's as a replacement for Kaldi and OpenCV.
- Add option to use AWS DynamoDB/S3 instead of Mongodb.

