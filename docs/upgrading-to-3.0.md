# Upgrading to Fairmatic SDK 3.0

1. Update dependency version in your build files
```groovy
dependencies {
	implementation 'com.fairmatic:sdk:3.0.0'
}
```
2. Add drivequant maven to your repositories block (settings.gradle or build.gradle)
```groovy
repositories {
    maven {
        url "https://maven.drivequant.com/repository/android-sdk/"
    }
}
```
3. Bump your minSdk version to `26`
```groovy
android {
    defaultConfig {
		minSdk 26
	}
}
```
4. Remove your `FairmaticBroadcastReceiver` implementation and all its usages
5. Remove your `FairmaticNotificationProvider` implementation and all its usages. This has been changed as a parameter to the setup method (`FairmaticTripNotification`)
6. Split `FairmaticDriverAttributes.name` into 2 parameters - `firstName` & `lastName`
7. In your `FairmaticSettingsCallback`, change `onComplete` from accepting `FairmaticSettings` to ` List<FairmaticSettingError>`
```kotlin
object : FairmaticSettingsCallback {  
        override fun onComplete(errors: List<FairmaticSettingError>) { ... }
}   
```
8. `FairmaticIssueType` is now `FairmaticSettingError`
9. These issue types have been removed:
    1. `POWER_SAVER_MODE_ENABLED`
    2. `GOOGLE_PLAY_SETTINGS_ERROR`
10. Add `trackingId` to `startInsurancePeriod1`
11. `Fairmatic.isValidInputParameter(driverId)` is now removed. You'll get all errors in the callback result (`FairmaticOperationResult.Error`)
