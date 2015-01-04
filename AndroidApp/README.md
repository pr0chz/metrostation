
# MetroStation

## About

MetroStation is an simple app for notifying you on which subway station you are located. It utilizes information
about connected cells / disconnects and works completely offline.

## Building

 * Either change path to android sdk in pom file or supply it with cmdline param `-Dandroid.sdk.path=...path...`
 * You should have API SDK 15 installed via Android SDK Manager
 * `mvn clean install`
 
## Release build

You have to activate profile release and specify some key-store related parameters (which are for obvious reasons
not included in pom).

`mvn clean install -Prelease -Dkey.store.pass=xxx -Dkey.pass=xxx -Dkey.store.path=/path/to/keystore -Dkey.alias=metrostation-key`
 
