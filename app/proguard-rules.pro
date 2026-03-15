# Add project specific ProGuard rules here.

# Keep Room entities
-keep class com.phykawing.calendaralarm.data.local.entity.** { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep enum classes used by Room converters
-keepclassmembers enum com.phykawing.calendaralarm.domain.model.** { *; }
