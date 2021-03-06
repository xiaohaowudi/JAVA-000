# 知识要点总结
# 1. JVM基本知识
## 1.1 Java语言本身的特点
面向对象的基于JVM虚拟机的半编译半解释型语言， 支持GC垃圾回收，支持二进制跨平台, 生态完整，生命力强
Java相对C++, Rust等其他语言的优势和劣势：
具备比较完善的GC机制，将内存的管理和回收都由JVM完成，对象的生命周期由JVM管理，设计哲学上讲，完全不信任程序员对内存的管理行为，一方面讲，将开发者从C++那样复杂的内存管理中解放出来，能够更多关注在业务逻辑上，但是同时也引入了GC的开销，在GC使用不当情况下会影响业务效率

## 1.2 Java程序运行基本机制
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/Java%E7%A8%8B%E5%BA%8F%E8%BF%90%E8%A1%8C%E6%9C%BA%E5%88%B6.png" width="35%" height="35%" />


应用开发者利用java编译器将java代码编译为字节码，每一个类对应一个字节码文件，程序运行时候首先是JVM虚拟机进程启动，作为所有Java程序的运行容器，由类装载器将类的字节码加载到内存中，加载到内存中的类就可以生成响应的对象，各个对象在特定的Java线程中运行自己的业务逻辑

## 1.3 字节码技术
### 1.3.1 字节码的作用
Java程序本质上运行机制是由JVM执行Java源文件生成的字节码，可类比C++层序运行时候是由CPU运行汇编指令来理解，字节码本质就是JVM指令的二进制表现，通常通过类似于汇编的助记符提供给开发者阅读，可以通过javap等工具将二进制字节码反编译为助记符，开发者可通过程序的字节码分析代码逻辑并定位一些性能问题，JVM的字节码操作码由一个字节表示，至多支持256中不同操作码，除了一部分留给调试使用以外，现在已经使用的JVM指令操作码大概200个左右

### 1.3.2 字节码分类
a. 栈操作指令，作用是操作栈帧中的栈结构和局部变量表，完成计算功能

b. 流程控制指令，完成goto, for, if等判断逻辑

c. 对象操作和方法调用指令

典型的方法调用指令:

&nbsp;&nbsp;&nbsp;&nbsp;**invokestatic** 用于调用静态方法

&nbsp;&nbsp;&nbsp;&nbsp;**invokespecial** 用于调用构造函数

&nbsp;&nbsp;&nbsp;&nbsp;**invokevirtual** 用于对象引用调用多态

&nbsp;&nbsp;&nbsp;&nbsp;**invokeinterface** 用于接口调用方法

&nbsp;&nbsp;&nbsp;&nbsp;**invokedynamic** JDK7新增加，用于实现动态类型语言，lambda表达式实现的基础指令

d. 算数运算和类型转换

### 1.3.3 JVM执行字节码的核心内存结构

*栈帧结构和load/store机制*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/load:store%E6%9C%BA%E5%88%B6.png" width="35%" height="35%" />
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/Java%E6%A0%88%E5%B8%A7%E7%BB%93%E6%9E%84.png" width="35%" height="35%" />


a. **栈结构** 栈结构是线程栈帧中一部分，类似于CPU中ALU的功能，用于缓存运算需要的参数，一般字节码运行时候基于栈的后入先出的机制读取参数，参数读取完之后进行弹栈，指令运行完后，如果有运行结果，将结果存储于栈顶，后面的指令从栈顶读取前面指令的运行结果，对于栈操作分为load和store两种，load类指令将局部变量表中的数值加载到栈顶，store类指令将栈顶数据弹栈并根据槽位号存储到局部变量表的对应位置

b. **局部变量表** 线程栈帧的组成部分，类似于CPU中通用寄存器的功能，用于保存临时变量，每一个槽位都绑定一个局部变量，本质是内存中的一个数组，指令运行时候数据会在栈和局部变量表之间以load/store方式进行交互


## 1.4 类加载器原理

