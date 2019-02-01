## 在Swing应用程序中整合JavaFX
1. JavaFX SDK提供了`JFXPanel类`，位于javafx.embed.swing包中，可以使你在**Swing应用程序中嵌入JavaFX内容**。
    - 创建一个JFXPanel类的实例隐式启动了JavaFX运行时。
    - GUI创建后，调用initFX方法在JavaFX应用程序线程上创建JavaFX场景。