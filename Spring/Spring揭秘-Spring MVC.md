## Spring揭秘-Spring MVC
### Chapter 22
1.Web框架类型：
- 请求驱动类型——Struts、Spring MVC
- 事件驱动类型——JSF(Java Server Faces，Asp.net)

### Chapter 23
1.目录结构
![目录结构](./images/1530373992016.png)
2.Controller结构
		![Controller](./images/1530372161625.png)
3.DispatcherServlet处理流程：
- ```org.springframework.web.servlet.HandlerMapping```专门管理Web请求与处理类之间的Mapping映射关系
- ```org.springframework.web.servlet.mvc.Controller```Web请求的实际处理者
	- 返回```org.springframework.web.servlet.ModelAndView```实例
- ViewResolver和View
	- View
		![Viewe](./images/1530372959428.png)
	- ```org.springframework.web.servlet.View```接口，统一抽象视图的生成策略
- 结构图
	![交互图](./images/1530373087589.png)
- WebApplicationContext
	![WebApplicationContext](./images/1530373845664.png)
2.对于前端Jsp，可以使用**DisplayTag**、**eXtrmeTable**等开源TagLib减少数据表格的实现工作量