### 1.4.1 Java类的声明周期
类加载器本质功能是通过一系列步骤将字节码文件加载到JVM内存中，供JVM使用，典型的类的生存周期有7个阶段：
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/Java%E7%B1%BB%E7%9A%84%E7%94%9F%E5%AD%98%E5%91%A8%E6%9C%9F.png" width="75%" height="75%" />
1. 加载 根据ClassPath等信息查找Class文件
2. 验证 验证字节码文件的格式和依赖
3. 准备 构造静态字段，方法表
4. 解析 将符号解析为真正的引用
5. 初始化 静态变量赋值，静态代码块执行
6. 使用 具体线程中对类进行使用
7. 卸载

### 1.4.2 类加载的场景和时机
1. 程序入口执行静态的main方法时候，会触发main函数所在的类的加载
2. 调用new创建类A对象时候，会触发加载类A
3. 调用类A的静态方法时候，会触发加载类A
4. 访问类A的静态数据字段时候，会触发加载类A
5. 子类被加载时候会先触发其父类的加载
6. 如果接口中实现了default方法，直接或者间接实现了该接口的类被加载时候会先触发该接口类的加载
7. 用反射API对类进行操作时候，会触发该类的加载
8. 初次调用MethodHandle时候，会触发该MethodHandle指向的方法所在的类的加载

### 1.4.3 类加载但是不初始化的场景
1. 子类引用了父类的静态字段，会触发父类的初始化，但是不会触发子类本身的初始化
2. 创建类A的对象数组(本质是构造引用数组), 类A会被加载，但是不会触发初始化
3. 对常量的引用不会触发该常量所在的类的初始化，因为常量本质是存放在常量池的，其数值在编译器已经确定，不依赖于其所在类的初始化
4. 通过类A.class方式引用A的Class对象，不会直接触发类A的初始化，除非用该Class对象实例化该类的对象或者访问了该类的静态字段或者方法
5. Class.forName加载指定类时候，如果initialize参数传递false，不会触发该类的初始化
6. 通过ClassLoader默认的loadClass方法加载类，不会触发类的初始化

### 1.4.4 类加载器的3种分类
1. **启动类加载器(BootstrapClassLoader)** 加载JVM依赖的最核心的系统类，例如rt.jar包中的类
2. **拓展类加载器(ExtClassLoader)** 加载拓展类路径下的类
3. **应用类加载器(AppClassLoader)** 加载应用开发者自己编写的类或者jar包

### 1.4.5 类加载器的运行原则
1. **双亲委托** 当前级别的类加载器加载类时候会先查看其上一级的类加载器有没有加载对应的类，如果已经加载了则直接使用

*类加载器的层级关系*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8%E7%9A%84%E5%B1%82%E7%BA%A7%E5%85%B3%E7%B3%BB.png" width="60%" height="60%" />


*类加载器具体实现类的继承关系*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E7%B1%BB%E5%8A%A0%E8%BD%BD%E5%99%A8%E5%AE%9E%E7%8E%B0%E7%B1%BB%E7%9A%84%E7%BB%A7%E6%89%BF%E5%85%B3%E7%B3%BB.png" width="60%" height="60%" />


2. **负责依赖** 加载一个类时候，其依赖的所有类也必须被加载
3. **缓存加载** 对于同一个类加载器而言，其已经加载的类只会被加载一次，第二次会从缓存中读取直接使用

## 1.5 JMM内存模型
### 1.5.1 线程的内存模型

*JVM线程的内存模型*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/JVM%20%E7%BA%BF%E7%A8%8B%E7%9A%84%E5%86%85%E5%AD%98%E7%BB%93%E6%9E%84.png" width="60%" height="60%" />


**线程中关于内存操作的原则：**
1. 每一个线程持有自己的栈帧，所有原生类型的局部变量都存储在每个线程自己的栈帧中，A线程的局部变量对于B线程不可见
2. JVM进程的堆空间对所有该JVM中的线程可见，所有线程创建的对象全部都保存在堆空间中，每一个线程都使用引用方式对堆中的对象进行访问，每一个线程对于堆中对象的引用变量也是互相独立的
3. 对象内部的字段不论是原生字段还是引用类型，均存储在堆中
4. 类的静态字段保存于堆中
5. 每个线程通过引用访问同一个对象的字段时候，会在线程自己的栈中先拷贝副本，如果有修改会在修改后回写到堆中，所以两个线程同时对一个对象进行的操作，各自有各自的缓存，互相对于对方的读写行为是不可见的

