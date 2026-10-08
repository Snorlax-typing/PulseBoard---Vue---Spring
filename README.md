# PulseBoard 全栈项目 README
> 项目：PulseBoard | Vue3 + Spring Boot 4.1 生产级实时任务指挥中心
> 技术栈：Spring Boot + Vue3 + WebSocket + JPA+H2 + JWT + VueRouter + ECharts
> 项目定位：前后端一体化单包部署，`mvn spring-boot:run` 一键启动，适合学习 Vue + Spring 全栈开发，内置大量生产工程化特性

## ✨ 项目亮点（本次新增全部生产级功能）
原基础功能：任务CRUD、REST接口、WebSocket实时指标推送、ECharts实时看板
新增生产功能清单：
1. **JWT 身份认证 & 权限控制**
    - 用户登录、Token签发、请求头Authorization校验
    - Token过期自动失效，前端存储Token，路由守卫拦截未登录访问
    - 区分普通用户 / 管理员角色，管理员才可批量删除任务
2. **H2 内嵌数据库 + Spring Data JPA**
    - 不再使用内存临时集合，数据持久化；重启服务数据保留（可配置）
    - H2 数据库控制台，浏览器直接访问查看表与数据
    - JPA实体、Repository、分页查询
3. **全局异常处理器 + 统一返回体**
    - 所有接口返回固定JSON格式，统一code、msg、data字段
    - 参数校验、空指针、权限异常、资源不存在异常捕获，友好提示
4. **请求参数校验（Jakarta Validation）**
    - 任务名称非空、长度限制；表单非法参数后端拦截
5. **Profile 多环境配置（dev / prod）**
    - dev：开启H2控制台、日志打印SQL、热部署devtools
    - prod：关闭H2控制台、精简日志、关闭调试信息
6. **Vue Router 前端路由**
    - 多页面：登录页 / 实时看板页 / 任务管理页
    - 路由守卫：未登录自动跳转登录页
7. **跨域配置 CORS**
    - 后端全局CORS配置，开发阶段前端独立服务调试也可对接
8. **日志规范化**
    - 区分业务日志，WebSocket连接/断开、登录、增删任务均打印审计日志
9. **全局CORS、接口请求封装（Axios）**
    - Vue封装axios请求拦截器：自动携带JWT Token；响应拦截器统一处理错误码、Token过期自动登出
10. **前端功能增强**
    - 登录表单校验、登录状态保存
    - 页面Loading、全局错误弹窗提示
    - 任务分页、筛选、优先级标签、状态流转
    - WebSocket断线自动重连，网络恢复继续接收实时监控指标
11. **API接口文档（SpringDoc OpenAPI / Swagger）**
    - dev环境可访问接口文档，在线调试所有REST接口

---

## 📁 项目文件结构
```
demo/
├── pom.xml                                 # Maven核心配置，所有依赖、插件、构建配置
├── README.md                               # 项目说明文档（当前文件）
├── src
│   ├── main
│   │   ├── java/com/example/demo
│   │   │   ├── DemoApplication.java        # SpringBoot启动入口 @SpringBootApplication
│   │   │   ├── config/                     # 后端配置类
│   │   │   │   ├── JwtConfig.java           # JWT加密、解析配置
│   │   │   │   ├── SecurityConfig.java      # 安全拦截、JWT过滤器
│   │   │   │   ├── WebSocketConfig.java     # WebSocket注册配置
│   │   │   │   ├── CorsConfig.java          # 全局跨域CORS配置
│   │   │   │   └── OpenApiConfig.java       # Swagger接口文档配置
│   │   │   ├── controller/                 # 接口控制器（接收前端HTTP请求）
│   │   │   │   ├── AuthController.java     # 登录、获取Token接口
│   │   │   │   ├── TaskController.java      # 任务CRUD、分页查询接口
│   │   │   │   └── HelloController.java     # 简易测试接口
│   │   │   ├── entity/                      # JPA数据库实体，映射H2数据表
│   │   │   │   ├── Task.java                # 任务实体
│   │   │   │   └── User.java                # 用户实体
│   │   │   ├── repository/                  # JPA数据库访问层
│   │   │   │   ├── TaskRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── service/                     # 业务逻辑层
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── TaskService.java
│   │   │   │   └── MetricsService.java      # 系统指标生成服务
│   │   │   ├── websocket/                   # WebSocket实时推送处理器
│   │   │   │   └── MetricsWebSocketHandler.java
│   │   │   ├── exception/                   # 全局异常处理
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── BusinessException.java
│   │   │   ├── filter/
│   │   │   │   └── JwtAuthenticationFilter.java # JWT请求拦截过滤器
│   │   │   └── util/                        # 工具类
│   │   │       └── JwtUtil.java             # Token生成、校验工具
│   │   ├── resources
│   │   │   ├── application.yml              # 主配置，多环境profile(dev/prod)
│   │   │   ├── data.sql                     # H2初始化脚本，预置测试账号、任务数据
│   │   │   ├── static                       # Vue前端打包产物，Spring直接托管静态资源
│   │   │   │   ├── index.html               # Vue入口页面
│   │   │   │   ├── js/
│   │   │   │   ├── css/
│   │   │   │   └── assets/                  # 静态图片
│   │   │   └── templates
│   │   └── test                             # 单元测试（预留）
│   └── test
├── .mvn/                                    # Maven Wrapper，无本地mvn也可构建
├── mvnw / mvnw.cmd
└── target/                                  # 编译打包输出目录，fat jar生成位置
```

