# 🛒 Smart E-Commerce 智能电商推荐系统

基于 Spring Boot 3 + Vue 3 的智能电商推荐系统，支持 AI 语义搜索与个性化推荐。

## 技术栈

### 后端
- **框架**: Spring Boot 3 + MyBatis-Plus
- **数据库**: MySQL 8
- **认证**: JWT
- **部署**: 8080 端口

### 前端
- **框架**: Vue 3 + Vite
- **UI**: Element Plus
- **部署**: Nginx 反向代理（91 端口）

## 功能模块

| 模块 | 说明 |
|------|------|
| 🔐 用户系统 | 注册/登录/JWT认证 |
| 🛍️ 商品浏览 | 分类筛选/搜索/详情 |
| 🛒 购物车 | 添加/删除/结算 |
| 📦 订单管理 | 下单/查询/状态跟踪 |
| ❤️ 收藏 | 收藏商品管理 |
| 🧠 AI推荐 | 协同过滤 + 语义推荐 + 预算过滤 |

## AI 推荐特性

- **协同过滤**: 基于用户行为找到相似用户，推荐他们喜欢的商品
- **语义推荐**: 支持自然语言查询，如"送女友的生日礼物，预算500以内"
- **预算过滤**: 自动解析用户预算，过滤超预算商品
- **场景扩展**: 识别送礼场景，自动扩展礼物品类关键词

## 快速启动

### 后端
```bash
cd backend
# 创建数据库
mysql -u root -p < sql/init.sql
# 启动
mvn spring-boot:run
```

### 前端
```bash
cd frontend
npm install
npm run dev
```

### 部署
```bash
# 前端构建
cd frontend && npm run build

# 启动后端
cd backend && mvn clean package -DskipTests
java -jar target/smart-ecommerce-1.0.0.jar
```

## 项目结构

```
smart-ecommerce/
├── backend/
│   ├── src/main/java/com/smartecommerce/
│   │   ├── controller/    # REST 控制器
│   │   ├── service/       # 业务逻辑
│   │   ├── mapper/        # MyBatis Mapper
│   │   ├── entity/        # 实体类
│   │   ├── dto/           # 数据传输对象
│   │   ├── config/        # 配置类
│   │   └── handler/       # 全局异常处理
│   ├── src/main/resources/
│   │   └── application.yml
│   └── sql/
│       └── init.sql
├── frontend/
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 公共组件
│   │   ├── api/           # API 请求封装
│   │   └── router/        # 路由配置
│   ├── vite.config.js
│   └── package.json
└── README.md
```

---

Built with ❤️ by nanxu001
