Lucida on this fork uses gRPC
=============================

The thrift rpc interface is currently being removed and replaced. This is work in progress.
Thrift is stable but the async C++ version provided by Facebook is not. FBThrift is broken,
you cannot build apache thrift from FB's fork, and the latest official release is Jan 2015. 
If you checkout master then the tests fail. In order to solve these issues one must checkout
a particular commit that is verifed to work with a particular commit of FB folly etc. Folly
is very active but FBthrift has to use an outdated version to work.

This version of Lucida uses gRPC. This is the same code Google uses for their infrastructure.
It's bullet proof tested, production ready, and stable. The conversion to gRPC should be 
completed soon.

## Building All Services

This directory contains all of the back-end services and the command center.
To build all services (assuming you are in this directory) you will need to
initialize the build system. This only need to be done once and may require
you to enter your credentials. The build init assumes you are in the sudoers.
If this is not the case then ensure your username is added to the sudoers first.

To initialize the build run:
```
pushd ../
make init
popd
```

To build Java services run:
```
gradle build
```

To build C++ services run:
```
make
```

Alternatively this can all be done from the root project directory.
```
pushd ../
make init
make
popd
```

## Project Structure

The top level root project builds the gRPC intefaces for Java, Python, and C++. The generated
code can be found in `./build/generated`. 

The core lucida service code is split into Java and C++. Python only requires the gRPC generated 
bindings. Java code is located at src/main/java, C++ code is located at src/main/cpp. Tests are
are located at src/test.java and src/test/cpp.

Server and client code are located in this folder. The backing implementation for a service
is typically one directory below. For example, the question and answering client and server code
is located at `./lucida/questionanswering/src` and the backing implementation is located at
`./lucida/questionanswering/OpenEphyra`.

## Importing into IntelliJ IDEA

### OSX

- Install the IDE and setup [Java home](http://stackoverflow.com/questions/31215452/intellij-idea-importing-gradle-project-getting-java-home-not-defined-yet).
- Import the gradle project at `./lucida`.

To set Java home in the shell add this line to your bash profile.
```
export JAVA_HOME="/usr/libexec/java_home"
```

### Linux

TBD.

## TODO List

If you would like more features let us know. Here is what we have on the near horizon.
The items below are just ideas and are not fully vetted.

- Streaming support for learn and infer.
- Common sense reasoning added to OpenEphra.
- True support for credentials.
- Change interface to use gRPC oneof rather than relying on the server and client to have
  an agreement on how data should be formatted.
- Add option to use Googles speech and vision API's as a replacement for Kaldi and OpenCV.
- Add option to use AWS DynamoDB/S3 instead of Mongodb.

