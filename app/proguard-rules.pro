# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

-repackageclasses

-keepclassmembers class com.example.mymediaplayer.data.dto.MediaDto {
    private <fields>;
}

# This is generated automatically by the Android Gradle plugin
-dontwarn com.google.android.exoplayer2.source.rtsp.RtspMessageChannel$MessageParser$ReadingState