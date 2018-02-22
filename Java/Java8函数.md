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

- Represents an operation that accepts a single input argument and returns no result. Unlike most other functional interfaces, Consumer is expected to operate via **side-effects**

### 3.Supplier
- 不接受参数，但是提供一个返回值
- 使用get()方法获得这个返回值

### 4.Predicate
- Represents a predicate (boolean-valued function) of one argument.
- This is a functional interface whose functional method is ```test(Object)```.

## 接口关系
![接口示意图][2]

## 关于lambda的限制
- Java8中的lambda表达式,并不是完全闭包，lambda表达式对值封闭，不对变量封闭。简单点来说就是局部变量在lambda表达式中如果要使用，必须是声明final类型或者是隐式的final
- 原因：
	- 为什么局部变量会有限制而静态变量和全局变量就没有限制，因为局部变量是保存在栈上的，而众所周知，栈上的变量都隐式的表现了它们仅限于它们所在的线程，而静态变量与实例变量是保存在静态区与堆中的，而这两块区域是线程共享的，所以访问并没有问题。
	- 现在我们假设如果lambda表达式可以局部变量的情况，实例变量存储在堆中，局部变量存储在栈上，而lambda表达式是在另外一个线程中使用的，那么在访问局部变量的时候，因为线程不共享，因此lambda可能会在分配该变量的线程将这个变量收回之后，去访问该变量。所以说，Java在访问自由局部变量时，实际上是在访问它的副本，而不是访问原始变量。如果局部变量仅仅赋值一次那就没有什么区别了。
	- 严格保证这种限制会让你的代码变得无比安全，如果你学习或了解过一些经典的函数式语言的话，就会知道不变性的重要性，这也是为什么stream流可以十分方便的改成并行流的重要原因之一。 

## 使用lambda实现Java的尾递归

  [1]: ./images/1519275752350.jpg
  [2]: ./images/1519277516029.jpg