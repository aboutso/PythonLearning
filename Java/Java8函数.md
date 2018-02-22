---
title: Java8函数
tags: Java,Java8,Lambda
grammar_cjkRuby: true
---
## 函数接口
### 1.Function 
![Function Interface][1]
 - 对接受一个T类型参数,返回R类型的结果的方法的抽象,通过调用apply方法执行内容。
 - 需要注意的是 ```FunctionalInterface```注解 

### 2.Consumer

- Represents an operation that accepts a single input argument and returns no result. Unlike most other functional interfaces, Consumer is expected to operate via **side-effects**.

  [1]: ./images/1519275752350.jpg