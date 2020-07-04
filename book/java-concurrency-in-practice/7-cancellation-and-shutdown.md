# 7. Cancellation and Shutdown

- [7. Cancellation and Shutdown](#7-cancellation-and-shutdown)
  - [7.1. Task Cancellation](#71-task-cancellation)
    - [Interruption](#interruption)
    - [Interruption Policies](#interruption-policies)
    - [Responding to Interruption](#responding-to-interruption)
    - [Example: Timed Run](#example-timed-run)
    - [Cancellation Via Future](#cancellation-via-future)
    - [Dealing with Non-interruptible Blocking](#dealing-with-non-interruptible-blocking)
    - [Encapsulating Nonstandard Cancellation with Newtaskfor](#encapsulating-nonstandard-cancellation-with-newtaskfor)
  - [7.2. Stopping a Thread based Service](#72-stopping-a-thread-based-service)
    - [Example: A Logging Service](#example-a-logging-service)
    - [ExecutorService Shutdown](#executorservice-shutdown)
    - [Poison Pills](#poison-pills)
    - [Example: A One-shot Execution Service](#example-a-one-shot-execution-service)
    - [Limitations of Shutdownnow](#limitations-of-shutdownnow)
  - [7.3. Handling Abnormal Thread Termination](#73-handling-abnormal-thread-termination)
    - [Uncaught Exception Handlers](#uncaught-exception-handlers)
  - [7.4. JVM Shutdown](#74-jvm-shutdown)
    - [Shutdown Hooks](#shutdown-hooks)
    - [Daemon Threads](#daemon-threads)
    - [Finalizers](#finalizers)

End-of-lifecycle issue는 시스템 디자인에 중요한 요소중 하나임. 이를태면 작업을 하는 thread가 직접 resource를 정리하게 하는거랑 해당 thread를 종료하는 thread가 자원을 종료시키는 경우에 시스템 디자인이 달라질 수 있음.

## 7.1. Task Cancellation

```java
@ThreadSafe
public class PrimeGenerator implements Runnable {
  @GuardedBy("this")
  private final List<BigInteger> primes = new ArrayList<BigInteger>();

  private volatile boolean cancelled;

  public void run() {
    BigInteger p = BigInteger.ONE;
    while (!cancelled ) {
      p = p.nextProbablePrime();
      synchronized (this) {
        primes.add(p);
      }
    }
  }

  public void cancel() { cancelled = true; }

  public synchronized List<BigInteger> get() {
    return new ArrayList<BigInteger>(primes);
  }
}
```

```java
List<BigInteger> aSecondOfPrimes() throws InterruptedException {
  PrimeGenerator generator = new PrimeGenerator();
  new Thread(generator).start();
  try {
    SECONDS.sleep(1);
  } finally {
    generator.cancel();
  }
  return generator.get();
}
```

### Interruption

### Interruption Policies

### Responding to Interruption

### Example: Timed Run

### Cancellation Via Future

### Dealing with Non-interruptible Blocking

### Encapsulating Nonstandard Cancellation with Newtaskfor

## 7.2. Stopping a Thread based Service

### Example: A Logging Service

### ExecutorService Shutdown

### Poison Pills

### Example: A One-shot Execution Service

### Limitations of Shutdownnow

## 7.3. Handling Abnormal Thread Termination

Multi threaded application에서는 에러가 발생하면 에러를 console에 찍음. But single thread application과는 다르게 아무도 인지 못할 수도 있음. Thread level에서 던지고 해당 thread만 죽기 때문임.

Thread level에서 exception을 던져버리면 해당 thread가 죽어버림. Thread pool에서 해당 thread가 죽어버렸는데 다시 안살리면 시스템 전체가 멈출 수 있음. 예를 들면 GUI application에서 event dispatch thread가 죽어버리면 event를 처리하지 못하고 멈춰버릴 수 있음. 그래서 보통 try-catch로 감싸서 죽는 것을 시스템에 알려주던지 해야 함.

Threadpool을 작성할 때는 새 thread를 만들거나 이미 충분한 thread가 있을 시 해당 thread를 그냥 죽여버리는 전략을 취할 수 있음.

```java
public void run() {
  Throwable thrown = null;
  try {
    while (!isInterrupted()) {
      runTask(getTaskFromWorkQueue());
    }
  } catch (Throwable e) {
    thrown = e;
  } finally {
    // 이런 식으로 알려주는게 좋음
    threadExited(this, thrown);
  }
}
```

### Uncaught Exception Handlers

Thread별로 `thread.setUncaughtExceptionHandler`를 사용해서 Exception handler 설정 가능. 기본 동작은 `System.err`에 stack trace를 찍는 것임.

Thread pool을 대상으로 `UncaughtExceptionHandler`을 설정하려면 `ThreadPoolExecutor`를 생성할 때 thread 생성을 담당하는 `ThreadFactory`를 넘겨주면 된다 (?? 이해안됨). 자바에서 제공하는 기본 thread pool에서는 try-finally를 사용해서 thread pool에 thread가 종료된다는 사실을 알려서 다른 thread로 대체될 수 있게 함.

참고로 execute로 제출된 task들만 UncaughtExceptionHandler로 처리할 수 있음. submit으로 제출된 것은 `future.get()`을 했을 때 ExecutionException로 감싸져서 던져짐.

```java
// exception handler interface
// thread.setUncaughtExceptionHandler 로 설정 가능
public interface UncaughtExceptionHandler {
  void uncaughtException(Thread t, Throwable e);
}

// custom exception handler
public class UEHLogger implements Thread.UncaughtExceptionHandler {
  public void uncaughtException(Thread t, Throwable e) {
    Logger logger = Logger.getAnonymousLogger();
    logger.log(Level.SEVERE, "Thread terminated with exception: " + t.getName(), e);
  }
}
```

## 7.4. JVM Shutdown

Jvm은 다음의 경우에 종료된다

- 마지막 non-daemon thread가 종료되었을 경우
- System.exit이 호출되거나 등 platform level에서의 종료 신호를 받았을 때 (Ctrl + c나 SIGINT 등)
- Runtime.halt에 의해 강제종료 되거나 os에 의해 jvm process가 죽었을 경우 (SIGKILL 같은거)

### Shutdown Hooks

`Runtime.getRuntime().addShutdownHook`로 jvm shutdown hook을 등록할 수 있음. But shutdownHook의 실행 순서는 보장되지 않음. 종료되고 있는 시점에 다른 thread가 실행되고 있으면 그 thread랑 hook이 같이 돌음. 모든 shutdownHook가 실행되고 나면 runFinalizersOnExit flag가 true인 경우 finalizer를 실행시킨 후 종료하고 아니면 바로 종료함. abrupt일 경우 hook이나 finalizer가 실행되지 않고 바로 종료됨.

shutdown hook나 finalizer가 종료되지 않으면 jvm도 계속 종료되지 않음 이 경우 abrupt시켜줘야함.

shutdown hook은 보통 resource cleanup등에서 사용되는데 병렬적으로 실행되어서 서비스간 문제가 발생할 수 있음. 그래서 race condition등을 피하기 위해 한개의 shutdown hook을 사용하는 것이 좋음. 그냥 종료할 때 single thread로 clean up해주는게 좋음 순서도 필요할 수 있고.

```java
public void start() {
  // register shutdown hooks
  Runtime.getRuntime().addShutdownHook(new Thread() {
    public void run() {
      try { LogService.this.stop(); }
      catch (InterruptedException ignored) {}
    }
  });
}
```

### Daemon Threads

Thread는 normal thread랑 daemon thread가 있음. jvm이 실행될 때 main thread를 제외하고는 daemon thread임. Thread가 생성될 때 해당 thread가 생성되는 thread의 상태를 받음. 그래서 보통 만들면 main thread의 상태를 받아서 normal thread임.

Jvm이 종료될 때 normal thread가 모두 종료된 것을 확인하고 shutdown process를 진행함. Daemon thread는 돌아가고 있든 말든 무시함. 그래서 Daemon thread의 자원같은게 정리되지 못할 수 있기 때문에 clean up이 없어도 되는 경우에만 사용하는게 좋음 (eg. 기간이 만료된 app내의 cache 제거).

### Finalizers

finalize method는 실행될지 확신할 수 없고 대부분 try-finally로 대체될 수 있음. 유일한 예외는 native method에서 사용한 resource를 정리하는 경우 그 외는 finalize method는 사용하지 마라.