### 1.5.2 JVM进程总体内存模型
*JVM进程内存模型结构*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/JVM%E5%A0%86%E5%86%85%E5%AD%98%E6%80%BB%E4%BD%93%E7%BB%93%E6%9E%84.png" width="60%" height="60%" />

JVM进程内存模型各组成部分：
1. **栈Stack** 用于给每个线程分配栈帧的内存空间
2. **堆Heap** 用于存放JVM进程中所有线程创建的对象，其中按照对象的生存周期状态分为新生代，老年代， 新分配的对象一般存活于Eden区，当Eden区满了，执行YnagGC时候，会将Eden中存活对象放到S区中当前活动的区域，另个S区始终保持为空，同样S区中非存活对象也会被清理掉，清理后，Eden区全部清空，S区会减少一些生命周期结束的对象，多一些内存碎片，也会多一些从Eden生存下来的对象，如果发生了S区放不下生存对象的情况下，所有生存下来对象会统一搬移到另外一个原来为空的S区去，消除内存碎片，然后两个S区倒换，交换身份；经过一定次数GC后一直生存的对象会从新生代搬移到老年代中去

3. **非堆Non-Heap** 包含CCS, CodeCache等部分, JDK8之后永久代改为Metaspace，位置移动到了非堆空间中，用于保存常量和方法的代码段等不变的信息

*JMV启动参数和对应内存区域之间的关系*

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/JMM%E9%85%8D%E7%BD%AE%E5%8F%82%E6%95%B0%E5%85%B3%E7%B3%BB.png" width="80%" height="80%" />

# 2. JVM中常用性能诊断工具
1. jps 常用jsp -lvm查看JVM进程的一些配置信息
2. jinfo 查看JVM进程参数信息
3. jstat -gc pid 定时打印GC的运行情况，可观察内存分区的容量，使用量，GC执行次数和时间
4. jstat -gcutil pid 以内存使用比例为单位定时打印GC运行情况
5. jmap -histo pid 打印JVM进程中类的实例数量以及内存占用情况
6. jmap -heap pid 打印堆空间分配比例和使用情况
7. jstack -l pid 打印JVM进程中每一个线程的调用栈和持有锁的情况
8. jcmd 比较综合的命令， 各种VM参数，线程参数GC状态等都可以打印
9. jconsole, jvisualvm, visualGV 一些图形化工具和插件，可以动态观察JVM的状态，并做一些性能分析

# 3. JVM中的GC
## 3.1 GC的一般性原理
### 3.1.1 GC基本流程
Java中内存管理是托管给JVM进行的，开发者不需要在申请完内存之后主动进行释放操作，因此JVM内部实现了一套管理内存，回收垃圾内存的机制，以保证系统中废弃的对象占用的内存能及时得到回收，保证整个系统是有空闲内存可以使用的

传统的一种比较朴素的内存管理方式是引用计数，系统记录每一个对象被外部引用的次数，对于引用计数为0的对象判定其为垃圾对象，对其占用的内存进行回收，但是无法解决复杂依赖关系中的循环引用问题；改进的方式是引用跟踪，从当前一些一定存活的根对象出发，遍历其可达的引用对象，将遍历到的对象标记为活跃，遍历不到的对象自然认为是垃圾对象，由于循环引用的一部分对象会性能一个外部无法达到的封闭连通分量，因此通过引用跟踪的方式，标记不到这些循环引用的对象，这些对象也就自然被认为是垃圾对象，巧妙解决了循环依赖的处理问题

现代的GC一般都采用的是标记跟踪方式，总体分为3个步骤：
1. **标记 Marking** 先标记出从根对象出发可达的对象
2. **清除 Sweeping** 将未标记到的对象删除
3. **压缩 Compack** 压缩内存碎片

