# Spring Testing

## Introduction to Spring Testing

Inversion of Control (IoC)은 의존관계 주입을 알아서 해줘서 테스트를 위한 객체를 쉽게 만들어준다.

## Unit Testing

Spring은 Unit Testing을 위한 mock object와 util class들을 제공해줌.

### Mock Objects

- Environment : `org.springframework.mock.env` package. `Environment`와 `PropertySource`에 대한 mock을 제공.
- JNDI : `org.springframework.mock.jndi` package. `JNDI SPI` (JNDI : Java Naming and Directory Interface).
- Servlet API : `org.springframework.mock.web` package. Spring MVC를 테스트 하기 위한 mock을 제공.
- Spring Web Reactive : `he org.springframework.mock.http.server.reactive` package. `ServerHttpRequest`와 `ServerHttpResponse`에 대한 mock을 제공.

### Unit Testing Support Classes

- General Testing Utilities : `org.springframework.test.util` package.
  - ReflectionTestUtils : protected, private 로 선언된 값들을 바꿔서 테스트 할 때 사용.
  - AopTestUtils : 테스트 대상의 object가 proxy로 감싸져 있는 경우 해당 object를 mocking 할 때 사용.
- Spring MVC Testing Utilities : `org.springframework.test.web` package. `ModelAndView`를 JUnit같은 다른 framework를 사용해서 테스트 할 수 있는 기능을 제공.

## Integration Testing

### Overview

## Reference

- [Spring Testing](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/testing.html)