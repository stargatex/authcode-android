[![](https://jitpack.io/v/stargatex/authcode-android.svg)](https://jitpack.io/#stargatex/authcode-android)

# authcode-android

The AuthCode Android is a library that leverages [AppAuth-Android(openid)](https://github.com/openid/AppAuth-Android) to provide PKCE-enhanced Authorization Code Flow for Android applications.

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
   configuration. `issueUri` and `registrationUri` are optional in the current version.

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

#### Login

1. As a first step, we will create an instance of `AuthConfiguration` in order to retrieve the
   configuration that needs to interact with the authorization server.

<details open>
<summary>Using Kotlin</summary>

```kotlin
val authConfiguration: AuthConfiguration = DefaultAuthConfiguration(context = this)
```

</details>

<details>
<summary>Using Java</summary>

```java
AuthConfiguration authConfiguration = new DefaultAuthConfiguration(this);
```

</details>

2. Next, to start authentication, we will create a 'WebAuthProvider' instance and use the '
   AuthConfiguration' created earlier as the authConfiguration.

<details open>
<summary>Using Kotlin</summary>

```kotlin
 val webAuthProvider: WebAuthProvider =
    DefaultWebAuthProvider(context = this, authConfiguration = authConfiguration)
```

</details>

<details>
<summary>Using Java</summary>

```java
WebAuthProvider webAuthProvider = new DefaultWebAuthProvider(this,authConfiguration);
```

</details>

3. Then we will call the `login` method of `WebAuthProvider` while passing the Intents of success
   and fail. After successful authentication, 'onSuccessIntent' will be used to redirect the user,
   while 'onFailIntent' will be used for cancellations. The `login` method of `WebAuthProvider`
   performs a set of actions consisting of configuring the request and retrieving the authorization
   code via the browser, then exchanging the authorization code for the token.

<details open>
<summary>Using Kotlin</summary>

```kotlin
  (webAuthProvider as DefaultWebAuthProvider).login(
    context = this,
    onSuccessIntent = Intent(this, UserActivity::class.java),
    onFailIntent = Intent(this, MainActivity::class.java)
)
```

</details>

<details>
<summary>Using Java</summary>

```java
    webAuthProvider.login(this,
        new Intent(this,UserActivity.class)
        ,new Intent(this,MainActivity.class)
        );
```

</details>

4. As a way to access the token results or the exception thrown during the process, the following
   code snippet can be placed into the activities that were supplied with 'onSuccessIntent' or '
   onFailIntent' intents, respectively.

<details open>
<summary>Using Kotlin</summary>

```kotlin
val authorizationCodeFlowResults: AuthorizationCodeFlowResults? =
    AuthFlowResultHandler.getLoginResultFromIntent(intent)
```

</details>

<details>
<summary>Using Java</summary>

```java
 AuthorizationCodeFlowResults authorizationCodeFlowResults = AuthFlowResultHandler.getLoginResultFromIntent(getIntent());
```

</details>

#### Token refresh

To refresh the access token, we will call the `refreshAccessToken` method of `WebAuthProvider` while
passing the refresh token as shown below. This will return an AuthorizationCodeFlowResults object
which contains the token results or the exception thrown during the process.

<details open>
<summary>Using Kotlin</summary>

```kotlin
 val refreshedTokenACFR: AuthorizationCodeFlowResults =
    (webAuthProvider as DefaultWebAuthProvider).refreshAccessToken(
        context = this@UserActivity,
        refreshToken = refreshToken
    )
```

</details>

<details>
<summary>Using Java</summary>

```java
AuthorizationCodeFlowResults authorizationCodeFlowResults = webAuthProvider.refreshAccessToken(
        context,refreshToken
        );
```

</details>

#### Logout

To end the session we will call the `logout` method of `WebAuthProvider` while passing the IdToken
and an Intent to redirect the user on action completion.

<details open>
<summary>Using Kotlin</summary>

```kotlin
 webAuthProvider.logout(
    context = this,
    idToken = idToken,
    onCompleteIntent = Intent(this, UserActivity::class.java)
)
```

</details>

<details>
<summary>Using Java</summary>

```java
    webAuthProvider.logout(
        this,
        idToken,
        new Intent(this,UserActivity.class)
        );
```

</details>

Receive the results via the 'getLogoutResultFromIntent' method of `AuthFlowResultHandler`

<details open>
<summary>Using Kotlin</summary>

```kotlin
 val endSessionFlowResults: EndSessionFlowResults? =
    AuthFlowResultHandler.getLogoutResultFromIntent(
        intent = intent
    )
```

</details>

<details>
<summary>Using Java</summary>

```java
 EndSessionFlowResults endSessionFlowResults = AuthFlowResultHandler.getLogoutResultFromIntent(getIntent());
```

</details>

#### With client secrets (Not Recommended)

Although using static client secrets in native applications is _not recommended_, if you are unable
to avoid using them, you can use them as an HTTP basic authentication header or request body
parameter, as shown below.

* As a request body parameter

<details open>
<summary>Using Kotlin</summary>

```kotlin
    val clientPostSecretConfig: ClientSecretConfig = ClientPostSecretConfig(CLIENT_SECRET)

    val authConfiguration: AuthConfiguration =
        DefaultAuthConfiguration(context = this, clientSecretConfig = clientPostSecretConfig)
```

</details>

<details>
<summary>Using Java</summary>

```java
  ClientSecretConfig clientPostSecretConfig = new ClientPostSecretConfig(CLIENT_SECRET);

        AuthConfiguration authConfiguration = new DefaultAuthConfiguration(this
        ,clientPostSecretConfig);

```

</details>

* As an HTTP basic authorization header

<details open>
<summary>Using Kotlin</summary>

```kotlin
    val clientBasicSecretConfig: ClientSecretConfig = ClientBasicSecretConfig(CLIENT_SECRET)

   val authConfiguration: AuthConfiguration =
    DefaultAuthConfiguration(context = this, clientSecretConfig = clientBasicSecretConfig)
```

</details>

<details>
<summary>Using Java</summary>

```java
  ClientSecretConfig clientBasicSecretConfig = new ClientBasicSecretConfig(CLIENT_SECRET);

  AuthConfiguration authConfiguration = new DefaultAuthConfiguration(this
        ,clientBasicSecretConfig);

```

</details>

> Updating...
