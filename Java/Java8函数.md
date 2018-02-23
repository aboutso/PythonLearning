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
### 【尾递归】
	如果一个函数中所有递归形式的调用都出现在函数的末尾，我们称这个递归函数是尾递归的。当递归调用是整个函数体中最后执行的语句且它的返回值不属于表达式的一部分时，这个递归调用就是尾递归。尾递归函数的特点是在回归过程中不用做任何操作，这个特性很重要，因为大多数现代的编译器会利用这种特点自动生成优化的代码。
#### 提供了尾递归Demo

## 组合式异步编程
### Java8之前的异步编程

 - 继承Thead类,重写run方法 实现runable接口，实现run方法
 - 匿名内部类编写thread或者实现runable的类，当然在java8中可以用lambda表达式简化
 - 使用futureTask进行附带返回值的异步编程
 - 使用线程池和Future来实现异步编程
 - spring框架下的@async获得异步编程支持

### Java8中的使用
- ```CompletableFuture```
-  构造同步和异步操作
   -  同步任务——使用```future.thenApply(Function)```来实现,该方法接受一个Function对象
   ```Java
   stream().map(xxx->supplayAsync(()->任务A)) 
   //这一步已经异步的映射成了任务A
   .map(future->future.thenApply(任务B)//执行同步的任务B
	.collect
   ```
   - 异步任务——与同步几乎一样，方法变为```future.thenCompose(Function)```
   ```Java
   stream()
		.map(xxx->supplayAsync(()->任务A)) //这一步已经异步的映射成了任务A
		.map(future->future.thenApply(任务B)//执行同步的任务B
	.map(future->future.thenCompose(任务C))//再异步执行任务C
	.collect
   ```
  - 将两个 CompletableFuture 对象整合起来，无论它们是否存在依赖
  ```Java
   Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> shop.getPrice(product))//任务A
                .thenCombine(
                     CompletableFuture.supplyAsync(() -> exchangeService.getRate(Money.EUR, 					Money.USD)), //任务B
                    (price, rate) -> price * rate); //任务A与任务B的合并操作
  ```
- 对结果进行处理——使用```thenAccept(Consumer)```
 ```Java
 	stream()
    .map(xxx->supplayAsync(()->任务A)) //这一步已经异步的映射成了任务A
    .map(future->future.thenApply(任务B)//执行同步的任务B
    .map(future->future.thenCompose(任务C))//再异步执行任务C
    .map(future->future.thenAccept(System.out::println))//将结果打印
    .collect
 ```
 
 - 使用allOf与anyOf对结果进行处理
 ```Java
 	CompletableFuture[] futures = findPricesStream("myPhone")
	.map(f -> f.thenAccept(System.out::println))
	.toArray(size -> new CompletableFuture[size]);
	
	CompletableFuture.anyOf(futures).join();
 ```
 
   - allOf 工厂方法接收一个由 CompletableFuture 构成的数组，数组中的所有 Completable-Future 对象执行完成之后，它返回一个 CompletableFuture 对象。这意味着，如果你需要等待最初 Stream 中的所有 CompletableFuture 对象执行完毕，对 allOf 方法返回的CompletableFuture 执行 join 操作是个不错的主意。这个方法对“最佳价格查询器”应用也是有用的，因为你的用户可能会困惑是否后面还有一些价格没有返回，使用这个方法，你可以在执行完毕之后打印输出一条消息“All shops returned results or timed out” 。
   - 然而在另一些场景中，你可能希望只要 CompletableFuture 对象数组中有任何一个执行完毕就不再等待，比如，你正在查询两个汇率服务器，任何一个返回了结果都能满足你的需求。在这种情况下，你可以使用一个类似的工厂方法 anyOf 。该方法接收一个 CompletableFuture 对象构成的数组， 返回由第一个执行完毕的 CompletableFuture 对象的返回值构成的 Completable-Future

  [1]: ./images/1519275752350.jpg
  [2]: ./images/1519277516029.jpg