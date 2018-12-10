## 使用内置的布局面板
1. BorderPane
- BorderPane常用于定义一个非常经典的布局效果：上方是菜单栏和工具栏，下方是状态栏，左边是导航面板，右边是附加信息面板，中间是核心工作区域。
![BorderPane](./BorderPane.png)
2. HBox
    - 设置内边距(Padding)属性可以用于管理节点到HBox边缘的距离
    - 设置间距(Spacing)属性可以用于管理节点之间的距离
    - 设置外边距(Margin)属性可以为单个控件周围增加额外的空间
    - 设置样式(Style)属性可以改变背景颜色
3. VBox
4. StackPane
5. GridPane
6. FlowPane
7. TilePane
8. AnchorPane
## 调整节点大小和对齐的技巧
9. 当布局面板的大小变化时，节点也会根据其预设大小的范围来调整大小
    - UI控件(Control)和布局面板(Layout Pane)是可以调整大小的
    - 但是*形状(Shape)*、*文本(Text)* 以及*组(Group)* 是**不可以**调整大小的，它们在布局中被认为是刚性对象(Rigid Objects)
    - 如果你希望对UI中的控件大小有更多的控制，你可以直接设置它们的预设大小(Preferred Size)范围。然后布局面板就会根据你的设置来计算和决定控件的大小。如果希望管理控件的位置，你可以使用布局面板的对齐属性(Alignment)属性