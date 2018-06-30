### Chapter 17 事务概念

 1. 基本特性
 - 原子性
 - 一致性
 - 持久性
 - 隔离性。4种类型的隔离级别（从弱到强）：
	 - Read Uncommitted
	 - Read Committed
	 - Repeatable Read
	 - Serializable

2.典型事务处理的参与者：
- Resource Manager（**RM**）——负责存储并管理系统数据资源的状态
- Transaction Processing Monitor（**TP Monitor**）——在分布式事务场景中协调包含多个RM的的事务处理
- Transaction Manager——TP Monitor中的核心模块，直接负责多RM之间事务的协调
- Application——事务边界的触发点

3.事务分类
- 全局事务——多RM参与
- 局部事务——只有一个RM
### Chapter 18 Java的事务管理
1.Java平台的局部事务支持

	通过当前使用的数据访问技术所提供的基于**connection**的API管理事务
- 消息服务资源——通过JMS的javax.jms.session控制整个事务

2.Java平台的分布式事务
- JTA（**Java Transaction API**）
	- JTA是Sun提出的标准化分布式事务访问的接口规范。
	- 具体实现由供应商提供
- JCA（**Java Connector Architecture**）——面向EIS（Enterprise Information System）的集成，通过为遗留EIS系统和Java EE应用服务器制定统一的通信标准，二者可以实现服务上的互通
### Chapter 19 Spring事务架构
1.**基本原则**：事务管理的关注点和数据访问关注点**相分离**
2.**DataSourceUtils**对connection的管理：```org.springframework.jdbc.datasource.DataSourceUtils```从```org.springframework.transaction.support.TransactionSynchronizationManager```获取connection资源。如果当前线程之前没有绑定connection资源，通过数据访问对象的datasource获取新的connection，否则使用绑定的connection
3.基础结构
![事务抽象接口关系图](./images/1530031694886.png)
- TransactionDefinition
	- 事务的隔离级别
	- 事务的传播行为——表示整个事务处理过程所跨越的业务对象，将以什么样的行为参与事务
		- PROPAGATION_REQUIRED(default)
		- PROPAGATION_SUPPORTS
		- PROPAGATION_MANDATORY
		- PROPAGATION_REQUIRES_NEW
		- PROPAGATION_NOT_SUPPORTED
		- PROPAGATION_NEVER
		- PROPAGATION_NESTED
	- 事务的超时时间
	- 是否为**只读**事务——仅仅是给ResourceManager一种优化提示，但是否优化由ResourceManager决定
	- 实现：**编程式事务场景**、**声明式事务场景**
- TransactionStatus
	- 查询事务状态
	- 通过setRollbackOnly()方法标记当前事务以使其回滚
	- 如果相应的PlatformTransactionManager支持SavePoint，可以通过TransactionStatus在当前事务中创建内部嵌套事务
- PlatformTransactionManager
	- 功能：界定事务边界。PlatformTransactionManager使用TransactionDefinition开启相关事务，事务从开启到结束之间的状态由TransactionStatus负责，可以通过TransactionStatus对事物进行有限的控制

4.PlatformTransactionManager
- 整个抽象体系基于**Strategy模式**：由PlatformTransactionManager对事务界定统一抽象，具体的界定策略实现交给具体的实现类

4.1、局部事务下PlatformTransactionManager实现类
| 数据访问技术| PlatformTransactionManager实现类|
| --------- | ---------------------------- |
| JDBC      | DataSourceTransactionManager |
| Hibernate | HibernateTransactionManager  |
| JDO       | JdoTransactionManager        |
| JPA       | JpaTransactionManager        |
| TopLink   | TransactionManager           |
| *JMS*       | JmsTransactionManager        |
4.2、全局事务下PlatformTransactionManager实现类
```org.springframework.transaction.jta.JtaTransactionManager```

### Chapter 20 Spring事务管理
1.编程式事务管理
- 使用**PlatformTransactionManager**
- 使用**TransactionTemplate**
	- Spring对PlatformTransactionManager使用模板方法和Callback相结合的方式封装，即TransactionTemplate
	- TransactionCallback接口、TransactionCallbackWithoutResult接口
- 基于**Savepoint**的嵌套事务——将TransactionStatus作为SavepointManager

2.声明式事务管理（*Spring AOP的使用*）
- @Transactional
- 
```xml
<tx:annotation-driven transaction-manager="transactionManager"/>
```
### Chapter 21 Spring事务管理之扩展
#### 1.ThreadLocal
- 目的：通过避免对象的共享来保证应用程序中的线程安全
- 每个Thread类由类型为ThreadLocal.ThreadLocalMap名为threadLocals的实例变量

2.**Strategy模式**——封装一系列可以互相替换的算法逻辑，使得算法的演化独立于使用它们的客户端代码
- 只要能够有效剥离客户端代码与特定关系点之间的依赖关系，就可以考虑Strategy模式
- Spring框架在Strategy模式的使用：
	-  