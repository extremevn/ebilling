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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

-keep, allowobfuscation class vn.com.extremevn.ebilling.*
-keepclassmembers, allowobfuscation class * {
    *;
}
-keepclassmembers, allowobfuscation interface * {
    *;
}
-keepnames class vn.com.extremevn.ebilling.billing.Billing
-keepnames class vn.com.extremevn.ebilling.billing.BillingProcessor
-keepnames class vn.com.extremevn.ebilling.billing.BillingActivity

-keep class vn.com.extremevn.ebilling.billing.Billing {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.Billing$* {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.BillingProcessor {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.BillingActivity {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.BillingException {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.ResponseCodes {
   public *;
}
-keep class vn.com.extremevn.ebilling.request.RequestException {
   public *;
}
-keep class vn.com.extremevn.ebilling.billing.request.* {
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.Billing {
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.Billing$* {
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.BillingProcessor{
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.BillingActivity{
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.BillingException{
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.ResponseCodes{
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.request.RequestException{
   public *;
}
-keepclassmembers class vn.com.extremevn.ebilling.billing.request.*{
   public *;
}
-keep interface vn.com.extremevn.ebilling.request.RequestListener {
   public *;
}
