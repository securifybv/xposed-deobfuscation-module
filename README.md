# String deobfuscation Xposed module

This repository contains the Android Xposed module to deobfuscate strings.

## Requirements

* Android phone running Xposed
* Network connection (LAN only)

## How to use

Install the module, start the application to start the server.
It will open a socket on port 6000.

After this, install the application you're analyzing on the same device.

To make use of this, you need to have a client. As of currently, the only client exists in the shape of a JEB3 plugin.
Other clients could be developed, either standalone or as a plugin for another decompiler.

## Limitations

Currently the module only supports methods of the following signature:


```java
String foo(String arg1)
```

In other words, it only suppports methods which accept a String and return a String.
I've never had use for any other signatures, so I didn't bother to implement it, but it wouldn't be too hard to do so.
