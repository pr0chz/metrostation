
# Metro Station Prague

Metro Station Prague is an simple android app for notifying you on which subway station you are (in Prague). 
It works completely offline utilizing network cell information.

[Get it on Google Play](https://play.google.com/store/apps/details?id=cz.prochy.metrostation&hl=cs)

## Motivation

I was spending considerable amount of subway travel time by watching MOOC videos on my phone and 
having earphones I was being constantly confused where I am. This application solved my problem by 
showing me where I am on phone display (where I was looking anyway).

## Features

First proof-of-concept was created by riding through Prague Metro with few phones to 
capture cell information for all operators. From that time application evolved, features include:
 
 * **Three types of on-display notification** - Notification area, toasts and overlay (most useful for watching 
   videos, but most intrusive).
 * **Predictive notifications** - Phone can be quite slow to catch signal after the blackout in tunnel.
   Notification often arrived too late. Predictive notifications are trying to solve this problem by 
   deducing line and direction of ride to predict next station.
 * **Cell logging** - Anonymous cell data are sent to server and later processed to fix / add missing cells.
   This enables application to keep working over time as cells are sometimes changing.
 * **Small, battery efficient, non-obtrusive, ad-free application.**

## Known limitations

Application is not 100% reliable. There are few situations which can prevent application from working correctly:

 * Phone sometimes skips station (does not catch signal) or does not update location on background
 * Generally all places where subway goes above the ground (mostly remote parts of Metro) contain lot of 
   cell ids. It is hard or maybe even impossible to assign them to stations, so app may not work perfectly 
   there.
 * As permanent cell signal will spread to more and more stations, there may be some other approach needed.

## Building

### Project structure

```
.
├── Cellar
├── MetroStation
├── Snake
└── Tracking
```

 * **Cellar** - Small spring based web server for receiving data from clients.
 * **MetroStation** - Main android application. Contains UI, settings and event retrieval code.
 Produces apk.
 * **Snake** - Small quick and dirty Scala program to analyze cell data from phones. It produces "snakes" of event 
 chains in which it is visually easy to identify problems.
 * **Tracking** - most of the logic is here in plain java library. Involves detection of location,
 predictions and via api is used by main application. Tests are using Java 8 but library needs
 to stay at Java 7 (which may give you a little trouble in IDE).

### Prerequisites:

 * Oracle JDK 8
 * Android SDK platform 22
 * 5.2 version of ProGuard inside Android SDK folders (see below)
 * Scala 2.11.7 for cell data analyzer

Release build uses proguard to obfuscate and minimize app size. Unfortunately build needs JDK8
which makes default android proguard fail. As a workaround you can put new proguard version into
android-sdk/tools/proguard directory (keep default configuration files).


### Running the build

Build uses Gradle wrapper or you can use Gradle 2.14. Release build of android application
needs following environment variables defined for signing:

```
MS_KEYSTORE
MS_KEYSTORE_PASS
MS_KEY
MS_KEY_PASS
```

If you deploy your own cell logging server you also need to specify an url variable
```
MS_UPLOAD_URL
```

Example of invoking full build (fill in your values):

```
#!/bin/bash

export MS_UPLOAD_URL=http://xx.xx.xx.xx:48989/store

export MS_KEYSTORE=~/projects/keystore
export MS_KEYSTORE_PASS=keystore_password
export MS_KEY=metrostation-key
export MS_KEY_PASS=key_password

./gradlew clean build

```





