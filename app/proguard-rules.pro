# NourishQuest ProGuard Rules
# Applied in release builds. Debug builds skip ProGuard entirely.

# ---- Room ----
# Room generates code at compile time; these rules prevent its annotations
# from being stripped in release builds, which would break DB queries silently.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }

# ---- Lifecycle (ViewModel + LiveData) ----
-keep class * extends androidx.lifecycle.ViewModel
-keep class * extends androidx.lifecycle.AndroidViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ---- Navigation Component ----
-keepnames class androidx.navigation.fragment.NavHostFragment

# ---- WorkManager ----
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ---- General Android ----
# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
# -renamesourcefileattribute SourceFile

# Suppress warnings for classes that are referenced but not in the classpath
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
