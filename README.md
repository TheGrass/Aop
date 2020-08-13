***
 ####一个基于JCTree修改语法树实现的切面工具
***
#### 1、Maven
    <dependency>
        <groupId>de.imcq</groupId>
        <artifactId>aop</artifactId>
        <version>1.0.0</version>
    </dependency>
### 2、主要接口
| 接口  |  执行时机  |
| ---|----|
|`de.imcq.aop.component.BeforeAdvice`|方法执行前|
|`de.imcq.aop.component.AfterAdvice`|方法正常执行后|
|`de.imcq.aop.component.ThrowableAdvice`|方法抛出异常时|
|`de.imcq.aop.component.FinallyAdvice`|方法执行完毕|
   


### 3、注解
#### `@JoinPoint`
##### 标注在`类(所有方法生效，可使用@Ignore注解排除)`或`方法`上使用

| 属性  |  描述  |
| ---|----|
| value|指定Advice的实现类|
| support|指定使用的模式，默认：BEFORE，AFTER，FINALLY，THROWABLE|

#### `@DisableCache`
##### 标注在Advice的实现类上，该实例不使用缓存


### 4、Advice工厂类 AbstractAdviceFactory
#### `获取Advice实例的抽象工厂类，默认使用实现类 DefaultAdviceFactory,可以通过SPI自定义实现类`

### 5、上下文对象  AdviceContext
    /**
     * 设置共享变量
     */
    public void setShared(Object shared);
    /**
     * 获取共享对象
     */
    public Object getShared();
    /**
     * 执行方法所属类
     */
    public Class<?> getTargetClass();
    /**
     * 执行方法名称
     */
    public String getMethodName();
    /**
     * 执行方法返回值
     */
    public Object getReturnValue();
    /**
     * 执行方法参数
     */
    public Object[] getArgs();
    /**
     * 执行方法抛出的异常
     */
    public Throwable getThrowable();
    /**
     * 方法开始执行时的纳秒数
     */
    public long getStartTick();

### 5、示例
#### `根据自己的需求实现特定的Advice接口`

        public class SampleAdvice implements BeforeAdvice, AfterAdvice, FinallyAdvice, ThrowableAdvice {
            @Override
            public void afterAdvice(AdviceContext context) {}
            @Override
            public void beforeAdvice(AdviceContext context) {}
            @Override
            public void finallyAdvice(AdviceContext context) {}
            @Override
            public void throwableAdvice(AdviceContext context) {}
        }
####`在需要的类或方法上使用@JoinPoint注解`
<pre>
    /**
     * 所有方法
     */
    @JoinPoint(SampleAdvice.class)
    public class Sample {
        private void sample1() {}
        
        /**
         * 排除该方法
         */
        @JoinPoint.Ignore
        private void sample2() {}
        
        /**
         * 该方法使用其他的Advice
         */
        @JoinPoint(AnotherAdvice.class)
        private void sample3() {}
    }
</pre>
<pre>
    public class Sample {
        @JoinPoint(SampleAdvice.class)
        private void sample() {}
    }
</pre>