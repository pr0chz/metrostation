
# MetroStation

## About

MetroStation is an simple app for notifying you on which subway station you are located. It utilizes information
about connected cells / disconnects and works completely offline.

## Building

 * Install *Android Support Repository* from *Android SDK Manager* - maven repo should be created in
  `${sdk}/extras/android/m2repository`
 * Either change path to android sdk in pom file or supply it with cmdline param `-Dandroid.sdk.path=...path...`
 * `mvn clean install`
 
## Release build

 * Use `release` profile
 * If you have problems with proguard complaining about Java 8, download new proguard (>5) and put it into 
 the android dir `sdk/tools/proguard`
 


