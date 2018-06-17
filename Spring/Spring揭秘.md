## Spring揭秘

### Chapter 7 一起来看AOP
#### 1.静态AOP

 - 代表 **AspectJ**
 - 相应的横切关注点以以Aspect形式实现以后，通过特定的编译器将实现后的Aspect编译并织入到系统的静态类。
 - 优点：Aspect直接以Java字节码的形式编译到Java类中。JVM可以向往常一样加载Java类运行
 - 缺点：不够灵活。每次修改都要使用编译器重新编译Aspect并重新织入到系统

#### 2.动态AOP
- JBoss AOP，Spring AOP，Nanning以及AspectJ
- 大都采用Java实现，AOP各种概念实体都是Java类。
- AOP的织入是在系统运行开始之后，而不是预先编译到系统类，织入信息采用外部XML保存，可以调整织入模块及织入点而不必变更系统其他模块

#### 3.术语
- Joinpoint
 类型：
	- 方法调用
	- 方法调用执行
	- 构造方法调用
	- 构造方法调用执行
	- 字段设置
	- 字段获取
	- 异常处理执行
	- 类初始化——类中某些静态类或静态块初始化的点
- Pointcut
  表述方式：
	- 直接指定Joinpoint所在方法名称
	- 正则表达式
	- 特定的Pointcut表述语言
	
 	Pointcut之间可以执行逻辑运算
- Advice——单一横切关注点逻辑的载体，代表会织入到Joinpoint的横切逻辑
	- Before Advice-在Joinpoint指定位置之前执行的Advice类型
	- After Advice
		- After Returning Advice
		- After throwing Advice
		- After (Finally) Advice

			![各种Advice的执行时机](./images/1529226287226.png)
	- Around Advice——在Joinpoint之前和之后执行横切逻辑
- **Introduction**——不是根据横切逻辑在Joinpoint的执行时机区分的，而是根据它可以完成的功能而区分其他Advice类型
	- 可以为原有的对象增加新的特性或行为