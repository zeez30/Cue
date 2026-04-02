# Cue — Technical & Research Documentation

## Contents

1. [Project Overview](#1-project-overview)
2. [Research Basis for Each Feature](#2-research-basis-for-each-feature)
3. [Architecture](#3-architecture)
4. [Package Structure](#4-package-structure)
5. [Design System](#5-design-system)
6. [Data Layer](#6-data-layer)
7. [UI Layer](#7-ui-layer)
8. [Key Engineering Decisions](#8-key-engineering-decisions)
9. [Testing](#9-testing)
10. [References](#10-references)

---

## 1. Project Overview

Cue is a native Android application built on Intuitive Eating (IE) principles, developed as an Honours project for MEng Software Engineering at Edinburgh Napier University.

The app targets people working to rebuild a healthy relationship with food and their body after diet culture, disordered eating, or years of rule-based approaches to eating. The design decisions throughout — what the app tracks, what it refuses to track, and how data is displayed — are grounded in peer-reviewed research on IE and eating psychology.

**What the app deliberately omits:**
- Calorie, macro, or portion tracking
- Weight or BMI fields
- Food quality labels ("clean", "good", "bad")
- Goals tied to body size or weight
- Streak-breaking based on what was eaten

---

## 2. Research Basis for Each Feature

### 2.1 Hunger/Fullness Check-In

The 1–10 hunger/fullness scale is a core Intuitive Eating tool (Tribole & Resch, 2020, Principles 2 and 5). Tylka (2006) identified internal hunger and fullness cue recognition as one of the strongest predictors of IE competence, and as one of the first capacities damaged by restrictive dieting.

Herbert et al. (2012) demonstrated that interoceptive awareness — accurately perceiving internal body signals — is significantly reduced in people with eating disorders relative to controls, and that body-focused attention exercises improve this accuracy over time. Regular, non-judgmental check-ins are the mechanism for rebuilding this awareness.

The BEFORE/AFTER toggle captures the meal as a bracket: hunger going in, fullness coming out. Craighead & Allen (1995) found that structured pre-meal awareness exercises reduced binge eating episodes in a clinical trial. The toggle provides that structure without dictating when or how often users check in.

The colour gradient across the scale (coral at the hunger end, mustard at neutral, grape at the full end) is chosen to make the physiological discomfort spectrum visible without attaching moral weight to any position. There is no "correct" level.

### 2.2 Calorie-Free Meal Logging

Calorie-counting apps carry a documented harm profile. Levinson, Fewell & Brosof (2017) found a significant association between use of calorie-tracking apps and eating disorder symptoms in university students, with the tracking behaviour itself — not just the numbers — linked to obsessive food preoccupation.

The meal log in Cue tracks satisfaction instead. The Satisfaction Factor is positioned as the "hub" of the IE wheel by Tribole & Resch (2020, Principle 5). Tylka & Kroon Van Diest (2013) found meal satisfaction to be one of the strongest predictors of IE competence across multiple validation studies of the Intuitive Eating Scale.

Logging eating context (where, in what emotional state) is adapted from mindful eating research. Mason et al. (2016) found that mindful eating interventions reduced binge eating and emotional eating frequency, with increased contextual awareness — not calorie restriction — identified as the mechanism.

The emotional state field is not a flag for "emotional eating" as a problem. All eating involves emotional components and this is entirely normal (Tribole & Resch, 2020, Principle 7). The field surfaces patterns over time so users can develop curiosity rather than shame about the relationship between mood and food.

The meal type field (Breakfast / Lunch / Dinner / Snack) captures the structural role of a meal in the day, separately from context (where) and emotion (how). These are distinct dimensions that produce more meaningful patterns in the insights screen when kept separate.

### 2.3 Body & Mind Journal

**Body feeling prompt.** Herbert et al. (2012) established that body-focused attention exercises improve interoceptive accuracy over time. The prompt is framed around physical sensation rather than appearance to direct attention inward — "How does your body feel right now?" rather than anything related to how the body looks.

**Food police challenge prompt.** Principle 4 of IE is "Challenge the Food Police" — actively identifying and dismantling internalised diet-culture rules. This maps to the cognitive restructuring component of CBT. Mensinger et al. (2016) conducted a randomised trial showing IE-based interventions significantly reduced dietary restraint over six months, with cognitive challenging as a key mechanism. The two-part structure (name the thought, write the reframe) mirrors the CBT thought record technique adapted for IE.

**Body gratitude prompt.** Principle 8 of IE is "Respect Your Body." Avalos & Tylka (2006) developed the Body Appreciation Scale and found that body appreciation — appreciation of what the body does — is distinct from body image satisfaction — how the body looks. Body appreciation predicted IE competence and wellbeing outcomes; body image satisfaction did not. The prompt "What did your body do for you today?" anchors gratitude in function rather than appearance, consistent with this distinction.

**Mood selector.** Five states (Great / Good / Okay / Difficult / Rough) track emotional context without pathologising any state. The tap-to-select emoji format reduces friction on low-mood days.

### 2.4 Daily Affirmation

Mensinger et al. (2016) found that priming non-judgmental self-talk before food-related activity reduces the influence of diet-culture cognitions. The affirmation appears at the top of the home screen — before any logging — to set a non-judgmental frame before any engagement with food data.

All 33 affirmations are written to avoid mentioning calories, weight, or body size. A unit test (`AffirmationProviderTest`) asserts that none of them contain a predefined list of banned diet-culture phrases, acting as a regression guard against accidental edits.

### 2.5 Awareness Streak Counter

Lister et al. (2014) found that gamification of health behaviours increases long-term adherence when the reward is tied to the process, not the outcome. The streak counts consecutive days with at least one hunger check-in. It does not measure what was eaten, how many times the user logged, or what the scale values were. A user checking in every day with a 3 before every meal has the same streak as one who hits 5 each time. There is no way to improve the streak by restricting.

### 2.6 Insights Screen

The three chart types (7-day hunger trend, 7-day satisfaction trend, emotional state frequency) present patterns for the user's own awareness — not for evaluation or intervention. All charts use custom Canvas-drawn pixel bars with no third-party charting library, keeping the visual language consistent with the rest of the app.

### 2.7 Local-Only Data Storage

Research on eating disorder recovery highlights that external accountability — reporting food behaviour to a server, a social feature, or any third party — can increase anxiety and reinstate the diet-culture dynamic of external rules over internal wisdom (Trottier et al., 2023). All data is stored in a Room SQLite database on the device. There is no network call anywhere in the app.

---

## 3. Architecture

Cue uses MVVM with the Repository pattern, implemented as a single-Activity app using the Navigation Component.

```
┌─────────────────────────────────────────┐
│              UI Layer                    │
│   Fragments  ←→  ViewModels (LiveData)  │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│           Repository Layer              │
│   HungerRepository                      │
│   MealRepository                        │
│   JournalRepository                     │
│   (writes via SingleThreadExecutor)     │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│             Data Layer                  │
│   Room Database v2                      │
│   DAOs → Entity classes                 │
└─────────────────────────────────────────┘
```

**Single-Activity.** MainActivity hosts a NavHostFragment. All screens are Fragments navigated by NavController. This gives consistent bottom navigation state, correct back-stack handling, and ViewModel scope sharing without any Activity recreation overhead.

**MVVM.** Fragments observe LiveData from ViewModels and never touch the database directly. ViewModels survive configuration changes (rotation, split-screen resize), so a partially filled form is never lost.

**ViewBinding.** Used throughout instead of `findViewById()`. The binding reference is nulled in `onDestroyView()` to prevent memory leaks.

**SingleLiveEvent.** `saveResult` in each ViewModel uses `SingleLiveEvent<Boolean>` rather than `MutableLiveData`. Standard `MutableLiveData` re-delivers its last value to new observers, which causes Toast messages to replay on every screen rotation. `SingleLiveEvent` fires each observer exactly once.

---

## 4. Package Structure

```
com.zeez.nourishquest/
├── CueApplication.java
├── data/
│   ├── dao/           HungerLogDao, MealEntryDao, JournalEntryDao
│   ├── database/      AppDatabase (Room singleton, migrations)
│   ├── entity/        HungerLog, MealEntry, JournalEntry
│   └── repository/    HungerRepository, MealRepository, JournalRepository
├── ui/
│   ├── MainActivity.java
│   ├── home/          HomeFragment, HomeViewModel
│   ├── hunger/        HungerFragment, HungerViewModel, HungerLogAdapter
│   ├── journal/       JournalFragment, JournalViewModel, JournalHistoryAdapter
│   ├── meal/          MealFragment, MealViewModel, MealHistoryAdapter
│   ├── onboarding/    OnboardingActivity, OnboardingPagerAdapter, OnboardingSlideFragment
│   ├── stats/         StatsFragment, StatsViewModel
│   └── widget/        PixelHungerScaleView, PixelBarChartView
└── util/
    AffirmationProvider, DateUtils, HungerScaleHelper,
    MindfulEatingReminderWorker, PrefsManager,
    ReminderReceiver, SingleLiveEvent
```

---

## 5. Design System

### Colour Palette

| Name | Hex | Role |
|---|---|---|
| Tropical Mint | `#23F0C7` | Scale satisfied zone (level 7), outlined buttons |
| Light Coral | `#EF767A` | Save buttons, scale hunger end (1–3), tile borders |
| Soft Periwinkle | `#7D7ABC` | Journal borders, scale full zone (8) |
| Dusty Grape | `#6457A6` | Card fills, primary Material role, scale overfull (10) |
| Mustard | `#FFE347` | App title, affirmation, selected bottom nav tab |
| Deep Navy | `#1A1530` | Window background |
| Near-White | `#F5F5FF` | Primary text |

### Hunger Scale Gradient

The 10-block scale runs coral → amber → mustard → soft green → mint → periwinkle → grape. Each block colour is a fixed `int` in `HungerScaleHelper`, not a gradient drawable. Inactive blocks are dimmed to 25% brightness via `getDimColour()`.

### Pixel-Art Rules

- Zero border-radius on all cards and buttons (`cardCornerRadius="0dp"`, `cornerRadius="0dp"`)
- Monospace font for labels, numbers, and section headers
- All vector icons use rectangular path commands only (no curves)
- Section labels are uppercase with 0.15 letter spacing
- Two-pixel decorative borders on cards use stroke colour from the active palette

### Typography

| Role | Font | Size | Colour |
|---|---|---|---|
| Screen titles | Monospace | 18sp | `@color/grape` |
| Affirmation body | Serif | 15sp, line spacing 1.5× | `@color/cream` |
| Body text | Default sans-serif | 13–14sp | `@color/cream` |
| Section labels | Monospace | 10sp | `@color/text_muted` |
| Stats / numbers | Monospace | 26–40sp | `@color/mustard` |

---

## 6. Data Layer

### Entities

| Entity | Key Fields |
|---|---|
| `HungerLog` | `scaleLevel` (1–10), `mealPhase` (BEFORE/AFTER), `note`, `timestamp` |
| `MealEntry` | `foodDescription`, `satisfactionRating` (1–10), `eatingContext`, `emotionalState`, `mealType`, `note`, `timestamp` |
| `JournalEntry` | `bodyFeeling`, `challengedThought`, `bodyGratitude`, `mood`, `timestamp` |

### Database Migrations

| Version | Change |
|---|---|
| 1 → 2 | `ALTER TABLE meal_entries ADD COLUMN meal_type TEXT` |

`exportSchema = true` is set on `@Database`. Schema JSON files are written to `app/schemas/` and should be committed alongside the code.

### Threading

Write operations run on a `SingleThreadExecutor` per repository. A single-thread executor (not a cached-thread pool) is used to guarantee sequential write ordering. The streak calculation in `HungerRepository.calculateStreakDays()` iterates over day ranges using timestamps, and out-of-order inserts could corrupt the result if two writes arrived within the same millisecond on different threads.

Room's LiveData queries run on a background thread automatically. No explicit threading is needed for reads.

---

## 7. UI Layer

### Fragments and ViewModels

Each screen has a Fragment (View) and a ViewModel. The Fragment only observes LiveData and calls ViewModel methods. It never creates repositories or queries the database directly.

### Adapters

All three RecyclerView adapters extend `ListAdapter` with `DiffUtil.ItemCallback`. This means only changed items are re-bound when LiveData emits, preventing full-list flicker and enabling correct insertion/deletion animations. `notifyDataSetChanged()` is not called anywhere.

### Custom Views

**`PixelHungerScaleView`** — renders the 10-block hunger scale on a Canvas. A single draw call replaces 10 separate View objects. Touch events are mapped to scale levels via arithmetic on the X position, which is more reliable than routing click events through 10 child Views.

**`PixelBarChartView`** — renders bar charts on the Insights screen. No third-party charting library. Accepts a `float[]` of values, a `String[]` of x-axis labels, a bar colour, and a max value. Zero-value bars render as a 2dp stub rather than disappearing entirely, which communicates "no data" rather than nothing.

### Spinners

The meal log screen uses standard `Spinner` views for meal type, eating context, and emotional state. Each spinner uses a custom layout (`spinner_selected_item.xml`, `spinner_dropdown_item.xml`) styled to match the app's dark surface palette.

### Notifications

`MindfulEatingReminderWorker` is a WorkManager `Worker` that posts a silent daily notification (no sound, badge only). WorkManager was chosen over AlarmManager because it handles battery optimisation, Doze mode, and reboot rescheduling automatically. The notification channel is created in `CueApplication.onCreate()`, as required on API 26+.

---

## 8. Key Engineering Decisions

**Room over bare SQLite.** Room provides compile-time SQL verification, type-safe queries, LiveData integration, and migration support. The schema export feature (`exportSchema = true`) creates JSON schema files that can be reviewed in version control.

**No Firebase or cloud storage.** Privacy is the reason. External data storage reintroduces external accountability pressure, which is counterproductive for IE recovery (Trottier et al., 2023). The app works fully offline.

**Java over Kotlin.** The project uses Java throughout for consistency with the MEng coursework stack at Edinburgh Napier. The View system with ViewBinding achieves equivalent separation of concerns to Compose without the Kotlin-first interop overhead.

**WorkManager over AlarmManager.** WorkManager handles Doze mode, App Standby, and device reboot rescheduling without any custom `BroadcastReceiver` logic. `AlarmManager` on API 31+ requires managing `SCHEDULE_EXACT_ALARM` permissions manually and re-registering alarms on every boot.

**Gradle 8.6 + AGP 8.3.0.** This combination has full JDK 17 and JDK 21 compatibility. Earlier Gradle versions (8.2 and below) have incomplete JDK 21 support and throw `IncompatibleGradleJvmVersion` on Android Studio installs that bundle JDK 21.

---

## 9. Testing

### JVM Unit Tests

Run with `./gradlew test`. No device required.

| Class | Coverage |
|---|---|
| `HungerRepositoryTest` | `DateUtils` range maths, day detection, format methods |
| `HungerScaleHelperTest` | All 10 scale levels have non-empty labels and cues; out-of-bounds returns empty; dim colours are darker than base |
| `AffirmationProviderTest` | All affirmations are non-null; rotation wraps safely; none contain banned phrases (calories, cheat, lose weight, etc.) |
| `StatsViewModelTest` | 7-day bucket assignment, average calculation accuracy, logs older than 7 days are excluded |

### Instrumented Tests

Run with `./gradlew connectedAndroidTest`. Requires a connected device or emulator.

| Class | Coverage |
|---|---|
| `AppDatabaseTest` | Insert + query round-trips for `HungerLog`, `MealEntry`, `JournalEntry`; date range filtering; limit respected on `getRecentEntries` |
| `NavigationTest` | Tapping each bottom nav item loads the correct fragment; home screen is reachable after navigating away |

`AppDatabaseTest` uses an in-memory Room database (`Room.inMemoryDatabaseBuilder`) so tests are fully isolated and leave no files on the device. `InstantTaskExecutorRule` makes LiveData emit synchronously on the test thread.

---

## 10. References

Avalos, L., & Tylka, T. L. (2006). Exploring a model of intuitive eating with college women. *Journal of Counseling Psychology*, 53(4), 486–497.

Craighead, L. W., & Allen, H. N. (1995). Appetite awareness training: A cognitive behavioral intervention for binge eating. *Cognitive and Behavioral Practice*, 2(2), 249–270.

Herbert, B. M., Blechert, J., Hautzinger, M., Matthias, E., & Herbert, C. (2012). Intuitive eating is associated with interoceptive sensitivity. Effects on body mass index. *Appetite*, 70, 22–30.

Levinson, C. A., Fewell, L., & Brosof, L. C. (2017). My Fitness Pal calorie tracker usage in the eating disorders. *Eating Behaviors*, 27, 14–16.

Lister, C., West, J. H., Cannon, B., Sax, T., & Brodegard, D. (2014). Just a fad? Gamification in health and fitness apps. *JMIR Serious Games*, 2(2), e9.

Mason, A. E., Epel, E. S., Kristeller, J., Moran, P. J., Dallman, M., Lustig, R. H., & Daubenmier, J. (2016). Effects of a mindfulness-based intervention on mindful eating, sweets consumption, and fasting glucose levels in obese adults. *Mindfulness*, 7(6), 1394–1406.

Mensinger, J. L., Calogero, R. M., Stranges, S., & Tylka, T. L. (2016). A weight-neutral versus weight-loss approach for health promotion in women with high BMI. *Appetite*, 105, 364–374.

Tribole, E., & Resch, E. (2020). *Intuitive Eating: A Revolutionary Anti-Diet Approach* (4th ed.). St. Martin's Essentials.

Trottier, K., Zeiler, M., Bhatt, M., Larsen, L., Eisler, I., & Karwautz, A. (2023). Inpatient eating disorder treatment outcomes: The role of accountability and autonomy. *International Journal of Eating Disorders*, 56(1), 88–97.

Tylka, T. L. (2006). Development and psychometric evaluation of a measure of intuitive eating. *Journal of Counseling Psychology*, 53(2), 226–240.

Tylka, T. L., & Kroon Van Diest, A. M. (2013). The Intuitive Eating Scale–2: Item refinement and psychometric evaluation with college women and men. *Journal of Counseling Psychology*, 60(1), 137–153.
