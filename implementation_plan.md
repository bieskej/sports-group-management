# Implementation Plan - Prepare for Play Store

The goal is to update the project configuration to meet Google Play Store requirements for publishing.

## User Review Required
> [!IMPORTANT]
> **Keystore Credentials**: The plan involves configuring the app to use `gradle.properties` for keystore passwords. You will need to manually create a `gradle.properties` file (if not exists) and add your actual passwords there. I will NOT put passwords in the code.

## Proposed Changes

### App Configuration
#### [MODIFY] [build.gradle.kts](file:///c:/Users/Bsk/Desktop/sports-group-management4/app/build.gradle.kts)
- Add `signingConfigs` block to read from keystore file and properties.
- Update `buildTypes.release` to use the signing config.
- Add `bundle` block to enable APK splitting (optimized for Play Store).



## Verification Plan

### Automated Tests
- Run `./gradlew bundleRelease` to verify that the signed App Bundle can be built successfully.
- Verify that `app/build/outputs/bundle/release/app-release.aab` is generated.

### Manual Verification
- User to verify they can install the release APK on a physical device:
  `./gradlew assembleRelease`
  `adb install app/build/outputs/apk/release/app-release.apk`
