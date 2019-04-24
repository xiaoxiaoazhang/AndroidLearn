# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# Annotations are for embedding static analysis information.
-dontwarn org.jetbrains.annotations.**
-dontwarn com.google.errorprone.annotations.**

# Retain generated TypeAdapters if annotated type is retained.
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class *
-keep class <1>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class **$*
-keep class <1>_<2>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class **$*$*
-keep class <1>_<2>_<3>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class **$*$*$*
-keep class <1>_<2>_<3>_<4>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class **$*$*$*$*
-keep class <1>_<2>_<3>_<4>_<5>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}
-if @com.ryanharter.auto.value.gson.GenerateTypeAdapter class **$*$*$*$*$*
-keep class <1>_<2>_<3>_<4>_<5>_<6>_GsonTypeAdapter {
    <init>(...);
    <fields>;
}