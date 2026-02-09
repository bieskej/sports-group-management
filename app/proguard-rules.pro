# ============================================================
# Project ProGuard / R8 rules
# ============================================================

# Keep annotations & generic signatures (Retrofit/Gson need these)
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# ============================================================
# RETROFIT & OKHTTP
# ============================================================

# Keep Retrofit interfaces and HTTP annotations
-keep class retrofit2.** { *; }
-keep interface retrofit2.http.** { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep OkHttp
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# Keep your API interface + api package
-keep interface ba.sum.fsre.sportska_grupa.api.SupabaseAPI { *; }
-keep class ba.sum.fsre.sportska_grupa.api.** { *; }

# ============================================================
# GSON
# ============================================================

# Keep gson itself
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep your model classes (Gson maps these)
-keep class ba.sum.fsre.sportska_grupa.models.** { *; }
-keep class ba.sum.fsre.sportska_grupa.models.request.** { *; }
-keep class ba.sum.fsre.sportska_grupa.models.response.** { *; }

# Keep fields annotated with @SerializedName
-keepclassmembers class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================================
# ANDROID COMPONENTS (safe)
# ============================================================
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# ============================================================
# OPTIONAL: remove logs in release (keep if you want)
# ============================================================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
