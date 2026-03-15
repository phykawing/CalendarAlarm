# CalendarAlarm - Developer Guide

## Build & Test

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (signed with debug keystore)
./gradlew assembleRelease

# Run lint checks
./gradlew lint
```

## Project Overview

**Package:** `com.phykawing.calendaralarm`
**Min SDK:** 26 (Android 8.0) | **Target SDK:** 35

Android app that reads device calendar events (including recurring events) and sets real alarms (sound, vibration, or both with gradual volume increase) before events start. Alarms persist until dismissed via slide-to-dismiss, support adjustable snooze, and survive reboots. Background sync via WorkManager keeps events up to date every 15 minutes. Google holiday calendars are automatically excluded.

## Architecture

MVVM + Repository pattern with Hilt DI.

- `data/calendar/` — ContentResolver-based calendar reader using `CalendarContract.Instances` for recurring event support
- `data/local/` — Room DB v2 (entities with CASCADE foreign key, DAOs), `fallbackToDestructiveMigration`
- `data/preferences/` — DataStore-based user preferences (global alarm, defaults, sync)
- `data/repository/` — Repository interface + impl with sync logic (saves alarm settings before deleteAll to survive CASCADE)
- `domain/` — Models (`CalendarEvent`, `AlarmSetting`, `AlarmType` with SOUND/VIBRATION/SOUND_VIBRATION, `TimeUnit`) and use cases
- `alarm/` — `AlarmScheduler` (AlarmManager.setAlarmClock), `AlarmReceiver` (launches activity + starts service), `AlarmService` (foreground service with AudioFader), `BootReceiver`
- `sync/` — `CalendarSyncWorker` (HiltWorker), `SyncScheduler` (WorkManager 15-min periodic)
- `ui/events/` — Homepage with gradient background, event cards with alarm time display
- `ui/eventdetail/` — Per-event alarm configuration
- `ui/settings/` — Global settings with "Apply to All Events" bulk action
- `ui/alarm/` — Full-screen alarm with slide-to-dismiss and adjustable snooze (+5/-5 min, capped at event start)
- `ui/components/` — Reusable components (EventCard, AlarmSettingRow, AlarmTypePicker, GradientButton)
- `ui/theme/` — Material 3 theme with custom color scheme (dynamic colors disabled)
- `di/` — Hilt modules (AppModule, AlarmModule)
- `navigation/` — Compose NavHost routes

## Key Decisions

- **CalendarContract.Instances** (not Events) — required for recurring event expansion
- **Instance `_ID`** as primary key — each recurring occurrence gets a unique ID
- **SCHEDULE_EXACT_ALARM + USE_EXACT_ALARM** — both permissions for API 31-32 and 33+ compatibility
- **SYSTEM_ALERT_WINDOW** — enables reliable background activity launch for alarm UI on all devices
- **USE_FULL_SCREEN_INTENT** — runtime request on Android 14+ for full-screen alarm notification
- **Room with KSP** (not kapt) — modern annotation processing
- **Foreground service type `specialUse`** — for alarm playback on Android 14+
- **AlarmManager.setAlarmClock()** — ensures alarm fires even in Doze mode
- **AudioFader** — ramps MediaPlayer volume 0→1 over 30 seconds in 500ms steps
- **AlarmActivity with showOnLockScreen/turnScreenOn** — full-screen alarm UI over lock screen
- **Activity launched from BroadcastReceiver** — receivers triggered by AlarmManager have background activity start privileges that Services lack on Android 10+
- **Alarm settings saved before deleteAll** — CASCADE foreign key on AlarmSettingEntity would otherwise destroy settings during sync
- **Debounced Flow observation** — 300ms debounce on event list to avoid race conditions during sync
- **Release APK signed with debug keystore** — configured in build.gradle.kts for easy distribution

## Dependencies (Version Catalog)

Defined in `gradle/libs.versions.toml`. Key versions:
- AGP 8.7.3, Kotlin 2.1.0, KSP 2.1.0-1.0.29
- Compose BOM 2024.12.01, Material 3
- Hilt 2.54, Room 2.6.1, WorkManager 2.10.0
- Navigation Compose 2.8.5, Lifecycle 2.8.7
