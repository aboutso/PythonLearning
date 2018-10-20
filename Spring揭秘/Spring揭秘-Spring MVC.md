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
		![View](./images/1530372959428.png)
	- ```org.springframework.web.servlet.View```接口，统一抽象视图的生成策略
- 结构图
	![交互图](./images/1530373087589.png)
- WebApplicationContext
	![WebApplicationContext](./images/1530373845664.png)
2.对于前端Jsp，可以使用**DisplayTag**、**eXtrmeTable**等开源TagLib减少数据表格的实现工作量
### Chapter 24
1.HandlerMapping
-  HandlerMapping是接口，实现类：
	- BeanNameUrlHandlerMapping——视图模版中的请求路径和容器中Handler的beanName一致
	- SimpleUrlHandlerMapping
	-  ControllerClassNameHandlerMapping
	-  DefaultAnnotationHandlerMapping
- HandlerMapping的优先级规定遵循Spring Ordered接口的语义
2.Controller
	![Controller层次体系](./images/1530412387295.png)
- 以**BaseCommandController**为基类规范操作派：
	- 自动抽取request参数并绑定到Command
	- 统一的数据验证方式，```org.springframework.validation.Validator```
	- 规范化表单(Form)的处理流程
- **AbstractController**，通过模版方法模式解决通用的关注点：
	- 管理当前Controller所支持的请求类型(GET/POST)
	- 管理页面的缓存设置，即是否允许浏览器缓存当前页面
	- 管理执行流程在Session上的同步

### BaseCommandController及其之类在Spring 3及之上中都已取消
3.Spring数据验证框架
- 核心类```org.springframework.validation.Validator```——实现具体验证逻辑
- 核心类```org.springframework.validation.Errors```——承载验证中出现的错误信息

3.ModelAndView
- 使用**ViewResolver**根据逻辑视图名(String)获取View实例
- 使用```org.springframework.ui.ModelMap```保持模型数据

4.**ViewResolver**
- BeanNameViewResolver
- AbstractCachingViewResolver

4.1、面向单一视图类型的实现类
- 基类**UrlBasedViewResolver**
- InternalResourceViewResolver
- FreeMarkerViewResolver/VelocityViewResolver/JasperReportsViewResolver
 ### Spring 5已取消支持
- XsltViewResolver
- TilesViewResolver

4.2、面向多视图类型的实现类
- ResourceBundleViewResolver
	- 使用ResourceBundle的国际化支持能力，唯一提供视图国际化支持的ViewResolver
	- 逻辑视图与物理视图的映射关系保存在Properties文件中，格式符合Spring Ioc容器的Properties配置格式
- XmlViewResolver
	- 与ResourceBundleViewResolver区别是ResourceBundleViewResolver使用Properties文件，XmlViewResolver使用符合Spring Ioc 的XML格式
- BeanNameViewResolver
	- 直接将View实例注册到DispatcherServlet所使用的WebApplicationContext中
	- 用于快速搭建系统原型

5.View
- 基类```AbstractView```
- 基类```AbstractUrlBasedView```
- Jsp的View实现
	- InternalResourceView
	- JstlView
	- TilesView
- FreeMarkerView/VelocityView
### Spring 5已取消支持
- 二进制文档格式
	- AbstractExcelView——使用Apache POI构建Excel
	- AbstractJExcelView——使用JExcel API
	- AbstractPdfView——使用OpenPDF
- JasperReports的View实现
	- JasperReportsCsvView
	- JasperReportsHtmlView
	- JasperReportsPdfView
	- JasperReportsXlsView
- AbstractXlsView
- RedirectView——对指定的Url重定向
	![RedirectView工作原理](./images/1530420163332.png)
	- 简便方法：在逻辑视图名中使用redirect/forward前缀
