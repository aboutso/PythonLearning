## springboot(二)：web综合开发
### 自定义Filter
- 实现Filter接口，实现Filter方法
- 添加@Configuration 注解，将自定义Filter加入过滤链
### 自定义Property
- 配置在application.properties中
- @Component和@Value(properties的key)
### thymeleaf模板
- 与其它模板引擎相比，Thymeleaf最大的特点是能够直接在浏览器中打开并正确显示模板页面，而不需要启动整个Web应用。
- **页面即原型**——Thymeleaf通过属性进行模板渲染不会引入任何新的浏览器不能识别的标签，例如JSP中的在Tag内部写表达式。
    - 整个页面直接作为HTML文件用浏览器打开，几乎就可以看到最终的效果
### Gradle 构建工具
### **WebJars**——以jar包的形式来使用前端的各种框架、组件
- WebJars是将客户端（浏览器）资源（JavaScript，Css等）打成jar包文件，以对资源进行统一依赖管理。WebJars的jar包部署在Maven中央仓库上。
## springboot(三)：Spring boot中Redis的使用

## springboot(四)：thymeleaf使用详解
**跳过**
## springboot(五)：spring data jpa的使用
### 根据方法名生成SQL语句
### 分页查询
- Page
- Pageable
```Java
//接口定义
Page<User> findALL(Pageable pageable);
    
Page<User> findByUserName(String userName,Pageable pageable);

//使用
@Test
public void testPageQuery() throws Exception {
    int page=1,size=10;
    Sort sort = new Sort(Direction.DESC, "id");
    Pageable pageable = new PageRequest(page, size, sort);
    userRepository.findALL(pageable);
    userRepository.findByUserName("testName", pageable);
}
```
### 限制查询
```Java
Userr findFirstByOrderByLastnameAsc();

User findTopByOrderByAgeDesc();

Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);

List<User> findFirst10ByLastname(String lastname, Sort sort);

List<User> findTop10ByLastname(String lastname, Pageable pageable);
```
### 自定义SQL查询
- @Modifying
- @Query
- @Transactional
```Java
@Modifying
@Query("update User u set u.userName = ?1 where u.id = ?2")
int modifyByIdAndUserId(String  userName, Long id);
	
@Transactional
@Modifying
@Query("delete from User where id = ?1")
void deleteByUserId(Long id);
  
@Transactional(timeout = 10)
@Query("select u from User u where u.emailAddress = ?1")
    User findByEmailAddress(String emailAddress);
```
### **多表查询**
- 利用hibernate的级联查询来实现
- 创建一个结果集的接口来接收连表查询后的结果(下面讲述)
    - 定义一个结果集的接口类
    ```Java
    public interface HotelSummary {

        City getCity();

        String getName();

        Double getAverageRating();

        default Integer getAverageRatingRounded() {
            return getAverageRating() == null ? null : (int) Math.round(getAverageRating());
        }
    }
    ```
    - 查询的方法返回类型设置为新创建的接口
    ```Java
    @Query("select h.city as city, h.name as name, avg(r.rating) as averageRating "
		- "from Hotel h left outer join h.reviews r where h.city = ?1 group by h")
    
    Page<HotelSummary> findByCity(City city, Pageable pageable);

    @Query("select h.name as name, avg(r.rating) as averageRating "
		- "from Hotel h left outer join h.reviews r  group by h")
    Page<HotelSummary> findByCity(Pageable pageable);
    ```
    - 使用
    ```Java
    Page<HotelSummary> hotels = this.hotelRepository.findByCity(new PageRequest(0, 10, Direction.ASC, "name"));
    for(HotelSummary summay:hotels){
		System.out.println("Name" +summay.getName());
	}

    /**
    * 在运行中Spring会给接口（HotelSummary）自动生产一个代理类来接收返回的结果，代码汇总使用getXX的形式来获取
    */
    ```
    ### 使用枚举
    - 希望数据库中存储的是枚举对应的String类型，而不是枚举的索引值
    - ```@Enumerated(EnumType.STRING)```
    - 示例
    ```Java
    @Enumerated(EnumType.STRING) 
    @Column(nullable = true)
    private UserType type;
    ```
    ### 不需要和数据库映射的属性
    - @Transient

## springboot(八)：RabbitMQ详解
使用activeMQ作为案例已实现
## springboot(九)：定时任务
- 启动类```@EnableScheduling```
- 定时方法```@Scheduled```

## springboot(十)：邮件服务
# **跳过**
## springboot(十一)：Spring boot中mongodb的使用

## springboot(十三)：springboot小技巧
### 初始化数据
- 使用Jpa

在使用spring boot jpa的情况下设置spring.jpa.hibernate.ddl-auto的属性设置为 create or create-drop的时候，spring boot 启动时默认会扫描classpath下面（项目中一般是resources目录）是否有import.sql，如果有机会执行import.sql脚本。

- 使用Spring JDBC

使用Spring JDBC 需要在配置文件中添加以下配置

spring:  
&emsp;&emsp;datasource:  
&emsp;&emsp;&emsp;schema: database/data.sql  
&emsp;&emsp;&emsp;sql-script-encoding: utf-8  
&emsp;&emsp;jpa:  
&emsp;&emsp;&emsp;hibernate:  
&emsp;&emsp;&emsp;&emsp;**ddl-auto**: none

schema ：设置脚本的路径  
sql-script-encoding：设置脚本的编码  
spring boot项目启动的时候会自动执行脚本。

**ddl-auto** 四个值的解释:
>create： 每次加载hibernate时都会删除上一次的生成的表，然后根据你的model类再重新来生成新表，哪怕两次没有任何改变也要这样执行，这就是导致数据库表数据丢失的一个重要原因。  
create-drop ：每次加载hibernate时根据model类生成表，但是sessionFactory一关闭,表就自动删除。  
update：最常用的属性，第一次加载hibernate时根据model类会自动建立起表的结构（前提是先建立好数据库），以后加载hibernate时根据 model类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。要注意的是当部署到服务器后，表结构是不会被马上建立起来的，是要等 应用第一次运行起来后才会。  
validate ：每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。  
none : 什么都不做。
### 随机端口
```XML
server.port=0
eureka.instance.instance-id=${spring.application.name}:${random.int}
```

```
server.port=${random.int[10000,19999]}
```

