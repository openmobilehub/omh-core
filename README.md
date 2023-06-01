[![GitHub license](https://img.shields.io/github/license/openmobilehub/omh-core)](https://github.com/openmobilehub/omh-core/blob/main/LICENSE)
![GitHub contributors](https://img.shields.io/github/contributors/openmobilehub/omh-core)
[![API](https://img.shields.io/badge/API-21%2B-green.svg?style=flat)](https://developer.android.com/studio/releases/platforms#5.0)

# OMH (Open Mobile Hub) - Core

### General context

*OMH is an open-source Android SDK to make easily swap between GMS, Non-GMS and our custom OHM services.*

*It aims at creating low coupled, extensible SDK reducing the code boilerplate of switching between GMS, Non-GMS, or any other service, and also provides a custom full open source alternative services switching automatically according to your configuration in the Gradle plugin giving the right outputs without overloading your APK with unnecessary libraries.*

### OMH - Core

The OMH Core is a [Gradle plugin](https://docs.gradle.org/current/userguide/plugins.html) that allows you to configure, implement and work with the OMH SDK, you can group in bundles depending how you would like to work. For further information please check out our wiki section.

* [Installation](#installation)
* [Basic Usage](#basic-usage)
* [Documentation](#documentation)
* [Contributing](#contributing)

## Basic Usage

Gradle Kotlin DSL:

```kotlin
plugins {
    ...
    id("omh-core")
}

...

omhConfig {
    bundle("worldwide") {
        auth {
            addGmsService("com.omh.android:auth-api-gms:1.0-SNAPSHOT") 
            addNonGmsService("com.omh.android:auth-api-non-gms:1.0-SNAPSHOT")
        }
    }

    bundle("gmsStore") {
        auth {
            addGmsService("com.omh.android:auth-api-gms:1.0-SNAPSHOT") 
        }
    }

    bundle("nonGmsStore") {
        auth {
            addNonGmsService("com.omh.android:auth-api-non-gms:1.0-SNAPSHOT")
        }
    }

    defaultServices {   
        auth("com.omh.android:auth-api-non-gms:1.0-SNAPSHOT")
    }
}
```

You can also use this plugin using Gradle Groovy DSL:

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

    defaultServices {
        it.auth 'com.omh.android:auth-api-non-gms:1.0-SNAPSHOT'
    }
}
```

## Documentation

See example and check the full documentation at our Wiki.

## Contributing

We'd be glad if you decide to contribute to this project.

All pull requests are welcome, just make sure that every work is linked to an issue on this repository so everyone can track it.
