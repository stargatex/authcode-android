[![](https://jitpack.io/v/stargatex/authcode-android.svg)](https://jitpack.io/#stargatex/authcode-android)

# authcode-android

The AuthCode Android library provides secure sign-in to Android applications

### Download and configure

Repo available on https://jitpack.io/#stargatex/authcode-android

1. Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.stargatex:authcode-android:$VERSION'
}
```

3. Add `appAuthRedirectScheme` as a manifest placeholder and its value in the `build.gradle`. You
   can find the redirect scheme value in your callback URL that is configured on your identity
   server. If your callback url is `demo://xyz/callback` then your `appAuthRedirectScheme` is `demo`

```groovy
android.defaultConfig.manifestPlaceholders = [
        'appAuthRedirectScheme': "demo"
]
```

4. Inside your Android application `res/raw` floder create `ocid.json` file with the following
   configuration. `issueUri` and `registrationUri` are optional in the current version (0.1.2-alpha)
   .

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

##### Login

1. As a first step, we will create an instance of `AuthConfiguration` in order to retrieve the
   configuration that needs to interact with the authorization server.

```kotlin
val authConfiguration: AuthConfiguration = DefaultAuthConfiguration(context = this)
```

2. Next, to start authentication, we will create a 'WebAuthProvider' instance and use the '
   AuthConfiguration' created earlier as the authConfiguration.

```kotlin
 val webAuthProvider: WebAuthProvider =
    DefaultWebAuthProvider(context = this, authConfiguration = authConfiguration)
```

3. Then we will call the `login` method of `WebAuthProvider` while passing the Intents of success
   and fail. After successful authentication, 'onSuccessIntent' will be used to redirect the user,
   while 'onFailIntent' will be used for cancellations. The `login` method of `WebAuthProvider`
   performs a set of actions consisting of configuring the request and retrieving the authorization
   code via the browser, then exchanging the authorization code for the token.

```kotlin
  (webAuthProvider as DefaultWebAuthProvider).login(
    context = this,
    onSuccessIntent = Intent(this, UserActivity::class.java),
    onFailIntent = Intent(this, MainActivity::class.java)
)
```

4. As a way to access the token results or the exception thrown during the process, the following
   code snippet can be placed into the activities that were supplied with 'onSuccessIntent' or '
   onFailIntent' intents, respectively.

```kotlin
val authorizationCodeFlowResults: AuthorizationCodeFlowResults? =
    AuthFlowResultHandler.getLoginResultFromIntent(intent)
```

##### Token refresh

To refresh the access token, we will call the `refreshAccessToken` method of `WebAuthProvider` while
passing the refresh token as shown below. This will return an AuthorizationCodeFlowResults object
which contains the token results or the exception thrown during the process.

```kotlin
 val refreshedTokenACFR: AuthorizationCodeFlowResults =
    (webAuthProvider as DefaultWebAuthProvider).refreshAccessToken(
        context = this@UserActivity,
        refreshToken = refreshToken
    )
```

##### Logout

To end the session we will call the `logout` method of `WebAuthProvider` while passing the IdToken
and an Intent to redirect the user on action completion.

```kotlin
 webAuthProvider.logout(
    context = this,
    idToken = idToken,
    onCompleteIntent = Intent(this, UserActivity::class.java)
)
```

Receive the results via the 'getLogoutResultFromIntent' method of `AuthFlowResultHandler`

```kotlin
 val endSessionFlowResults: EndSessionFlowResults? =
    AuthFlowResultHandler.getLogoutResultFromIntent(
        intent = intent
    )
```

> Updating..