# FCM 알림 기능 설정 가이드

## Firebase 설정

### 1. Firebase 프로젝트 생성
1. [Firebase Console](https://console.firebase.google.com/)에 접속
2. "프로젝트 추가" 클릭
3. 프로젝트 이름 입력 (예: MyKKU)
4. Google Analytics 설정 (선택사항)
5. 프로젝트 생성 완료

### 2. Android 앱 추가
1. 프로젝트 개요에서 "Android 앱에 Firebase 추가" 클릭
2. Android 패키지 이름 입력
3. 앱 닉네임 입력 (선택사항)
4. SHA-1 인증서 추가 (선택사항)
5. `google-services.json` 다운로드
6. 안드로이드 프로젝트에 파일 추가

### 3. iOS 앱 추가
1. 프로젝트 개요에서 "iOS 앱에 Firebase 추가" 클릭
2. iOS 번들 ID 입력
3. 앱 닉네임 입력 (선택사항)
4. `GoogleService-Info.plist` 다운로드
5. iOS 프로젝트에 파일 추가

### 4. 서비스 계정 키 생성
1. Firebase Console에서 프로젝트 설정 > 서비스 계정 탭으로 이동
2. "새 비공개 키 생성" 클릭
3. JSON 파일 다운로드
4. 서버에 안전하게 보관

## 백엔드 설정

### 1. 환경 변수 설정

`application.yml` 또는 환경 변수에 다음을 추가:

```yaml
firebase:
  service-account-key-path: /path/to/your/firebase-service-account-key.json
```

또는 환경 변수:
```bash
export FIREBASE_SERVICE_ACCOUNT_KEY_PATH=/path/to/your/firebase-service-account-key.json
```

### 2. 데이터베이스 마이그레이션

Flyway가 자동으로 다음 테이블을 생성합니다:
- `notification`: 알림 기록
- `fcm_token`: FCM 토큰 관리
- `notification_setting`: 알림 설정

## API 엔드포인트

### FCM 토큰 관리

#### 토큰 등록/갱신
```
POST /api/v1/fcm-tokens
Content-Type: application/json

{
  "token": "FCM_TOKEN_STRING",
  "deviceId": "DEVICE_UNIQUE_ID",
  "deviceType": "ANDROID" // or "IOS"
}
```

#### 토큰 목록 조회
```
GET /api/v1/fcm-tokens
```

#### 토큰 삭제
```
DELETE /api/v1/fcm-tokens/{deviceId}
```

### 알림 조회

#### 알림 목록 조회
```
GET /api/v1/notifications?page=0&size=20
```

#### 읽지 않은 알림 조회
```
GET /api/v1/notifications/unread?page=0&size=20
```

#### 읽지 않은 알림 개수
```
GET /api/v1/notifications/unread/count
```

#### 알림 읽음 처리
```
PATCH /api/v1/notifications/{notificationId}/read
```

#### 모든 알림 읽음 처리
```
PATCH /api/v1/notifications/read-all
```

#### 알림 삭제
```
DELETE /api/v1/notifications/{notificationId}
```

### 알림 설정

#### 알림 설정 조회
```
GET /api/v1/notification-settings
```

#### 알림 설정 변경
```
PATCH /api/v1/notification-settings
Content-Type: application/json

{
  "notificationType": "FEED_LIKE", // FEED_LIKE, FEED_COMMENT, FOLLOW, FOLLOWING_POST, SYSTEM_NOTICE
  "isEnabled": true
}
```

## 알림 타입

- `FEED_LIKE`: 피드 좋아요
- `FEED_COMMENT`: 피드 댓글
- `FOLLOW`: 팔로우
- `FOLLOWING_POST`: 팔로잉한 사람의 게시글
- `SYSTEM_NOTICE`: 시스템 공지

## 이벤트 기반 알림 발송

다음 이벤트가 발생하면 자동으로 알림이 발송됩니다:

1. **피드 좋아요** (`FeedLikedEvent`)
   - 누군가 내 피드에 좋아요를 누르면 알림 발송
   - 자기 자신의 피드는 제외

2. **피드 댓글** (`FeedCommentedEvent`)
   - 누군가 내 피드에 댓글을 남기면 알림 발송
   - 자기 자신의 댓글은 제외

3. **팔로우** (`FollowedEvent`)
   - 누군가 나를 팔로우하면 알림 발송

4. **팔로잉 게시글** (`FeedCreatedByFollowingEvent`)
   - 내가 팔로우하는 사람이 게시글을 작성하면 알림 발송
   - 모든 팔로워에게 알림 발송

## 모바일 앱 통합

### Android (Kotlin)

#### 1. Dependencies 추가
```kotlin
// build.gradle (Project level)
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}

// build.gradle (App level)
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
}
```

#### 2. FCM 토큰 등록
```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        val token = task.result
        // 백엔드에 토큰 등록
        registerTokenToBackend(token)
    }
}

fun registerTokenToBackend(token: String) {
    val request = RegisterFcmTokenRequest(
        token = token,
        deviceId = getDeviceId(), // UUID or Android ID
        deviceType = "ANDROID"
    )
    // Retrofit 등을 사용하여 POST /api/v1/fcm-tokens
}
```

#### 3. 푸시 알림 수신
```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 알림 표시
        showNotification(remoteMessage)
    }

    override fun onNewToken(token: String) {
        // 새 토큰 발급 시 백엔드에 갱신
        registerTokenToBackend(token)
    }
}
```

### iOS (Swift)

#### 1. Firebase SDK 설치
```swift
// Package.swift or Podfile
dependencies: [
    .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "10.0.0")
]
```

#### 2. FCM 토큰 등록
```swift
import FirebaseMessaging

Messaging.messaging().token { token, error in
    if let error = error {
        print("Error fetching FCM token: \(error)")
    } else if let token = token {
        // 백엔드에 토큰 등록
        registerTokenToBackend(token: token)
    }
}

func registerTokenToBackend(token: String) {
    let request = RegisterFcmTokenRequest(
        token: token,
        deviceId: UIDevice.current.identifierForVendor?.uuidString ?? "",
        deviceType: "IOS"
    )
    // Alamofire 등을 사용하여 POST /api/v1/fcm-tokens
}
```

#### 3. 푸시 알림 수신
```swift
extension AppDelegate: UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification
    ) async -> UNNotificationPresentationOptions {
        return [.banner, .sound, .badge]
    }
}
```

## 주의사항

1. **Firebase 서비스 계정 키 보안**
   - 절대 Git에 커밋하지 마세요
   - .gitignore에 추가하세요
   - 환경 변수나 Secret Manager를 사용하세요

2. **토큰 갱신**
   - FCM 토큰은 만료될 수 있습니다
   - 앱 시작 시마다 토큰을 확인하고 갱신하세요

3. **알림 권한**
   - 모바일 앱에서 알림 권한을 요청하세요
   - iOS: Info.plist에 권한 설명 추가
   - Android: AndroidManifest.xml에 권한 추가

4. **다중 디바이스 지원**
   - 한 사용자가 여러 디바이스에서 로그인 가능
   - 모든 디바이스에 알림이 발송됩니다

## 문제 해결

### FCM 토큰이 등록되지 않음
- Firebase Console에서 프로젝트 설정 확인
- 서비스 계정 키 경로 확인
- 네트워크 연결 확인

### 알림이 발송되지 않음
- 알림 설정이 활성화되어 있는지 확인
- FCM 토큰이 유효한지 확인
- Firebase Console에서 메시지 로그 확인

### 알림이 중복 발송됨
- 같은 디바이스에 대해 토큰이 중복 등록되었는지 확인
- deviceId가 고유한지 확인
