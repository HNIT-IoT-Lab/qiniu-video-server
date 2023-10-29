```java
代码千万行，规范第一行
代码不规范，亲人两行泪
```

## 模块划分

```java
+---hnit-assembly      // 网关服务 [8080]
+--- hnit-entity        // 公共实体，异常类
+--- hnit-Interceptor   // 拦截器
+--- hnit-starter       // 自动装箱类，存放自定义插件配置
+--- hnit-utils         // 公共工具类
+--- hnit-common        // 通用模块
|    +--- hnit-common-core           	// 核心模块
|    +--- hnit-common-knife4j           // 集成knife4j
|  
|  
|  
```

