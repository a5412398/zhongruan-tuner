# 中阮调音器 (Zhongruan Tuner)

一款专为中老年人设计的极简中阮调音应用。

## 特点

- 🎯 **极简设计**：打开即用，无需登录
- 👵 **适老化**：大字体、高对比度、直白文字
- 🆓 **完全免费**：无广告、无内购
- 🎵 **精准检测**：YIN 算法，实时音高检测

## 技术栈

- **平台**: Android 8.0+ (API 26)
- **语言**: Kotlin
- **UI**: Jetpack Compose
- **音频处理**: TarsosDSP (YIN 算法)

## 构建说明

### 方法一：使用 GitHub Actions 自动构建（推荐，无需安装 Android Studio）

1. **上传到 GitHub**
   ```bash
   # 进入项目目录
   cd zhongruan-tuner

   # 初始化 Git
   git init

   # 添加所有文件
   git add .

   # 提交
   git commit -m "Initial commit: 中阮调音器"

   # 添加你的 GitHub 仓库（替换为你的仓库地址）
   git remote add origin https://github.com/YOUR_USERNAME/zhongruan-tuner.git

   # 推送到 GitHub
   git push -u origin main
   ```

2. **GitHub Actions 自动构建**
   - 推送到 GitHub 后，GitHub Actions 会自动开始构建
   - 前往 `https://github.com/YOUR_USERNAME/zhongruan-tuner/actions` 查看构建进度
   - 构建完成后，在页面底部下载 APK 文件

3. **手动触发构建**
   - 前往 `https://github.com/YOUR_USERNAME/zhongruan-tuner/actions/workflows/build-apk.yml`
   - 点击 "Run workflow" 按钮
   - 选择分支（main）
   - 点击 "Run workflow"

4. **下载 APK**
   - 构建完成后，点击最新的构建记录
   - 在 "Artifacts" 部分下载 `app-debug.zip` 或 `app-release-unsigned.zip`
   - 解压后得到 APK 文件

### 方法二：本地构建（需要 Android Studio）

### 前置要求

1. **Android Studio**: 下载并安装 [Android Studio](https://developer.android.com/studio)
2. **JDK 17**: Android Studio 内置

### 构建步骤

1. **打开项目**
   - 启动 Android Studio
   - 选择 `File` → `Open`
   - 选择 `zhongruan-tuner` 文件夹

2. **同步 Gradle**
   - 打开后会自动同步 Gradle
   - 等待同步完成

3. **构建 APK**

   **Debug 版本** (用于测试):
   ```bash
   # 在 Android Studio 中点击 Build → Build Bundle(s) / APK(s) → Build APK(s)
   # 或使用命令行:
   ./gradlew assembleDebug
   ```

   **Release 版本** (用于发布):
   ```bash
   ./gradlew assembleRelease
   ```

4. **APK 位置**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

### 安装到手机

1. 启用手机的"未知来源"安装权限
2. 将 APK 文件传输到手机
3. 点击 APK 文件进行安装

## 项目结构

```
zhongruan-tuner/
├── app/
│   ├── src/main/
│   │   ├── java/com/lobsterai/zhongruan_tuner/
│   │   │   ├── audio/           # 音频处理模块
│   │   │   │   ├── AudioRecorder.kt
│   │   │   │   ├── PitchDetector.kt
│   │   │   │   └── ReferenceTonePlayer.kt
│   │   │   ├── model/           # 数据模型
│   │   │   │   ├── RuanString.kt
│   │   │   │   └── TunerState.kt
│   │   │   ├── ui/              # UI 界面
│   │   │   │   ├── components/  # UI 组件
│   │   │   │   ├── theme/       # 主题和样式
│   │   │   │   ├── TunerScreen.kt
│   │   │   │   └── TunerViewModel.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/                 # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── docs/                        # 文档
│   ├── prd/                     # 产品需求文档
│   ├── design/                  # 设计文档
│   └── plans/                   # 实现计划
└── README.md
```

## 中阮标准定弦

| 弦 | 音名 | 频率 (Hz) |
|----|------|----------|
| 第 1 弦 | C3 | 130.81 |
| 第 2 弦 | G2 | 98.00 |
| 第 3 弦 | D3 | 146.83 |
| 第 4 弦 | G3 | 196.00 |

## 界面说明

### 调音状态

| 状态 | 颜色 | 文字 | 操作 |
|------|------|------|------|
| 太低 | 🔴 红色 | "太低 → 调紧" | 顺时针旋转弦轴 |
| 准 | 🟢 绿色 | "准！" | 完成 |
| 太高 | 🟠 橙色 | "太高 ← 调松" | 逆时针旋转弦轴 |

## 使用说明 (给母亲)

1. **打开应用** - 点击图标
2. **允许麦克风权限** - 点击"允许"
3. **选择要调的弦** - 点击对应的按钮 (默认第 1 弦)
4. **弹响琴弦** - 用手指拨动琴弦
5. **看屏幕调整**:
   - 显示**红色**"太低→调紧"：把弦**调紧**
   - 显示**绿色**"准！"：已经准了
   - 显示**橙色**"太高←调松"：把弦**调松**
6. **调下一根弦** - 点击其他弦按钮，重复步骤 4-5

## 开发信息

- **创建日期**: 2026-03-01
- **版本**: 1.0.0
- **开发者**: LobsterAI

## 许可证

MIT License
