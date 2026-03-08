# 📝 Android Intents Day 4 — Quick Note App: Sharing, Foreground Services & Alarms

A practical Android mini-app that ties together **text/image sharing**, a **Foreground Service**, and **AlarmManager-based reminders** into a single cohesive "Quick Note" experience — built entirely with **Jetpack Compose**. This is Day 4 of the Intents series, where all previous concepts come together into a real, working feature.

---

## 📖 Description

The **Quick Note** app lets users type a note, share it as text or attach an image and share that too, schedule a reminder alarm to check back on the note, and trigger a background upload simulation via a Foreground Service — all from a single screen. It demonstrates how Android's sharing system, background execution model, and alarm scheduling work in harmony inside a modern Compose app.

---

## 🗂️ Project Structure

```
app/
└── src/main/java/com/example/intentsday4/
    ├── MainActivity.kt           # Entry point; hosts QuickNoteScreen composable
    ├── MyForegroundService.kt    # Foreground Service simulating a background note upload
    └── TaskReceiver.kt           # BroadcastReceiver + AlarmManager reminder scheduler
```

---

## 🧠 Key Concepts Covered

| Concept | Implementation |
|---|---|
| Share text via chooser | `Intent.ACTION_SEND` with `type = "text/plain"` |
| Share image via chooser | `Intent.ACTION_SEND` with `type = "image/*"` + `FLAG_GRANT_READ_URI_PERMISSION` |
| Image picker | `ActivityResultContracts.GetContent` with `"image/*"` |
| Foreground Service | `Service` subclass with `startForeground()` + persistent notification |
| Notification Channel | `NotificationChannel` (required on Android O+) |
| Auto-stopping service | `Handler.postDelayed` + `stopForeground(STOP_FOREGROUND_REMOVE)` + `stopSelf()` |
| AlarmManager reminder | `AlarmManager.set()` with `RTC_WAKEUP` firing after 60 seconds |
| BroadcastReceiver alarm | `TaskReceiver` receives alarm and shows a `Toast` |
| PendingIntent flags | `FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT` (Android 12+ compliant) |
| Safe service start | `ContextCompat.startForegroundService()` |
| Conditional button state | `enabled = selectedImageUri != null` to guard image share |

---

## 🖥️ Screen Breakdown — `QuickNoteScreen`

A single-screen app with six interactive elements:

| UI Element | Action |
|---|---|
| `TextField` | User types their note |
| **Share Note** button | Shares note text via Android's share sheet |
| **Select Image from Gallery** button | Opens image picker |
| **Share Selected Image** button | Shares selected image (disabled until image is chosen) |
| **Remind Me in 1 Minute** button | Schedules an `AlarmManager` reminder via `TaskReceiver` |
| **Start Upload Service** button | Launches `MyForegroundService` for a 5-second simulated upload |

---

## ⚙️ Component Deep Dives

### 📤 Sharing (`MainActivity.kt`)

Two utility functions handle sharing — both use `Intent.createChooser()` to let the user pick the target app:

```kotlin
// Text sharing
fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share note via"))
}

// Image sharing — FLAG_GRANT_READ_URI_PERMISSION is required for URI access
private fun shareImage(context: Context, imageUri: Uri) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share image via"))
}
```

### 🔔 Foreground Service (`MyForegroundService.kt`)

- Creates a `NotificationChannel` (`note_channel`) on Android O+
- Calls `startForeground()` immediately with a persistent notification
- Uses `Handler(Looper.getMainLooper()).postDelayed(...)` to simulate 5 seconds of work
- Stops cleanly with `stopForeground(STOP_FOREGROUND_REMOVE)` + `stopSelf()`
- Returns `START_NOT_STICKY` — the service will NOT restart if killed by the system

> ⚠️ Uses `@RequiresApi(Build.VERSION_CODES.O)` since `NotificationChannel` was introduced in API 26.

### ⏰ Alarm Reminder (`TaskReceiver.kt`)

- `TaskReceiver` is a `BroadcastReceiver` that shows a `Toast` when fired
- `scheduleReminder()` uses `AlarmManager.set()` (not `setExact()`) to avoid battery optimization restrictions on newer Android versions
- `PendingIntent` is created with `FLAG_IMMUTABLE` (mandatory on Android 12+) and `FLAG_UPDATE_CURRENT` to replace any existing pending intent

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Minimum SDK: 26 (Android 8.0) — required for `NotificationChannel`
- Kotlin 1.9+
- Jetpack Compose BOM

### Required Manifest Additions

```xml
<!-- Foreground Service declaration -->
<service
    android:name=".MyForegroundService"
    android:foregroundServiceType="dataSync"
    android:exported="false" />

<!-- BroadcastReceiver for alarm -->
<receiver android:name=".TaskReceiver" android:exported="false" />

<!-- Permissions -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

### Setup

```bash
git clone https://github.com/your-username/android-intents-day4-quicknote.git
```

Open in Android Studio, sync Gradle, and run on a device or emulator (API 26+).

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Background:** Android `Service` (Foreground)
- **Scheduling:** `AlarmManager` + `BroadcastReceiver`
- **Sharing:** `Intent.ACTION_SEND` + `createChooser`
- **Architecture:** Single Activity, single Composable screen

---

## 📚 What I Learned

- How to share **text and images** using Android's built-in share sheet with `Intent.createChooser()`
- Why `FLAG_GRANT_READ_URI_PERMISSION` is required when sharing image URIs with other apps
- How **Foreground Services** work — why they need a visible notification and how to stop them cleanly
- The difference between `START_STICKY` and `START_NOT_STICKY` return values in `onStartCommand()`
- How `AlarmManager.set()` differs from `setExact()` in terms of battery optimization trade-offs
- Why `PendingIntent.FLAG_IMMUTABLE` is mandatory on Android 12+ (API 31+)

---

## 🔗 Related Projects

- [Day 1 — Basic Explicit & Implicit Intents](https://github.com/your-username/android-intents-day1)
- [Day 2 — Camera, Gallery, Document Picker & Permissions](https://github.com/your-username/android-intents-day2)
- [Day 3 — Settings, Deep Links & Broadcast Receivers](https://github.com/your-username/android-intents-day3)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).