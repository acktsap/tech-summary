# Item 79: Avoid Excessive Synchronization

## Excessive Synchronization & Alien

```text
To avoid liveness and safety failures, never cede control to the client
within a synchronized method or block. In other words, inside a synchronized
region, do not invoke a method that is designed to be overridden, or one provided
by a client in the form of a function object (Item 24).
```

```text
From the perspective of the class with the synchronized region, such methods are alien.
The class has no knowledge of what the method does and has no control over it.
Depending on what an alien method does, calling it from a synchronized region can cause exceptions, deadlocks, or data corruption
```

## Do as little work inside synchronized regions

```text
An alien method invoked outside of a synchronized region is known as an
open call [Goetz06, 10.1.4]. Besides preventing failures, open calls can greatly
increase concurrency. An alien method might run for an arbitrarily long period

As a rule, you should do as little work as possible inside synchronized
regions
```

## Writing mutable class

```text
If you are writing a mutable class, you have two options: you can omit all
synchronization and allow the client to synchronize externally if concurrent use is
desired, or you can synchronize internally, making the class thread-safe (Item 82).

You should choose the latter option only if you can achieve significantly higher
concurrency with internal synchronization than you could by having the client
lock the entire object externally

java.util (former) vs java.util.concurrent (latter)

When in doubt, do not synchronize your class, but document that it is not thread-safe.
```

## Summary

```text
In summary, to avoid deadlock and data corruption, never call an alien method
from within a synchronized region. More generally, keep the amount of work that
you do from within synchronized regions to a minimum. When you are designing
a mutable class, think about whether it should do its own synchronization.

Synchronize your class internally only if there is a good reason to do so, and document your decision clearly (Item 82).
```
