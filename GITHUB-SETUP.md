# GitHub Actions 自动构建 APK 指南

## 第一步：创建 GitHub 仓库

1. 登录你的 GitHub 账号
2. 点击右上角 **+** → **New repository**
3. 填写仓库信息：
   - Repository name: `zhongruan-tuner`
   - Description: `中阮调音器 - 专为中老年人设计的极简调音应用`
   - 选择 **Public** 或 **Private**（推荐 Public）
   - **不要** 勾选 "Initialize this repository with a README"
4. 点击 **Create repository**

## 第二步：上传代码到 GitHub

### 方法 A: 使用 Git 命令行（推荐）

打开命令行工具（Git Bash 或 PowerShell），执行：

```bash
# 进入项目目录
cd C:\Users\wayne\lobsterai\project\zhongruan-tuner

# 初始化 Git 仓库
git init

# 添加所有文件到 Git
git add .

# 提交
git commit -m "Initial commit: 中阮调音器 v1.0"

# 添加远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/zhongruan-tuner.git

# 推送到 GitHub
git branch -M main
git push -u origin main
```

### 方法 B: 使用 GitHub Desktop

1. 下载并安装 [GitHub Desktop](https://desktop.github.com/)
2. 打开 GitHub Desktop
3. 选择 `File` → `Add local repository`
4. 选择 `zhongruan-tuner` 文件夹
5. 点击 `Commit to main`
6. 点击 `Publish repository`

### 方法 C: 直接上传（最简单，但后续更新不便）

1. 在项目根目录，将所有文件打包成 ZIP
2. 在 GitHub 仓库页面，点击 `uploading an existing file`
3. 拖拽 ZIP 文件上传
4. 等待上传完成后，点击 `Commit changes`

**注意**：此方法无法享受 Git 版本控制的好处，推荐使用方法 A 或 B。

## 第三步：等待 GitHub Actions 自动构建

1. 推送代码后，GitHub Actions 会自动开始构建
2. 前往 `https://github.com/YOUR_USERNAME/zhongruan-tuner/actions`
3. 你会看到一个正在运行的工作流（绿色或黄色进度条）
4. 等待 5-10 分钟，直到状态变为绿色 ✓

## 第四步：下载 APK 文件

### 从 Artifacts 下载（推荐）

1. 在 GitHub Actions 页面，点击最新的构建记录
2. 滚动到页面底部
3. 在 **Artifacts** 部分，点击 `app-debug` 下载 Debug 版本
4. 如果需要 Release 版本，点击 `app-release-unsigned`

**注意**：Artifacts 只保留 90 天，请尽快下载。

### 从 Releases 下载（需要创建 Tag）

如果你想创建永久下载链接：

```bash
# 本地创建标签
git tag v1.0.0
git push origin v1.0.0
```

推送 Tag 后，GitHub Actions 会自动创建 Release，APK 文件会作为附件上传。

## 第五步：安装到手机

1. 下载 APK 文件后，解压 ZIP 文件
2. 将 APK 文件传输到手机（微信文件传输助手、QQ、数据线等）
3. 在手机上打开 APK 文件
4. 如果提示"未知来源"，需要授权允许安装
5. 点击安装

## 常见问题

### Q: 构建失败了怎么办？

A: 点击失败的构建记录，查看错误日志。常见原因：
- 网络连接问题
- Gradle 依赖下载失败
- 代码编译错误

### Q: 如何只构建 Release 版本？

A: 修改 `.github/workflows/build-apk.yml`，删除 Debug 构建步骤。

### Q: 如何自定义 APK 文件名？

A: 在 `app/build.gradle.kts` 中添加：

```kotlin
android {
    applicationVariants.all {
        outputs.all {
            outputFileName = "zhongruan-tuner-${versionName}.apk"
        }
    }
}
```

### Q: 构建太慢了怎么办？

A: GitHub Actions 使用免费 Ubuntu 服务器，通常 5-10 分钟完成。如果太慢，可以：
1. 检查网络连接
2. 使用国内镜像（需要修改 Gradle 配置）

## 后续更新代码

修改代码后，推送新提交：

```bash
git add .
git commit -m "修复 xxx 问题"
git push
```

GitHub Actions 会自动重新构建，你可以在 Actions 页面查看进度和下载新的 APK。

## 安全提示

- **不要** 将 keystore 文件上传到 GitHub
- **不要** 将签名密钥上传到 GitHub
- 如果仓库是 Public，所有代码都是公开的
- Debug APK 未签名，仅用于测试

---

**需要帮助？** 查看 [GitHub Actions 文档](https://docs.github.com/en/actions)
