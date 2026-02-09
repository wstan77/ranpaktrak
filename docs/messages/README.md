# Messages

## Information / Warnings
`ResponseObject`  is the base class that should be extended in order to return an **info** or **warning** message to the client. An **Info** message should be used to reflect a success or useful information that should be relayed to the end user.  A **warning**  message should reflect something of concern that may require user action (e.g. "There is already a Foo for user Bar. Redirecting to the existing Foo."). 

```Java
  public SomethingThatExtendsResponseObject saveSomething(SomethingThatExtendsResponseObject something) {
    // ...
    // ...
    return something.addResponseInfo("Something Saved Successfully");
  }
```

## Errors
*Error* messages **SHOULD NOT** utilize the `ResponseObject`  class. Instead throw a `RuntimeException`  such as one of the following:
1. **FormValidationException**  - should be thrown when a business rule is broken or a form is invalid.
2. **InsufficientPermissionException** - should be thrown when a user does not have permission to perform an action.
3. **ModifiedSinceLoadedException** - should be thrown when attempting to update a resource that is out of date or stale.
4. **ResourceNotFoundException**  - should be thrown when a resource can not be loaded because it does not exist. Also when a user attempts to load a resource they don't have permission to know about.

> Note: This is not a finite list of exceptions. You should choose one that correctly identifies the reason for the exception being thrown. If one does not exist, a new exception should be created. Creating a new `Exception`  may require updating the apps `ErrorController`. 
