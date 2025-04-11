# Integrate the Fairmatic iOS SDK in your app

## Prerequisites for the SDK

- The Fairmatic SDK works for Android API 26 (Oreo) and above.
- [Sign in](https://app.fairmatic.com/settings/advanced) to the Fairmatic dashboard to access your
  Fairmatic SDK Key.

## SDK Installation

1. In your module's build.gradle file, add the dependency.

```groovy
implementation 'com.fairmatic:sdk:3.0.2'
```

2. In your app's settings.gradle file, add the repository.

```groovy
mavenCentral()
```

3. Run gradle sync.

## SDK Setup

```kotlin
// Create the driver attributes object
val driverAttributes = FairmaticDriverAttributes(
    "firstName", "lastName",
    "email", "phoneNumber"
)

// Create the Fairmatic configuration object. 
// The driver ID must be unique to each driver.
val fairmaticConfiguration = FairmaticConfiguration(
    sdkKey, driverId, driverAttributes
)
// Create a Fairmatic notification object
val fairmaticNotification = FairmaticTripNotification(
    "Notification title", "Notification content",
    R.drawable.notificaiton_icon
)
// Call Fairmatic SDK setup
Fairmatic.setup(context, fairmaticConfiguration, fairmaticNotification,
    object : FairmaticOperationCallback {
        override fun onCompletion(result: FairmaticOperationResult) {
            // Confirm success
        }
    })
```

## Insurance Periods

1. Start insurance period 1 when the driver starts the day and is waiting for a request. The
   tracking ID is a key that is used to uniquely identify the insurance trip.

```kotlin
Fairmatic.startDriveWithPeriod1(
    context,
    trackingId,
    object : FairmaticOperationCallback {
        override fun onCompletion(result: FairmaticOperationResult) {
            // Confirm success
        }
    },
)
```

2. Start insurance period 2 when the driver accepts a request from the passenger or the company.

```kotlin
Fairmatic.startDriveWithPeriod2(...... )
```

3. Start insurance period 3 when the passenger/goods board the vehicle. In case of multiple
   passengers, the SDK needs to stay in insurance period 3.

```kotlin
Fairmatic.startDriveWithPeriod3(...... )
```

4. Stop the insurance period when the driver ends the work day. Call stop period when the driver is
   no longer looking for a request.

```kotlin
Fairmatic.stopPeriod(
    context,
    object : FairmaticOperationCallback {
        override fun onCompletion(result: FairmaticOperationResult) {
            // Confirm success
        }
    },
)
```

## Fairmatic Settings

Please check for any errors or warnings to ensure the Fairmatic SDK functions smoothly.
> [!NOTE]
> The SDK requires the Location permission to be set to 'Always On,' with precise location enabled
> and battery optimization disabled. While the other warnings are not mandatory, they are crucial for
> the SDK to function properly.

```kotlin
Fairmatic.getFairmaticSettings(
    context,
    object : FairmaticSettingsCallback {
        override fun onComplete(errors: List<FairmaticSettingError>) {
            // Check for errors and warnings
        }
    },
)
```

## Disable SDK [optional]

Call teardown API when the driver is no longer working with the application and logs out. This will
completely disable the SDK on the application.

```kotlin 
Fairmatic.teardown(
    context,
    object : FairmaticOperationCallback {
        override fun onCompletion(result: FairmaticOperationResult) {
            // Confirm success
        }
    },
)
```

