## **Package** `javafx.concurrent`
1. javafx.concurrent包提供的JavaFX API，可以处理好多线程代码和UI的交互，并且可以保证这个交互发生在正确的线程上
- **Worker接口**——提供了对后台工作与UI之间通信有用的API
    - 定义了一个执行一个或多个后台线程工作的对象
    - Worker对象的状态在JavaFX应用程序线程中是可观察且可用的
    - 创建时，Worker对象是READY状态。开始计划工作时，Worker对象转变为SCHEDULED状态。之后当Worker对象执行工作时，状态变为RUNNING。
    - 注意当Worker对象没有计划而直接开始时，状态也是先转变为SCHEDULED再转为RUNNING状态，并且value属性也设置到Worker对象的结果中。
    - 如果任何异常在Worker对象执行过程中抛出，状态变为FAILED并且exception属性也会设置为产生的异常类型。
    - 在Worker对象结束前的任何时候，开发者都可以调用cancel方法来终止，此时状态将变为CANCELLED。
    - Worker对象的工作完成过程可以通过三种不同的属性获取，totalWork，workDone和progress
- **Task类**是java.util.concurrent.FutureTask类的一个完整的可观察的实现。
    - Task类使开发者可以在JavaFX应用程序中实现异步任务。用来实现需要在后台完成的工作的逻辑。
    - 实现Task的类必须重写`call方法`来处理后台工作和返回结果。
    - call方法在后台进程中被调用，因此这个方法只能操作后台线程读写安全的状态。
    - call方法内部，你可以使用`updateProgress`，`updateMessage`，`updateTitle`方法，这些方法可以在JavaFX应用程序线程更新对应属性的值。
    - 如果任务被取消时，call方法中的返回值会被忽略掉。
    - Task类兼容Java并发库是因为它继承`java.utils.concurrent.FutureTask`类，此类实现了Runnable接口。因此Task对象可以在Java并发Executor API中使用，也可以作为参数传递给线程。你可以使用FutureTask.run()方法来直接调用Task对象，这样可以在另一个后台线程执行该任务。
    - Task类定义了一个一次性且不能重用的对象。如果你需要一个可以重用的Worker对象，使用`Service类`。
- **Service类**
    - 设计来在一个或多个后台线程上执行一个Task对象
    - 可以按需启动，取消和重启服务
    - **Service类**可以按以下其中一种执行:
        - 可以被一个特定服务指定的Executor对象执行
        - 如果没有执行器被指定，则由守护进程执行
        - 由自定义的执行器执行，如ThreadPoolExecutor
- **WorkerStateEvent类**指定了一个工作实现状态发生变化时的事件。
    - Task和Service类都实现了EventTarget接口，因此可以支持监听这个状态事件。
- **WorkerStateEvent类**和状态转换
    - 每当Worker实现的状态改变时，由WorkerStateEvent类定义的一个适当的事件将会发生。
    - 受保护的便利的方法有几种，如cancelled，failed，running，scheduled和succeeded，当Worker实现转换到对应状态时将会被调用。
- **ScheduledService类**
    - ScheduledService类代表了一个在成功执行后，或者在特定情况下失败后，可以*自动重启的服务*。
    - 当ScheduledService对象被创建时，即为READY状态。
    - 在调用ScheduledService.start()或者ScheduledService.restart()方法后，ScheduledService对象在delay属性定义的范围内变为SCHEDULED状态。
    - 在处于RUNNING状态时，ScheduledService执行其任务。
    - 在任务完成后，ScheduledService对象转变为SUCCEEDED状态，然后变为READY状态，然后再变为SCHEDULED状态。
        - 再次变为SCHEDULED状态的周期取决于最后一次转变为RUNNING状态的时间、现在的时间、和定义相邻两次运行时间的最小值的period属性值。如果前一次运行结束早于周期失效，ScheduledService对象会保持在SCHEDULED状态，直到周期失效。否则如果前一次执行比指定的周期更长，ScheduledService对象会立刻转变为RUNNING状态。
    - 当任务以FAIL状态结束的情况下，ScheduledService对象会重启或者退出，这取决于**restartOnFailure**，**backoffStrategy**和**maximumFailureCount**属性的值。
        - **restartOnFailure**属性为false，ScheduledService对象会转变为FAILED状态并退出
        - 如果restartOnFailure属性是true，ScheduledService对象会转变为SCHEDULED状态，并在cumulativePeriod属性定义的周期内保持此状态，cumulativePeriod属性可以通过调用backoffStrategy属性获取到结果。使用cumulativePeriod属性，你可以强制已失败的ScheduledService在下一次运行前等待更长。
        - 在ScheduledService成功完成后，cumulativePeriod属性会被重置为period属性的值。
        - 当失败数量达到maximumFailureCount属性的值时，ScheduledService对象会转变为FAIL状态然后退出。
- 在ScheduledService对象运行时，delay和period属性发生任何变化都会在下一次循环中生效。delay和period属性的默认值为0