### 3.1.2 Stop The World机制 
由于对象之间的引用关系是不断在变化的，因此为了准确标注出活动对象，需要将业务线程停止一段时间，保持对象间的引用关系不变，然后进行标注和清除工作，甚至是在压缩工作也完成之后，在恢复业务线程的运行 

### 3.1.3 分代回收思想
分代回收思想基于一个假设，大量的对象都是生存周期很短的对象，经过GC之后存活下来的对象在接下来的GC中存活下来的概率也比较高，所以可以将对象分为生命周期短的和生命周期长的，分别放在新生代和老年代，由于GC标记的是存活对象，对于新生代中的标记工作就会很快完成，因为新生代中存活的对象非常少，反之对于老年代的标注工作会比较耗时， 所以新生代的GC会比老年代频繁，在经过YongGC一定次数之后还生存的对象就会被放入老年代(默认JVM中该次数是15次)，现代的Java垃圾回收，对于新生代和老年代会采取不同的策略

### 3.1.4 可以作为引用跟踪的根对象
1. 当前正在执行的方法的局部变量和入参
2. 活动的线程对象
3. 所有类的静态字段
4. JNI引用

### 3.1.5 GC中的三种算法策略
1. 标记清除算法 将所有存活对象标记，然后把未标记的对象全部删除，会留下大量内存碎片
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E6%A0%87%E8%AE%B0%E6%B8%85%E9%99%A4%E7%AE%97%E6%B3%95.png" width="60%" height="60%" />

2. 标记复制算法 将所有存活对象标记，把存活对象全部复制到存活区，原来保存对象的区域全部清空成空闲对象(一般新生代采用此策略)
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E6%A0%87%E8%AE%B0%E5%A4%8D%E5%88%B6%E7%AE%97%E6%B3%95.png" width="60%" height="60%" />

3. 标记清除整理算法 将存活对象标记，把未标记对象全部删掉，然后再原区域做对象搬移，压缩内存碎片, 由于大量挪动对象，速度较慢(一般老年代采用此策略)
<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/%E6%A0%87%E8%AE%B0%E6%95%B4%E7%90%86%E7%AE%97%E6%B3%95.png" width="60%" height="60%" />

## 3.2 JVM具体GC算法
### 3.2.1 串行GC
新生代采用标记复制策略处理，老年代采用标记清除整理算法处理，两个GC都是单线程，不能并行处理GC任务，进行GC时候必须进入STW状态，GC完成后所有业务线程再恢复执行，暂停时间长，只能在堆比较小的JVM进程上使用，早期的GC算法，现在基本不使用

### 3.2.2 ParNew GC
串行GC的改进版本，在串行GC基础上将单线程改进为多线程并行进行GC操作，依然需要所有业务线程进入STW状态，其他无改进，现代一般配合CMS使用

### 3.2.3 并行GC
新生代采用标记复制策略处理，老年代采用标记清除整理算法处理，需要应用线程STW，但是充分利用了多核资源，多线程并行进行GC操作，效率比较高，而且只有在GC发生时候才会有GC线程产生，在两次GC间隔时间内，JVM进程中没有gc线程，减少了系统消耗，JDK8默认启用的就是并行GC

### 3.2.4 CMS GC
新生代采用多线程并行标记复制策略处理，老年代采用并发标记清除策略

新生代的GC需要业务线程进入STW状态，然后进行GC工作

老年代的GC不停止工作线程，而是和业务线程并发执行，最后也不进行老年代的碎片压缩操作，而是使用free-list结构维护老年代可用的内存，整体目标是降低老年代GC中的卡顿，默认情况下，CMS GC使用并发线程数是CPU核心数都的1/4，对比并行GC而言，老年代GC停顿时间比较短，但回收效率相对低一些，老年代会遗留一些内存碎片

