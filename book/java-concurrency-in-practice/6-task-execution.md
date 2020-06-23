# 6. Task Execution

- [6. Task Execution](#6-task-execution)
  - [6.1. Executing Tasks in Thread](#61-executing-tasks-in-thread)
    - [Executing Tasks Sequentially](#executing-tasks-sequentially)
    - [Explicitly Creating Threads for Tasks](#explicitly-creating-threads-for-tasks)
    - [Disadvantages of Unbounded Thread Creation](#disadvantages-of-unbounded-thread-creation)
  - [6.2. The Executor Framework](#62-the-executor-framework)
    - [Example: Web Server Using Executor](#example-web-server-using-executor)
    - [Execution Policies](#execution-policies)
    - [Thread Pools](#thread-pools)
    - [Executor Lifecycle](#executor-lifecycle)
    - [Delayed and Periodic Tasks](#delayed-and-periodic-tasks)
  - [6.3. Finding Exploitable Parallelism](#63-finding-exploitable-parallelism)
    - [Example: Sequential Page Renderer](#example-sequential-page-renderer)
    - [Result bearing Tasks: Callable and Future](#result-bearing-tasks-callable-and-future)
    - [Example: Page Renderer with Future](#example-page-renderer-with-future)
    - [Limitations of Parallelizing Heterogeneous Tasks](#limitations-of-parallelizing-heterogeneous-tasks)
    - [CompletionService: Executor Meets BlockingQueue](#completionservice-executor-meets-blockingqueue)
    - [Example: Page Renderer with CompletionService](#example-page-renderer-with-completionservice)
    - [Placing Time Limits on Tasks](#placing-time-limits-on-tasks)
    - [Example: A Travel Reservations Portal](#example-a-travel-reservations-portal)

## 6.1. Executing Tasks in Thread

작업은 서로 독립적이고 작은 단위로 나누어야 한다. 그래야 에러 복구도 쉽고 병렬처리를 잘 할 수 있다. 서버의 입장에서 작은 단위가 client 요청 한개 정도일 수도 있다.

서버는 과부하에서 죽지 말고 성능이 점진적으로 떨어지게 설계되어야 한다.

### Executing Tasks Sequentially

서버를 single thread로 실행시키면 resource utilization에서도 안좋고 I/O나 db접근 등에 blocking 상태가 되면 cpu를 낭비하고 다른 요청을 받기 힘듬.

```java
class SingleThreadWebServer {
  // acceping & processing request in a single thread
  public static void main(String[] args) throws IOException {
    ServerSocket socket = new ServerSocket(80);
    while (true) {
      Socket connection = socket.accept();
      handleRequest(connection);
    }
  }
}
```

### Explicitly Creating Threads for Tasks

요청마다 다른 thread를 만들어서 처리하면

- 요청을 받는 main thread가 다른 요청을 더 빨리 받을 수 있음
- 요청 처리를 병렬적으로 할 수 있음
- But 요청 처리에 thread safety를 확보해야함

```java
class ThreadPerTaskWebServer {
  // acceping request in a single thread & processing it in multi thread
  public static void main(String[] args) throws IOException {
    ServerSocket socket = new ServerSocket(80);
      while (true) {
        final Socket connection = socket.accept();
        Runnable task = () -> handleRequest(connection);
        new Thread(task).start();
      }
  }
}
```

### Disadvantages of Unbounded Thread Creation

- Thread를 생성하고 소멸시키는데 비용이 많이듦
- 처리할 수 있는 processor는 한정되어 있는데 너무 많은 thread를 생성하면 memory가 낭비됨. 또한 gc에도 부담을 주며 많은 thread가 cpu를 가지려고 경쟁해서 더 느려짐.
- jvm 설정에 따라 생성될 수 있는 개수가 한계가 있음. 또한 `-Xss` 설정으로 할 수 있는 thread별 stack size도 한계가 있음.

이처럼 너무 많은 thread를 생성하면 안좋은 점이 많아서 thread 수에 bound를 줄 필요가 있음.

## 6.2. The Executor Framework

### Example: Web Server Using Executor

### Execution Policies

### Thread Pools

### Executor Lifecycle

### Delayed and Periodic Tasks

## 6.3. Finding Exploitable Parallelism

### Example: Sequential Page Renderer 

### Result bearing Tasks: Callable and Future

### Example: Page Renderer with Future 

### Limitations of Parallelizing Heterogeneous Tasks

### CompletionService: Executor Meets BlockingQueue

### Example: Page Renderer with CompletionService

### Placing Time Limits on Tasks

### Example: A Travel Reservations Portal
