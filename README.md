# Timy — Kisii University Academic Timetable & Venue Management System

> **“Where am I supposed to be, when, and has anything changed?”**

Timy is a native Android application built with Kotlin and Jetpack Compose for students and faculty at Kisii University. It delivers deterministic, zero-ambiguity timetable scheduling, real-time venue availability tracking, and instant schedule change notifications with a 100% offline-first architecture.

---

## Architecture Overview

Timy follows **Clean Architecture** with feature-oriented modularity and MVI unidirectional data flow:

```
Presentation (Compose Screens + ViewModels)
    ↓
Domain (Entities + Use Cases + Contracts)
    ↑
Data (TimyRepositoryImpl + Room SQLite + Retrofit REST API + DataStore)
```

### Persistence & Data Flow
- **Single Source of Truth**: Room Database is the authoritative source for all displayed timetables and notifications. ViewModels observe reactive `Flow`s directly from Room DAOs.
- **Offline Reliability**: When campus Wi-Fi or cellular networks are intermittent, the application seamlessly serves cached academic schedules, labels connection states as `OFFLINE`, and synchronizes updates in the background without dropping cached records.
- **Data Protection**: User tokens, student session metadata, and biometric preferences are stored in encrypted `DataStore Preferences`.

---

## Design System & Institutional Palette

Built strictly on Material Design 3 with custom institutional tokens tailored for Kisii University:

| Token | Hex | Role |
|---|---|---|
| **Obsidian** | `#080C14` | Primary dark background |
| **Kisii Navy** | `#0F2942` | Institutional brand surfaces & active emphasis |
| **Emerald** | `#10B981` | Active lectures & available venue states |
| **Amber** | `#F59E0B` | Schedule relocations, warnings, and time changes |
| **Crimson** | `#EF4444` | Class cancellations and urgent administrative alerts |
| **Text Primary** | `#F8FAFC` | High-contrast typography |
| **Text Secondary** | `#94A3B8` | Supporting metadata & timestamps |

Typography utilizes clean sans-serif paired with **monospace** typography for room codes (`LH 26`, `NSC Lab 1`), course codes (`SOEN 311`, `COMP 314`), and precise time ranges (`08:00 – 11:00`).

---

## Core Features & Screens

### 1. Onboarding & Authentication
- **Welcome Screen**: Clean presentation of Timy's zero-ambiguity value proposition.
- **Student Sign In**: Admission number verification (`IN16/00142/23`), password visibility toggle, detected cohort indicator, and validation.
- **Biometric Enrollment**: Optional biometric unlock with Android Keystore protection.
- **Initial Sync**: Progressive step-by-step loading and persistence into local Room SQLite.

### 2. Home Dashboard (Daily Agenda)
- **Institutional Header**: Kisii University crest, active cohort badge, live connection status pill (`LIVE` / `SYNCING` / `OFFLINE` / `SYNC FAILED`), notification bell with unread badge.
- **Foreground Alert Banner**: In-app animated banner for urgent venue relocations or cancellations.
- **Weekly Day Selector**: Monday to Saturday horizontal chip selector with current day highlighting and schedule change alert dots.
- **Happening Now Hero**: Prominent card displaying active lecture countdown (`Ends in 42 min`), venue badge, lecturer, and relocation notes.
- **Lecture Cards**: Four distinct semantic states:
  1. *Normal*: Clean code, title, time range, and lecturer.
  2. *Active*: Emerald border and active pulse pill.
  3. *Rescheduled*: Amber accent with strikethrough original venue (`LH 14 → NSC Lab 2`).
  4. *Cancelled*: Crimson accent with official faculty reason (`Faculty Board Defense`).
- **Free Period State**: Illustrated restful indicator when no lectures are scheduled.

### 3. Weekly Schedule Inspector
- Full weekly calendar view across all 6 academic days.
- Quick filter chips: *All Sessions*, *Schedule Changes Only*, *Practical Labs Only*.
- Day-by-day tab jump navigation.

### 4. Venue Availability Inspector
- Debounced room search for lecture halls (`LH 26`, `LH 14`) and computer laboratories (`NSC Lab 1`, `Software Lab 3`).
- Availability status badges: `AVAILABLE`, `OCCUPIED`, `MAINTENANCE`.
- Detailed inspection bottom sheet showing seating capacity, building, current occupying unit, and next scheduled lecture.

### 5. Notification Center
- Tabbed alerts: *All*, *Venue Moves*, *Cancellations*.
- Unread indicator dots and "Mark all read" action.
- Direct linking to affected lecture details.

### 6. Student Profile & Preferences
- Student identification and enrolled program.
- Cohort switcher (Software Engineering, Computer Science, Information Technology, Applied Statistics).
- Biometric lock toggle and dark/light mode toggle.
- Room database cache metrics (last synced timestamp, force sync action).
- Secure sign out with complete session cleanup.

---

## Backend API Specification

Timy integrates with the Kisii University Academic Timetable API:

- `POST /api/v1/auth/login`: Student portal authentication.
- `POST /api/v1/auth/refresh`: Safe token refresh avoiding recursive loops.
- `GET /api/v1/timetables/schedule`: Cohort weekly timetable retrieval.
- `GET /api/v1/notifications`: Push and in-app administrative alerts.
- `PATCH /api/v1/notifications/{id}/read`: Mark notification as read.
- `GET /api/v1/venues`: Campus room directory and capacity metrics.
- `GET /api/v1/venues/{id}/availability`: Real-time room occupancy state.

---

## Verification & Testing

- **Compilation**: Verified via `./gradlew assembleDebug` / `compile_applet`.
- **Unit & Robolectric Tests**: Verified via `gradle :app:testDebugUnitTest`.
  - String resource validation.
  - Active session countdown calculations.
  - Timetable status models and venue strikethrough formatting.
  - Venue availability derivation.
  - Notification filtering.