## 🧩 分层角色与调用时机（后端）
1. **Controller 控制器**
    角色：接收前端HTTP请求，参数接收，调用Service，返回统一JSON
    调用时机：浏览器/Vue axios发起接口请求时，Spring MVC路由匹配后进入
2. **Service 业务层**
    角色：业务逻辑处理、数据组装、权限判断、事务控制
    调用时机：Controller调用；不直接操作数据库，调用Repository
3. **Repository 数据访问层(JPA)**
    角色：封装SQL，读写H2数据库
    调用时机：Service需要持久化/查询数据时
4. **Entity 实体**
    角色：映射数据库表结构，字段校验注解
    调用时机：JPA读写数据库自动映射对象 ↔ 数据库记录
5. **Config 配置类**
    角色：注册Bean、开启WebSocket、安全策略、跨域、Swagger
    调用时机：**项目启动阶段**，Spring容器初始化自动加载
6. **Filter过滤器(JWT)**
    角色：拦截每一次HTTP请求，解析Token，校验登录状态
    调用时机：请求到达Controller**之前**执行
7. **GlobalExceptionHandler 全局异常**
    角色：捕获全项目抛出的异常，包装成统一返回体
    调用时机：任意层抛出异常时触发

## 🖥️ Vue前端架构说明（static内打包产物）
> 源码为Vue3 + Composition API + VueRouter + Axios + ECharts，打包后放到Spring static目录
1. **路由 Vue Router**
    - 页面：`/login`、`/dashboard`实时看板、`/task`任务管理
    - 路由守卫：跳转页面前检查本地Token，未登录强制跳转登录页
2. **Axios 请求封装**
    - 请求拦截：自动在Header带上 `Authorization: Bearer {token}`
    - 响应拦截：捕获后端错误码，弹出提示；Token 401过期自动清空本地登录态并跳转登录
3. **WebSocket 模块**
    - 页面挂载时建立连接，接收后端推送的CPU/内存/QPS实时指标
    - 断线自动重试机制
4. **ECharts**
    - 实时折线图：服务器指标
    - 环形饼图：任务状态统计
5. **状态管理（简易）**
    - localStorage存储JWT Token与用户名，刷新页面保持登录

## ⚙️ 环境配置 Profile（dev / prod）
`application.yml` 使用 `---` 分割多环境
- **dev（开发环境，日常学习使用）**
    - 端口：8080
    - 开启H2数据库控制台：`[http://localhost:8080/h2-console](http://localhost:8080/h2-console)`
    - 打印SQL日志、开启SpringBoot DevTools热部署
    - Swagger接口文档启用：`[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)`
    - CORS放开本地前端调试
    - 日志级别DEBUG
- **prod（生产环境）**
    - 端口：8081
    - 关闭H2控制台、关闭Swagger、关闭热部署
    - 日志精简，INFO级别
    - 严格安全策略

### 切换环境启动命令
```powershell
# dev开发环境
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"

# prod生产环境
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=prod"
```

## 🚀 快速启动步骤
> 前置：JDK17 + Maven全局已配置
1. 进入项目根目录
2. 执行启动命令（推荐dev）
```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```
3. 访问页面：`[http://localhost:8080](http://localhost:8080)`
    - 默认测试账号：`admin / 123456`（管理员，可批量删除任务）
    - 普通账号：`user / 123456`
4. dev环境附加地址
    - H2数据库控制台：[http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - Swagger接口文档：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📡 前后端交互说明
1. **REST HTTP接口**
    用途：任务新增、编辑、删除、分页查询、登录（CRUD业务）
    通信：Axios发起POST/GET请求，携带JWT Token，后端校验权限
2. **WebSocket长连接 ws://localhost:8080/ws/metrics**
    用途：**服务器主动推送实时监控数据**，无需前端轮询
    场景：看板实时指标，后端定时推送CPU、内存、QPS，前端图表自动刷新

## 📦 打包部署（生产）
打包生成可执行Fat Jar，内置Tomcat，不需要单独装Tomcat
```powershell
mvn clean package -Dspring.profiles.active=prod
# 运行jar包
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 📌 学习重点（Vue + Spring 结合核心）
1. Spring 后端提供JSON接口 + 身份鉴权(JWT)、数据库持久化、全局异常
2. Vue前端负责页面渲染、用户交互、图表展示、路由控制
3. 两种通信模型结合：
    - HTTP REST：一问一答，适合增删改查业务
    - WebSocket：服务端主动推送，适合实时监控看板
4. 工程化思想：多环境Profile、统一返回、参数校验、日志、接口文档、权限隔离，贴近真实企业项目规范

## ❗ 常见问题排查
1. 访问页面404：确认项目已启动；确认Vue打包产物在static目录
2. 登录提示401：Token过期，清空浏览器localStorage，重新登录
3. WebSocket不更新：检查防火墙；确认后端WebSocket端点正常
4. H2控制台无法访问：确认是dev环境，prod默认关闭H2控制台
5. mvn命令找不到：使用VS Code完全重启，刷新环境变量（之前排障的问题）

## 📜 License
本项目为学习演示项目，用于 Vue + Spring Boot 全栈学习。