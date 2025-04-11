# Upgrading to Fairmatic SDK 3.0

1. Update dependency version in your build files

```groovy
implementation 'com.fairmatic:sdk:3.0.2'
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

4. Remove your `FairmaticBroadcastReceiver` implementation and all its usages
5. Remove your `FairmaticNotificationProvider` implementation and all its usages. This has been
   changed as a parameter on the setup method (`FairmaticTripNotification`)
6. Split `FairmaticDriverAttributes.name` into 2 parameters - `firstName` & `lastName`

```diff
private val fairmaticDriverAttributes = FairmaticDriverAttributes(
-      name = "John Doe",
+     firstName = "John",
+     lastName = "Doe",
       email = "john_doe@company.com",
       phoneNumber = "1234567890"
    )
```

7. In your `FairmaticSettingsCallback`, change `onComplete` from accepting `FairmaticSettings` to
   ` List<FairmaticSettingError>`

```kotlin
object : FairmaticSettingsCallback {
    override fun onComplete(errors: List<FairmaticSettingError>) {
        ...
    }
}   
```

8. `FairmaticIssueType` is now `FairmaticSettingError`
10. Add `trackingId` to `startInsurancePeriod1`
11. `Fairmatic.isValidInputParameter(driverId)` is now removed. You'll get all errors in the
    callback result (`FairmaticOperationResult.Error`)
