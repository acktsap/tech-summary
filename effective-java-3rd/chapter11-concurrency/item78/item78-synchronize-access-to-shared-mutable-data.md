# Item 78: Synchronize access to shared mutable data

## Synchronization

```text
Not only does synchronization prevent threads from observing an object
in an inconsistent state (mutual exclution), but it ensures that each thread
entering a synchronized method or block sees the effects
of all previous modifications that were guarded by the same lock
```

> Fulfill happen before condition

## Synchronization on primitive type

```text
The language specification guarantees that reading or writing a variable is
atomic unless the variable is of type long or double [JLS, 17.4, 17.7].

While the language specification guarantees that a thread will not see an
arbitrary value when reading a field, it does not guarantee that a value written by
one thread will be visible to another. Synchronization is required for reliable
communication between threads as well as for mutual exclusion.

This is due to a part of the language specification known as the memory model,
which specifies when and how changes made by one thread become visible to others.
```

> Because of cache
> Use volatile to keep value only on memory (not on cache)

## Stopping thread

```text
The libraries provide the Thread.stop method,
but this method was deprecated long ago because it is inherently unsafe—its use
can result in data corruption. Do not use Thread.stop. A recommended way to
stop one thread from another is to have the first thread poll a boolean field that is
initially false but can be set to true by the second thread to indicate that the first
thread is to stop itself
```

## Synchronization & Read/Write

```text
It is not sufficient to synchronize only the write method!
Synchronization is not guaranteed to work unless both read and
write operations are synchronized
```

## Avoid Synchronication

```text
The best way to avoid the problems discussed in this item is not to share
mutable data. Either share immutable data (Item 17) or don’t share at all. In other
words, confine mutable data to a single thread
```

## Summary

```text
when multiple threads share mutable data, each thread that
reads or writes the data must perform synchronization. In the absence of
synchronization, there is no guarantee that one thread’s changes will be visible to
another thread. The penalties for failing to synchronize shared mutable data are
liveness and safety failures

If you need only inter-thread communication,
and not mutual exclusion, the volatile modifier is an acceptable form of
synchronization, but it can be tricky to use correctly
```
