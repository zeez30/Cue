# Cue

A native Android app built around Intuitive Eating. No calorie tracking, no weight goals, no food rules.

---

## Requirements

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum device API 26 (Android 8.0)

## Building

Open the project folder in Android Studio and sync Gradle. Run on a device or emulator (API 26+).

For an emulator, use a **Google APIs** image rather than Google Play to avoid unrelated system service crashes in Logcat.

Run unit tests without a device:
```bash
./gradlew test
```

Run instrumented tests on a connected device or emulator:
```bash
./gradlew connectedAndroidTest
```

---

## Screens

| Screen | Description |
|---|---|
| **Home** | Daily affirmation, check-in streak, 7-day satisfaction average, quick-action tiles |
| **Hunger Check-In** | 1–10 pixel block scale, BEFORE/AFTER meal toggle, today's check-in history |
| **Meal Log** | Food description, satisfaction slider (1–10), meal type, eating context, emotional state |
| **Body Journal** | Three prompts: body feeling, food-police challenge, body gratitude. Mood picker. |
| **Insights** | 7-day hunger trend, 7-day satisfaction trend, emotional state frequency — all pixel bar charts |
| **Onboarding** | 5-slide first-launch explainer covering what IE is and what this app does and does not track |

---

## Colour Palette

| Hex | Name | Used for |
|---|---|---|
| `#23F0C7` | Tropical Mint | Scale satisfied zone, outlined buttons |
| `#EF767A` | Light Coral | Save buttons, scale hunger end, action borders |
| `#7D7ABC` | Soft Periwinkle | Journal card borders, scale full zone |
| `#6457A6` | Dusty Grape | Card backgrounds, primary nav colour |
| `#FFE347` | Mustard | App title, affirmation text, selected nav tab |

Background: `#1A1530` (deep navy). Text: `#F5F5FF`.

---

## Architecture

Single-Activity · MVVM · Repository Pattern · Room (SQLite) · LiveData · Navigation Component · ViewBinding

```
Fragments → ViewModels → Repositories → Room DAOs → SQLite
```

All data lives on-device only. No network calls, no analytics, no cloud sync.

---

## Database

**Version 2.** Migration history:
- `1 → 2`: Added `meal_type` column to `meal_entries`

Room schema export is enabled. Migration files are in `app/schemas/`.

---

## Testing

**JVM unit tests** (`src/test/`):

| Class | Covers |
|---|---|
| `HungerRepositoryTest` | DateUtils range calculation, today detection, format methods |
| `HungerScaleHelperTest` | Scale label coverage, colour opacity, out-of-bounds guards |
| `AffirmationProviderTest` | Affirmation rotation, null safety, banned diet-language phrases |
| `StatsViewModelTest` | 7-day bucket calculation, average accuracy, edge cases |

**Instrumented tests** (`src/androidTest/`):

| Class | Covers |
|---|---|
| `AppDatabaseTest` | Room DAO insert and query round-trips for all three entities |
| `NavigationTest` | Bottom nav destinations load correct fragments |

---

## Research Foundation

Full references and per-feature research rationale are in [DOCUMENTATION.md](DOCUMENTATION.md).

Key sources: Tribole & Resch (2020), Tylka (2006), Tylka & Kroon Van Diest (2013), Herbert et al. (2012), Levinson et al. (2017), Mensinger et al. (2016), Avalos & Tylka (2006).
