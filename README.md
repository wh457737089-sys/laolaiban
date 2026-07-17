# 银龄数字生活助手 — Android 原生APK 编译指南

## 方式一（推荐）：GitHub Actions 自动编译

1. 把这个文件夹上传到 GitHub 仓库（`android/` 目录放在根目录）
2. 推送到 `main` 分支
3. 在 GitHub 仓库页面点 **Actions** tab
4. 左侧点 **Build APK**，再点 **Run workflow**
5. 几分钟后，APK 生成完毕，点进去下载 `shop-guide-app.zip`
6. 解压得到 `.apk` 文件，微信发给老人手机上安装

## 方式二：本地编译（需要 Android Studio）

### 环境要求
- Android Studio Hedgehog (2023.1.1) 或更新版本
- JDK 17+

### 步骤

1. 用 Android Studio 打开 `android/` 文件夹（Open an existing project）
2. 等 Gradle Sync 完成（首次会自动下载依赖，需要联网）
3. 连接 Android 手机（开启开发者模式 + USB 调试）
4. 点 Run 按钮（绿色三角）直接安装到手机
5. 或者 Build → Build Bundle(s) / APK → Build APK
6. APK 生成在：`app/build/outputs/apk/debug/app-debug.apk`

### 手机上安装

1. 把 APK 文件通过微信/QQ 发给老人的手机
2. 在手机上点文件 → 选择"用其他应用打开" → 选"包安装器"
3. 点"继续安装"
4. 装好后桌面上会出现 "银龄生活助手" 图标
5. **第一次使用：** 子女帮点开 → 点"开始引导" → 在系统设置中打开"悬浮窗权限"
6. 以后老人打开淘宝时，屏幕上会有一个小气泡，点一下就有语音引导
