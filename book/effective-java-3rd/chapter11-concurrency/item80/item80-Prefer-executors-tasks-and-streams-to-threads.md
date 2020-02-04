# Item 80: Prefer executors, tasks, and streams to threads

## ExecutorService

```text
You can do many more things with an executor service.

For example

You can wait for a particular task to complete (with the get method)
You can wait for any or all of a collection of tasks to complete (using the invokeAny or invokeAll methods)
You can wait for the executor service to terminate (using the awaitTermination method)
You can retrieve the results of tasks one by one as they complete (using an ExecutorCompletionService)
You can schedule tasks to run at a particular time or to run periodically (using a
ScheduledThreadPoolExecutor)
```

## Choosing Thread Pool

```text
If you want more than one thread to process requests from the queue, simply
call a different static factory that creates a different kind of executor service called a thread pool.
The java.util.concurrent.Executors class contains static factories
that provide most of the executors you’ll ever need.

For a small program, or a lightly loaded server, Executors.newCachedThreadPool is
generally a good choice because it demands no configuration and generally “does
the right thing.” But a cached thread pool is not a good choice for a heavily loaded
production server!

If a server is so heavily loaded that all of its CPUs are fully
utilized and more tasks arrive, more threads will be created, which will only make
matters worse. Therefore, in a heavily loaded production server, you are much
better off using Executors.newFixedThreadPool
```

## High Level Abstraction : Runnable, Callable, ExecutorService

```text
The key abstraction is the unit of work, which is the task.
There are two kinds of tasks: Runnable and its close cousin, Callable (which is
like Runnable, except that it returns a value and can throw arbitrary exceptions).
The general mechanism for executing tasks is the executor service.
```

## ForkJoinPool

```text
In Java 7, the Executor Framework was extended to support fork-join tasks,
which are run by a special kind of executor service known as a fork-join pool. A
fork-join task, represented by a ForkJoinTask instance, may be split up into
smaller subtasks, and the threads comprising a ForkJoinPool not only process
these tasks but “steal” tasks from one another to ensure that all threads remain
busy, resulting in higher CPU utilization, higher throughput, and lower latency

Parallel streams (Item 48) are written atop fork join pools and allow you
to take advantage of their performance benefits with little effort.
```
