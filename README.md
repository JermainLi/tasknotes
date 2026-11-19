# TaskPanel APK（狗蛋任务面板 · 安卓壳）

一个极简的 WebView 外壳，把 https://blog.jermainli.world/panel/ 包成安卓 App。

## 为什么是壳而不是完整 App
- 面板本身是 PWA，功能已够用（看任务 / 加任务 / 勾完成）
- 完整重写需要 mdbase 连接、同步协议 —— 不值得
- 壳只有几百 KB，编译 3~5 分钟

## 云端编译（推荐）
push 到 GitHub → Actions 自动编译 → 在 Actions 页面下载 `taskpanel-debug-apk`

**需要在仓库 Settings → Secrets and variables → Actions 添加：**
| Secret | 说明 | 示例 |
| --- | --- | --- |
| `PANEL_USER` | 面板用户名 | jermain |
| `PANEL_PASS` | 面板密码 | 你的密码 |
| `PANEL_URL` | 可选，默认 https://blog.jermainli.world/panel/ | |

## 本地编译
需要 JDK 17 + Android SDK 34：
```bash
./gradlew assembleDebug   # 产物在 app/build/outputs/apk/debug/
```

## 结构
```
app/src/main/java/world/jermainli/taskpanel/MainActivity.kt   WebView + 自动 Basic Auth + 下拉刷新
app/src/main/res/layout/activity_main.xml                     SwipeRefreshLayout + WebView
.github/workflows/build.yml                                   GitHub Actions
```
