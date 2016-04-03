
# MetroStation

## About

MetroStation is an simple app for notifying you on which subway station you are located. It utilizes information
about connected cells / disconnects and works completely offline.

## Versioning

Maven versioning is not utilized as it currently makes not sense. Versioning is done just in android manifest and
should be done according to semver. All releases should be tagged with annotated tag:

git tag -a v1.0.0

## Building

### Prerequisites:

 * Oracle JDK 8
 * Android SDK platform 16
 * 5.2 version of ProGuard inside Android SDK folders (see below)

Release build uses proguard to obfuscate and minimize app size. Unfortunately build needs JDK8
which makes default android proguard fail. As a workaround you can put new proguard version into
android-sdk/tools/proguard directory (keep default configuration files).

### Project structure

```
.
├── Cellar
├── MetroStation
└── Tracking
```

 * Cellar - small spring based web server for receiving data from clients.
 * MetroStation - main android application. Contains UI, settings and event retrieval code.
 Produces apk.
 * Tracking - most of the logic is here in plain-java library. Involves detection of location,
 predictions and via api is used by main application. Tests are using Java 8 but library needs
 to stay at Java 7 (which may give you a little trouble in IDE).

*** Running the build

Build uses Gradle wrapper or you can use Gradle 2.11. Release build of android application
needs following environment variables defined for signing:

```
MS_KEYSTORE
MS_KEYSTORE_PASS
MS_KEY
MS_KEY_PASS
```

Example of invoking full build is in release.sh script.



