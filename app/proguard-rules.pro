# SignalBot release configuration

# Retrofit / Gson
-keepattributes Signature
-keepattributes *Annotation*

-keep class com.farhad.signalbot.core.model.** { *; }
-keep class com.farhad.signalbot.data.remote.** { *; }

# Room
-keep class androidx.room.** { *; }

# Preserve model constructors used by reflection.
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Remove unnecessary logging in release.
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
