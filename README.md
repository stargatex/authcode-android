[![](https://jitpack.io/v/stargatex/authcode-android.svg)](https://jitpack.io/#stargatex/authcode-android)

# authcode-android
The AuthCode Android library provides secure sign-in to Android applications

### Download and configure
Repo available on https://jitpack.io/#stargatex/authcode-android

1. Add the JitPack repository to your build file

```Gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency

```Gradle
dependencies {
    implementation 'com.github.stargatex:authcode-android:$VERSION'
}
```

3. Add `appAuthRedirectScheme` as a manifest placeholder and its value in the `build.gradle`. You can find the redirect scheme value in your callback URL that is configured on your identity server.
    If your callback url is `demo://xyz/callback` then your `appAuthRedirectScheme` is `demo`
```Gradle
android.defaultConfig.manifestPlaceholders = [
        'appAuthRedirectScheme': "demo"
]
```

4. Inside your Android application `res/raw` floder create `ocid.json` file with the following configuration. `issueUri` and `registrationUri` are optional in the current version (0.1.2-alpha).
```json
{
  "issueUri": "YOUR_OPENID_CONFIGURATION_DISCOVERY_URI",
  "clientId": "YOUR_CLIENT_ID",
  "redirectUri": "YOUR_REDIRECT_URI",
  "logoutRedirectUri": "YOUR_LOGOUT_REDIRECT_URI",
  "authorizationUri": "YOUR_AUTHORIZATION_URI",
  "tokenUri": "YOUR_TOKEN_URI",
  "registrationUri": "YOUR_REGISTRATION_URI",
  "endSessionUri": "YOUR_END_SESSION_URI",
  "scope": "openid profile offline_access YOUR_SCOPE"
}
```
### Implementation
> Updating..