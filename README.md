# VidBurst

## 项目介绍

VidBurst（结合了"vid"和"burst"，意为视频和瞬间的结合），一个短视频应用。**Web** 端使用 **Vue** + **Arco-Design**+**pinia**，后端使用  **SpringBoot** + **Mybatis-plus**+**Dubbo**+**MongoDB**进行开发，使用 **Sa-Token**做登录验证和权限校验，使用 **ElasticSearch** 作为全文检索服务，使用 **Github Actions**完成VidBurst的持续集成，使用**协同过滤算法结合LRU最近最少使用算法**实现视频推荐，文件支持**七牛云** 存储。

## 运行配置

启动服务需要一些中间件

+ mongodb
+ elasticsearch
+ redis
+ 七牛云对象存储kodo

配置好这些后端服务就可以启动啦

## 项目特点

- 友好的代码结构及注释，便于阅读及二次开发
- 实现前后端分离，通过 **Json** 进行数据交互，前端再也不用关注后端技术
- 页面交互使用 **Vue3.x**，极大的提高了开发效率。
- 引入**ElasticSearch**  作为全文检索服务，并支持可插拔配置
- 引入**Redis**作为缓存，完成点赞数据存储
- 引入**Github Actions** 工作流，完成持续集成、持续部署。
- 引入**七牛云对象存储**
- 引入**腾讯云短信**实现手机验证码登录
- 引入 **RBAC** 权限管理设计，灵活的权限控制，按钮级别的细粒度权限控制，满足绝大部分的权限需求
- 手写**协同过滤算法+LRU最近最少使用算法**推荐热门视频

## 项目地址

目前项目托管在  **Github** 平台上中

- 前端：[HNIT-IoT-Lab/qiniu-video-front: 七牛云短视频应用前端程序 (github.com)](https://github.com/HNIT-IoT-Lab/qiniu-video-front)
- 后端：[HNIT-IoT-Lab/qiniu-video-server: 七牛云短视频应用后端程序 (github.com)](https://github.com/HNIT-IoT-Lab/qiniu-video-server)

## 快速体验

[title (hnit-iot-lab.github.io)](https://hnit-iot-lab.github.io/qiniu-video-front/home)

## 项目目录

- apps

  + video-admin：提供后台API接口服务（后台管理）
  + video-server：提供web端API接口服务

- common：

  + es：提供搜索服务
  + kodo：提供七牛云对象存储服务

- libraries

  + hnit-assembly：提供mongo服务
  + hnit-common：公共模块
  + hnit-entity：提供实体类
  + hnit-Interceptor：提供拦截器功能
  + hnit-starter：自定义的场景启动器
  + hnit-utils：常用工具类

## 技术选型

### 系统架构图

![在这里插入图片描述](https://img-blog.csdnimg.cn/7f199bb559cb4e6392c18d76ff5f22d3.png#pic_center)


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

## 模块规格

### 需求分析

![在这里插入图片描述](https://img-blog.csdnimg.cn/05c970c8240b4c059c601ab8c93bd3d7.png#pic_center)


### 后端模块

![在这里插入图片描述](https://img-blog.csdnimg.cn/a128e75cb9fd44aa90186d6c15fd270f.png#pic_center)


## 分工

团队三个人：

+ 曾吉平  主要负责后端框架的搭建、视频以及用户接口的编写、视频推荐算法的编写
+ 严奕杰   主要负责前端视频播放模块的编写、视频上下键滚动切换以及前端通用模块如router等的封装
+ 陈瑜   主要负责前端用户模块的编写、登录，视频点赞收藏，主页瀑布式布局、api接口调用和Ui架构等

## 视频推荐算法

使用协同过滤算法实现：

1. 获取用户的行为数据（用户的点赞、收藏）
2. 使用皮尔逊相关系数，构建用户相似度矩阵
3. 找到相似度最高的用户
4. 基于相似用户的喜好推荐视频

基于LRU（Least Recently Used，最近最少使用）算法实现：

1. 数据结构：LRU算法通常使用一个双向链表（Linked List）和一个哈希表（Hash Table）来实现。
   - 双向链表：用于记录数据的访问顺序，越靠近链表头表示越近期被访问过，越靠近链表尾表示越久远被访问过。
   - 哈希表：用于快速查找数据在链表中的位置，以提高访问数据的效率。
2. 访问数据：
   - 当访问一个数据时，首先在哈希表中查找该数据是否存在。
   - 如果存在，将该数据从链表中移除，并将其移到链表头表示最近访问过。
   - 如果不存在，根据实际需求生成或获取该数据，并将其添加到链表头。
   - 如果链表已满，将链表尾部的数据删除，以给新的数据腾出空间。
3. 淘汰数据：
   - 当需要淘汰数据时，直接将链表尾部的数据删除即可，因为它是最久未被访问的数据。

基于上述理论

根据协同过滤算法拿到相似度最高的用户喜好（可以是点赞/收藏、分享）的视频数据，结合最近最少使用算法，淘汰掉最久未被访问的数据，然后将视频数据返回给前端

### 实现：

思路整理：

1. 数据模型设计：

   - 创建一个`User`类，用于表示用户信息。其中可以包含用户ID、名称等字段。
   - 创建一个`Article`类，用于表示文章信息。其中可以包含文章ID、标题、内容等字段。
   - 创建一个`UserArticleInteraction`类，用于表示用户与文章的交互信息。其中可以包含用户ID、文章ID、交互类型（如点赞、收藏等）和交互时间等字段。
2. 协同过滤算法：

   - 使用MongoDB的聚合框架，对用户与文章的交互数据进行分组和计算。可以计算用户之间的相似度。
   - 根据相似度计算出用户之间的相似度矩阵，用于推荐相似用户喜欢的文章。
3. LRU算法：

   - 在内存中维护一个固定大小的LRU缓存，用于存储最近访问过的文章。
   - 当用户访问文章时，首先在LRU缓存中查找。如果文章存在于缓存中，则更新文章在缓存中的访问时间。
   - 如果文章不在缓存中，则从数据库中获取文章，并将其添加到缓存中。如果缓存已满，则使用LRU算法删除最近最少使用的文章。
   - 将数据返回给前端


## 视频滚动模块

### 实现：

1. 页面布局设计：

   使用3个嵌套的video控件作为基础循环使用，使得页面控件数量可控，降低渲染以及前端压力。

2. 滑动窗口算法设计：

   使用一个变量**n**维护当前用户观看视频，当用户向下滚动后将**n-2**下标的控件更新为下一个视频，当用户上滑时将**n+2**下标的控件更新为上一个控件，以此来实现无线滚动与视频无感刷新。
