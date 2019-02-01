1. 绑定由一个或多个源组成，这些源被称之为依赖(dependencies)。一个绑定会观察其所有依赖的变化，并且会在一个变化被检测到后自动更新自己。
### 理解属性
2. JavaFX中对属性的支持是基于著名的JavaBeans组件结构的属性模型
### Observable, ObservableValue, InvalidationListener和ChangeListener
3. Observable与ObservableValue接口触发改变通知，而InvalidationListener和ChangeListener接口接收通知。
    - ObservableValue包装了一个值并触发此值的改变到任何已注册的ChangeListener上
    - Observable(并未包装一个值)是触发改变到任何已注册的InvalidationListener上
4. JavaFX绑定与属性的实现都支持延迟计算(lazy evaluation)，意思是当改变发生时值并不是立即重新计算。当此值随后被请求时才进行重新计算 


---
---
---
## 讲的很简略