### Chapter 25
![逻辑结构](./images/1530631168246.png)
1.MultipartResolver与文件上传
- 前端代码
```html
<form action="" method="POST" enctype="multipart/form-data">
	<input NAME="FileName" type="file"/>
	<input TYPE="Submit" VALUE="Upload"/>
</form>
```
- Spring提供抽象接口```org.springframework.web.multipart.MultipartResolver```
	- MultipartResolverde的**resolveMultipart**方法返回**MultipartHttpServletRequest**供后续处理流程使用
	- 两个实现类：**CommonsMultipartResolver** for Apache Commons FileUpload
	- 两个实现类：**StandardServletMultipartResolver** for the Servlet 3.0+ Part API
- 文件上传,Spring MVC提供两个**PropertyEditor**实现类：
	- ```org.springframework.web.multipart.support.ByteArrayMultipartFileEditor```
	- ```org.springframework.web.multipart.support.StringMultipartFileEditor```

2.Handler与HandlerAdapter
- Spring MVC中把任何可以处理Web请求的对象统称为**Handler**。Controller只是Handler的一种特殊类型
- HandlerExecutionChain中所返回的用于处理Web请求的对象,不只是Controller
- DispatcherServlet将不同Handler的调用职责转交给**HandlerAdapter**

3.HandlerInterceptor
- HandlerExecutionChainf返回一组HandlerInterceptor,可以在Handler执行前后对处理流程进行拦截操作

3.1.可用实现
- UserRoleAuthorizationInterceptor
	- Interceptor that checks the authorization of the current user via the user's roles, as evaluated by *HttpServletRequest's isUserInRole* method
- WebContentInterceptor
	- 检查请求方法是否在支持方法之列
	- 检查必要的Session实例
	- 检查缓存时间并通过设置相应Http Header的方式控制缓存行为

3.2 HandlerInterceptor之外的选择
- Servlet Filter
![HandlerInterceptor与ServletFilter](./images/20180705.png)
	- Filter在servlet层面对DispatcherServlet进行拦截
	- HandlerInterceptor在DispatcherServlet内部对Handler进行拦截
- Filter作为Servlet标准组件，在web.xml中配置,因而其生命周期由Web容器管理
- 为了能够使用Spring IoC功能，Spring MVC引入```org.springframework.web.filter.DelegatingFilterProxy```
![HandlerInterceptor与ServletFilter](./images/2018070501.png)

4.框架内异常处理与HandlerExceptionResolver
- **HandlerExceptionResolver**对异常的处理仅限于Handler查找和Handler执行期间
- 实现类```org.springframework.web.servlet.handler.SimpleMappingExceptionResolver```
	- 使用**Properties**管理具体异常类型与所要转向的错误页面之间的映射关系
	- SimpleMappingExceptionResolver根据类名而不是类型,使用String.IndexOf方法进行匹配
	- *此处有坑*

5.国际化视图与LocalResolver
- ViewResolverg根据逻辑视图名解析视图时,方法**resolveViewName(viewName,locale)***，根据不同Locale返回不同视图。
	- 此处locale参数的获取
- ```LocalResolver```接口对各种可能的Locale获取/解析方式进行统一的策略抽象
- 实现类```FixedLocalResolver```
- 实现类```AcceptHeaderLocaleResolver```——根据Http Header的**Accept-Language**
- 实现类```SessionLocalResolver```
- 实现类```CookieLocalResolver```

5.1 DispatcherServlet在初始化的时候获取LocalResolver实例,进而获取解析后的Locale值,以**LocaleContext**形式绑定到当前线程

5.2 **LocaleChangeInterceptor**

6.Theme
### Chapter 26 基于注解的Controller
6.1 本书中举例是**DefaultAnnotationHandlerMapping**，Spring 3之后使用```RequestMappingHandlerMapping```及```RequestMappingHandlerAdapter```

### Chapter 27 Spring MVC的*Convention Over Configuration*
1.Web请求与视图之间的约定

```RequestToViewNameTranslator```-Strategy interface for translating an incoming HttpServletRequest into a logical view name when no view name is explicitly supplied.