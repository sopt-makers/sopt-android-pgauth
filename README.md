## What is Sopt-Android-pgauth
[국내 최대 규모의 대학생 연합 IT 벤처창업 동아리 SOPT](https://sopt.org/)에서 제작한 Android 공식 앱을 위한
[Playground](https://playground.sopt.org/) 로그인을 제공합니다.

현제는 Beta 사용 버전으로 실제 서버에 요청을 전달하지 않습니다.

## Setup
**Step1. Add JitPack repository to your build file**
Add in in your root build.gradle at the end of repositories:
```groovy
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

** Step2. Add the dependency**
```groovy
    dependencies {
	        implementation 'com.github.sopt-makers:sopt-android-pgauth:0.1.2'
	}
```

## How to use
### 인증 화면 요청
`Activity`에서 `PlaygroundAuth`의 `authorizeWithWebTab()`을 호출합니다.<br>
`autorizeWithWebTab()`을 호출하면 Sopt-Android-pgauth 에서 CustomTab 웹 브라우저를 실행하여
Playground 인증화면을 띄웁니다.
```kotlin
PlaygroundAuth.authorizeWithWebTab(this@MainActivity) { result ->
    result.onSuccess {
        authViewModel.completeLogin(it.accessToken)
    }.onFailure { exception -> }
}
```
호출 과정에서 웹 브라우저를 띄우고 상호작용하기 위한 Activity를 띄우기 위해 `context`가 반드시 필요합니다.
또한 호출 결과로 `Result<OauthToken>`을 반환합니다.<br>
<br>
사용 과정에서 필요하다면 `AuthStateGenerator` interface를 이용하여 Playground에 보낼 state code를 직접 생성하거나
조작할 수 있습니다.<br>

추후 인증 Redirect_URI와 API 주소가 변경될 경우
`authorizeWithWebTab()`에 `URI`와 `PlaygroundAuthDatasource` interface 를 사용하여 조작을 변경할 수 있습니다.<br>

### 인증 결과
위 코드에서 확인 가능하 듯 결과는 callback 에 `Result<OauthToken>` 을 담아 반환됩니다.<br>
해당 부분은 추후 수정을 통해 Result 만을 반환하거나 Coroutine 을 사용하여 개선할 예정입니다.

### 인증 과정 Error
인증 과정에서 발생하는 Error는 아래와 같이 정의합니다.
```kotlin
    object IllegalConnect : PlaygroundError("비정상적인 접근 방법입니다.")

    object NotFoundStateCode : PlaygroundError("state 인증 코드를 찾을 수 없습니다")

    object NetworkUnavailable : PlaygroundError("Network 에 연결할 수 없습니다.")

    object InconsistentStateCode : PlaygroundError("state 인증 코드가 일치하지 않습니다.")
```
- IllegalConnect : 비정상적인 링크를 사용하여 접근 한 경우 발생하며 현재 무조건 throw 하여 Exception 을 발생시킵니다.
- NotFoundStateCode : state 인증 코드가 전달되지 않은 경우 발생합니다.
- InconsistentStateCode : 전달한 state 인증 코드와 반환된 state 인증 코드가 일치하지 않은 경우 발생합니다.
- NetworkUnavailable : 네트워크 연결을 할 수 없는 경우 발생합니다.


