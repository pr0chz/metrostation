
# MetroStation

## About

MetroStation is an simple app for notifying you on which subway station you are located. It utilizes information
about connected cells / disconnects and works completely offline.

## Versioning

Maven versioning is not utilized as it currently makes not sense. Versioning is done just in AndroidManifest.xml and 
should be done according to semver. All releases should be tagged with annotated tag:

git tag -a v1.0.0

## Building

 * Either change path to android sdk in pom file or supply it with cmdline param `-Dandroid.sdk.path=...path...`
 * You should have API SDK 15 installed via Android SDK Manager
 * `mvn clean install`
 
## Release build

You have to activate profile release and specify some key-store related parameters (which are for obvious reasons
not included in pom).

`mvn clean install -Prelease -Dkey.store.pass=xxx -Dkey.pass=xxx -Dkey.store.path=/path/to/keystore -Dkey.alias=metrostation-key`
 
If you do not have a key already, you can generate it:

`keytool -genkey -v -keystore keystore -alias metrostation-key -keyalg RSA -keysize 2048 -validity 10000`


