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