CMS GC的6个阶段：
1. **初始标记(Initial Mark)** 将根对象出发的第一个可达的对象标记出来
2. **并发标记(Concurrent Mark)** GC线程和业务线程并发执行，标记根对象出发的第一个对象的接下来的引用对象，不用暂停业务线程
3. **并发预清理(Concurrent Preclean)** GC线程和业务线程并发运行，将已经变化的引用关系标注为脏区域，不用暂停业务线程
4. **最终标记(Final Remark)** 业务线程进入STW状态，对前面标注的活动对象进行校正，修正前面的标注错误
5. **并发清除(Concurrent Sweep)** 将没有标注到的垃圾对象清除， 不需要暂停业务线程
6. **并发重置(Concurrent Reset)** 重置算法内部的状态，为下一次GC做准备

### 3.2.5 G1 GC
G1 GC 设计目标是让STW状态出现的时间长度和频次分布变为可控的，可以为G1 GC设置预期的性能指标，让G1 GC在运行期间不断调整GC的触发条件和每次的执行时间，以达到预期性能目标，是一种启发式的GC；堆区不再固定分为新生代和老年代，而是分割成一系列小的内存区域region, 每一个region的身份是动态的，可能在新生代，老年代，存货区等不同身份中变动

G1 GC对于回收内存的多少有一定容忍度，并不像并行GC或者CMS一样必须每次GC都把能回收的内存全部或回收掉，垃圾多的Region会优先回收，一般GC时候收集所有年轻代的region中的垃圾，但只回收一部分老年代中的垃圾，整个GC效率比较有弹性，功能上看是处于并行GC和CMS中间地带的一种GC

### 3.2.7 ZGC
JDK11 中新开发的GC，在JDK11中只支持Linux系统，适合在大堆场景下使用，GC最大停顿时间不超过10ms，总体上保证大量GC操作都可以并发和业务线程一起运行，大量减少了业务线程卡顿，但是GC频率比较高，总体吞吐量相比G1有所下降

### 3.2.8 各种GC的特性对比

<img src="https://github.com/xiaohaowudi/JAVA-000/blob/main/Week_01/images/GC%E7%89%B9%E6%80%A7%E5%AF%B9%E6%AF%94.png" width="60%" height="60%" />

### 3.2.7 新生代和老年代使用的GC策略组合
不同的GC算法有不同的特点，总体上讲一部分GC算法侧重于每一次GC都尽量把垃圾回收完，这样整个系统空闲资源会比较多，但是GC上会消耗较多时间，延迟相对会高一些，但是整个系统的吞吐量是比较大的，典型的就是并行GC；反之，一部分GC算法更侧重于减少每一次GC的实延，让业务线程尽量少暂停，典型的是CMS，这样的策略会让延迟降低，GC回收效率会相对低一些，不同的GC算法组合都是在延迟和吞吐量两个指标上进行权衡
常见的GC组合有如下几种：
1. 新生代使用Serial GC，老年代使用Serial Old GC 目标是实现单线程的低延迟回收
2. 新生代使用ParNew, 老年代使用CMS 目标是实现多线程的低延迟回收
3. 新生代使用Parallel Scavenge GC, 老年代使用Parallel SACvengeance Old GC, 目标是实现高吞吐量垃圾回收


### 小结
本周简单学习了JVM的字节码原理，类装载器原理和JVM内存模型，在读书时候曾经接触过这些知识，但是没有非常深入去学习，在老师讲解之后，自己动手写了几个实例程序用javap进行解析然后分析其字节码，其实Java字节码系统相对于硬件CPU的指令集系统已经简单了非常多，Java字节码指令集本身很精简，LOAD STORE机制以及局部变量表配合一个内部栈进行计算，跟CPU中通用寄存器组配合ALU做算数运算的原理如出一辙。JMM内存模型相对以前有了一些更新换代，但是本质设计思想还是没有大的变化，趁着训练营机会正好把这些年没怎么关注的Java这一块的更新内容学习一下，课程部分内容是自己盲区，不太了解，通过老师讲解和课后查阅资料已经进行了补习，后续还有很多内容需要跟随老师学习，希望后面能安排好时间，一直跟上进度。
