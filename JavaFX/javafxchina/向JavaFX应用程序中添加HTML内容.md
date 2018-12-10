## 1. WebView组件概览
- 功能
    -  加载本地或者远程URL的HTML内容
    - 获取Web历史
    - 执行JavaScript指令
    - 执行由JavaScript向上调用JavaFX
    - 管理web上的弹出式（pop-up）窗口
    - 为内嵌浏览器应用特效
- **WebEngine类**——基本的web页面功能
    - 导航链接和提交HTML表单
    - 加载HTML内容和访问DOM对象
    - 执行JavaScript指令
- **WebView类**——Node类的一个扩展。它*封装了WebEngine对象*，将HTML内容加入程序的scene中，并且提供各种属性和方法来应用特效和变换
- PopupFeatures类——描述了JavaScript规范中定义的web弹出式（pop-up）窗口的功能
    ```Java
    webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
        @Override public WebEngine call(PopupFeatures config) {
            // do something
            // return a web engine for the new browser window
        }
    });
    ```
## 2. JavaFX web组件的当前版本支持以下HTML5特性
### *可以理解为是Web Engine具备功能，就内核具备的功能*
- Canvas和可缩放矢量图形（SVG）——基本的图形功能，包括渲染图形、使用SVG（Scalable Vector Graphics）语法构建阴影、应用颜色设置、可视化效果和动画
- 媒体重播——HTML5规范中的`video`和`audio`标签
- 表单控件
- 历史维护
    - 使用javafx.scene.web包中的**WebHistory类**来获取已访问网页的列表。
    - WebHistory类表示与WebEngine对象关联的会话历史。
- **交互式元素标记（Interactive element tags）**
## 陌生
- DOM
    - 通过使用WebEngine类的getDocument()方法访问文档模型的根元素
- Web workers(此功能使需要长时间运行的脚本能够在无需等待UI的情况下运行)
## 陌生
- Web sockets
## 陌生
- Web fonts
## 陌生
- 使用@font-face注解声明。使用该注解以后即链接上了字体，该字体会在需要时自动下载  
    ```CSS
        @font-face {
        font-family: "MyWebFont";
        src: url("http://example.com/fonts/MyWebFont.ttf")
        }
    ```
## 3. 处理JavaScript指令
- WebEngine类提供了在当前HTML页面的上下文中执行脚本的API
- WebEngine类的executeScript方法能够执行在加载的HTML页面声明的任何JavaScript指令。使用下面的代码在调用网页引擎上的这个方法：`webEngine.executeScript(“<function name>”)`;
- 该方法的执行结果会按下面的规则转化为java.lang.Object实例：
    - JavaScript的Int32会被转化为lang.Integer
    - JavaScript的number会被转化为lang.Double
    - JavaScript的string会被转化为lang.String
    - JavaScript的boolean会被转化为lang.Boolean
## 4. 完成JavaScript到JavaFX的调用
- 大体上的理念是在JavaFX程序中创建一个接口对象，并通过调用JSObject.setMember()方法使它对JavaScript可见。然后你就可以在JavaScript中调用该对象的public方法、访问public属性了
- ## 教程Demo *例6-1 通过使用JavaScript来关闭JavaFX程序* 未生效
## 5. 管理Web弹出式窗口
```Java
    webEngine.setCreatePopupHandler(
        (PopupFeatures config) -> {
            smallView.setFontScale(0.8);
            if (!toolBar.getChildren().contains(smallView)) {
                toolBar.getChildren().add(smallView);
            }
            return smallView.getEngine();
        });
```