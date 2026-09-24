# R8 rules for the Android release build.
#
# Compose, AndroidX and kotlinx.serialization ship their own consumer rules, so this only
# covers what the app itself relies on at runtime.

# Type-safe navigation routes in App.kt are @Serializable classes looked up through their
# generated serializers; keep those companions so route (de)serialization survives R8.
-keepclassmembers @kotlinx.serialization.Serializable class com.pixel.composeexperiments.** {
    static ** Companion;
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.pixel.composeexperiments.**$$serializer { *; }

# Keep file/line info so Play Console crash reports are readable after deobfuscation.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
