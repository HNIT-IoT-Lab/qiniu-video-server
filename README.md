# VidBurst

## 项目介绍

VidBurst（结合了"vid"和"burst"，意为视频和瞬间的结合），一个短视频应用。**Web** 端使用 **Vue** + **ElementUi**，后端使用  **SpringBoot** + **Mybatis-plus**+**Dubbo**+**MongoDB**进行开发，使用 **Sa-Token**做登录验证和权限校验，使用 **ElasticSearch** 作为全文检索服务，使用 **Github Actions**完成VidBurst的持续集成，文件支持**七牛云** 存储。

## 运行配置

启动服务需要一些中间件

+ mongodb
+ elasticsearch
+ redis
+ 七牛云对象存储kodo

配好这些然后项目就可以启动啦

## 项目特点

- 友好的代码结构及注释，便于阅读及二次开发
- 实现前后端分离，通过 **Json** 进行数据交互，前端再也不用关注后端技术
- 页面交互使用 **Vue2.x**，极大的提高了开发效率。
- 引入**ElasticSearch**  作为全文检索服务，并支持可插拔配置
- 引入**Github Actions** 工作流，完成持续集成、持续部署。
- 引入七牛云对象存储
- 引入 **RBAC** 权限管理设计，灵活的权限控制，按钮级别的细粒度权限控制，满足绝大部分的权限需求

## 项目地址

目前项目托管在  **Github** 平台上中

- 前端：[HNIT-IoT-Lab/qiniu-video-front: 七牛云短视频应用前端程序 (github.com)](https://github.com/HNIT-IoT-Lab/qiniu-video-front)
- 后端：[HNIT-IoT-Lab/qiniu-video-server: 七牛云短视频应用后端程序 (github.com)](https://github.com/HNIT-IoT-Lab/qiniu-video-server)

## 项目目录

- apps

  + video-admin：提供后台API接口服务
  + video-server：提供web端API接口服务

- common：

  + es：提供搜索服务
  + kodo：提供七牛云对象存储服务

- libraries

  + hnit-assembly：
  + hnit-common：
  + hnit-entity：
  + hnit-Interceptor：
  + hnit-starter：
  + hnit-utils：常用工具类

## 技术选型

### 系统架构图

![image-20231106233155328](C:\Users\小曾\Desktop\博客图片\image-20231106233155328.png)

### 后端技术

|      技术      |          说明           |                             官网                             |
| :------------: | :---------------------: | :----------------------------------------------------------: |
|   SpringBoot   |         MVC框架         | [ https://spring.io/projects/spring-boot](https://spring.io/projects/spring-boot) |
|     Nginx      | HTTP和反向代理web服务器 |                      http://nginx.org/                       |
|     Lombok     |    简化对象封装工具     | [ https://github.com/rzwitserloot/lombok](https://github.com/rzwitserloot/lombok) |
|     SLF4J      |        日志框架         |                    http://www.slf4j.org/                     |
|     七牛云     |    七牛云 - 对象储存    |         https://developer.qiniu.com/sdk#official-sdk         |
|     Kibana     |    分析和可视化平台     |               https://www.elastic.co/cn/kibana               |
| Elasticsearch  |        搜索引擎         | [ https://github.com/elastic/elasticsearch](https://github.com/elastic/elasticsearch) |
|   腾讯云短信   |      短信发送平台       |        https://cloud.tencent.com/document/product/382        |
|     Hutool     |     Java工具包类库      |                  https://hutool.cn/docs/#/                   |
|     Redis      |       分布式缓存        |                      https://redis.io/                       |
|     Docker     |       容器化部署        |      [ https://www.docker.com](https://www.docker.com/)      |
| Docker Compose |     Docker容器编排      |               https://docs.docker.com/compose/               |
| Github Actions |       自动化部署        |              https://help.github.com/en/actions              |

### 前端技术

|    技术    |           说明            |                             官网                             |
| :--------: | :-----------------------: | :----------------------------------------------------------: |
|   Vue.js   |         前端框架          |                      https://vuejs.org/                      |
| Vue-router |         路由框架          |                  https://router.vuejs.org/                   |
|    Vuex    |     全局状态管理框架      |                   https://vuex.vuejs.org/                    |
|  Nuxt.js   | 创建服务端渲染 (SSR) 应用 |                    https://zh.nuxtjs.org/                    |
|  Element   |        前端ui框架         |    [ https://element.eleme.io](https://element.eleme.io/)    |
|   Axios    |       前端HTTP框架        | [ https://github.com/axios/axios](https://github.com/axios/axios) |

## 环境搭建

|     工具      | 版本号 |                             下载                             |
| :-----------: | :----: | :----------------------------------------------------------: |
|      JDK      |  1.8   | https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html |
|     Maven     | 3.3.0+ |                   http://maven.apache.org/                   |
| Elasticsearch | 6.3.0  |               https://www.elastic.co/downloads               |
|     Nginx     |  1.10  |              http://nginx.org/en/download.html               |
|     Redis     | 3.3.0  |                  https://redis.io/download                   |

## 模块规格、分工
