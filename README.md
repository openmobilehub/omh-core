[![GitHub license](https://img.shields.io/github/license/openmobilehub/omh-core)](https://github.com/openmobilehub/omh-core/blob/main/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-core)
[![API](https://img.shields.io/badge/API-21%2B-green.svg?style=flat)](https://developer.android.com/studio/releases/platforms#5.0)

# OMH (Open Mobile Hub) - Core

## Overview

*OMH is an open-source Android SDK to make easily swap between GMS, HMS and our custom OHM services.*

*It aims at creating low coupled, extensible SDK reducing the code boilerplate of switching between GMS, HMS, or any other service, and also provides a custom full open source alternative services switching automatically according to your configuration in the Gradle plugin giving the right outputs without overloading your APK with unnecessary libraries.*

## OMH - Core

The OMH Core is a [Gradle plugin](https://docs.gradle.org/current/userguide/plugins.html) that allows developers to configure, enable and set-up the OMH SDK in their projects.This plugin automatically implements the necessary dependencies and enable the custom-build variants to allow you compile the different builds to use the defined providers.


## Installation
Go to your app build.gradle file and add the following:

```groovy
plugins {
    ...
    id 'omh-core'
}

...

omhConfig {
    bundle('worldwide') {
        it.auth {
            addGmsService 'com.omh.android:auth-api-gms:1.0-SNAPSHOT'
            addNonGmsService 'com.omh.android:auth-api-non-gms:1.0-SNAPSHOT'
        }
    }

    bundle('gmsStore') {
        it.auth {
            addGmsService 'com.omh.android:auth-api-gms:1.0-SNAPSHOT'
        }
    }

    bundle('nonGmsStore') {
        it.auth {
            addNonGmsService 'com.omh.android:auth-api-non-gms:1.0-SNAPSHOT'
        }
    }
}
```

You can also see this video: 

https://github.com/openmobilehub/omh-core/assets/10377529/1b142648-9005-43cd-804b-8e80e1b0ea07


## Usage

### Step 1: Create an instance of OmhAuthClient
```
private val omhAuthProvider = OmhAuthProvider.Builder()
    .addGmsPath(BuildConfig.AUTH_GMS_PATH)
    .addNonGmsPath(BuildConfig.AUTH_NON_GMS_PATH)
    .build()        
```
```
private val omhAuthClient = omhAuthProvider.provideAuthClient(
    scopes = listOf("openid", "email", "profile"),
    clientId = "YOUR_CLIENT_ID",
    context = applicationContext
)
```
### Step 2: Start using some functions

In you sample app, perform the authentication
```
private fun startLogin() {
    val loginIntent = omhAuthClient.getLoginIntent()
    loginLauncher.launch(loginIntent)
}
```
If the authentication is successful, then navigate to your desired screen
```
private val loginLauncher: ActivityResultLauncher<Intent> =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        try {
            omhAuthClient.getAccountFromIntent(result.data)
            navigateToLogIn()
        }  catch (exception: OmhAuthException) {
            val errorMessage = OmhAuthStatusCodes.getStatusCodeString(404)
        }
    }
```

You can also see the video:

https://github.com/openmobilehub/omh-core/assets/10377529/b71d0fad-31cf-4a7c-983d-f3542045f3f1

## Documentation

See example and check the full documentation at our Wiki.

## Contributing

We'd be glad if you decide to contribute to this project.

All pull requests are welcome, just make sure that every work is linked to an issue on this repository so everyone can track it.

## Common issue: There is an certain error when using gradle 8.0

* Add this code
```
packagingOptions {
    exclude("META-INF/DEPENDENCIES")
}
```
