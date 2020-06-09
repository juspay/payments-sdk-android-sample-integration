 # Juspay SDK Test Integration App

 ## Installation

 Clone the Android project.

 ```
 git clone https://github.com/juspay/payments-sdk-android-sample-integration.git
 ```

 ## Getting started

 Run the project using the latest version of [Android Studio](https://developer.android.com/studio).

 ## Adding/Modifying Maven URL

 SDK can be added/modified by adding the following repository in `build.gradle`

 ```
 maven { url "https://maven.juspay.in/jp-build-packages/hyper-sdk/" }
 ```

 ## Adding/Modifying the latest SDK

 SDK can be added/modified by adding the following dependency in `app/build.gradle`

 ```
 implementation 'in.juspay:hypersdk:2.0.0-rc.47'
 ```

 ## Modifying SDK Credentials

 SDK credentials can be accessed as constants in ```app/src/main/java/in/juspay/TestIntegrationApp/PayloadConstants.java```

 ## Learn more

 [Juspay Integration Documentation](https://developer.juspay.in/)