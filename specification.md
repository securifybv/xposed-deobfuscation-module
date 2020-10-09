# Specification

This document will describe how you can implement a client for this Xposed module.

## Information

The client will need to acquire the following information:

* Methods
    * Is it static?
    * Method name
    * Class name
    * Package name (exact location)
    * Arguments
        * Type
        * Value
* Package name

## Format

The data needs to be sent in JSON format, no newlines are allowed in the data except for one at the end to signal the end of the payload.
Following is an example of one such payload:


```json
{
    "data": [
        {
            "static": true,
            "methodName": "test",
            "className": "ObfuscationMethods",
            "locationPackage": "nl.securify.stringobfuscationtest",
            "arguments": [
                {
                    "type": "Ljava/lang/String;",
                    "arg": "test"
                }
            ]
        }
    ],
    "manifestPackagename": "nl.securify.stringobfuscationtest"
}
```
