# Migrating from Zendrive SDK to Fairmatic SDK

If you are using Zendrive SDK, this guide will help you with the quick steps you need to follow to
migrate to 3.x version of the Fairmatic SDK.
> Copy the Fairmatic SDK key, available in
> the [advanced tab](https://app.fairmatic.com/app/settings/advanced) of the settings screen on the
> Fairmatic dashboard. Use the Fairmatic SDK key for Fairmatic SDK. Note that the Fairmatic SDK key *
*can NOT** be used with the Zendrive SDK or vice versa.

1. Update dependency version in your build files

```groovy
implementation 'com.fairmatic:sdk:3.0.0'
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
minSdk 26
```

4. Remove your `ZendriveBroadcastReceiver` implementation and all its usages
5. Remove your `ZendriveNotificationProvider` implementation and all its usages. This has been
   changed as a parameter on the setup method (`FairmaticTripNotification`)
6. Split `ZendriveDriverAttributes alias` into 2 parameters - `firstName` & `lastName`
```kotlin
private val fairmaticDriverAttributes = FairmaticDriverAttributes(
    firstName = "John",
    lastName = "Doe",
    email = "john_doe@company.com",
    phoneNumber = "1234567890"
)
```

7. Use `FairmaticSettingsCallback`, instead of `ZendriveSettingsCallback`. Change `onComplete` 
   ` List<FairmaticSettingError>`
```kotlin
object : FairmaticSettingsCallback {
    override fun onComplete(errors: List<FairmaticSettingError>) {
        ...
    }
}   
```

8. Replace all the `Zendrive.startPeriodX()` API calls with `Fairmatic.startPeriodX()`. Also, the `startPeriod1()` API accepts a `trackingId` string on the Fairmatic SDK to stay consistent with the other insurance period APIs.
9. `Fairmatic.isValidInputParameter(driverId)` is now removed. You'll get all errors in the
    callback result (`FairmaticOperationResult.Error`)
