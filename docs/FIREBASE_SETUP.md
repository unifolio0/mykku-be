# Firebase Cloud Messaging 설정 가이드

## 개요

MyKKU 백엔드는 모바일 푸시 알림을 위해 Firebase Cloud Messaging (FCM)을 사용합니다.

## 1. Firebase 프로젝트 생성

1. [Firebase Console](https://console.firebase.google.com/)에 접속
2. "프로젝트 추가" 클릭
3. 프로젝트 이름 입력 (예: mykku-dev)
4. Google Analytics 설정 (선택사항)
5. 프로젝트 생성 완료

## 2. Android/iOS 앱 추가

### Android 앱 추가
1. Firebase 프로젝트 > 프로젝트 설정
2. "앱 추가" > Android 선택
3. Android 패키지 이름 입력
4. google-services.json 다운로드 → 모바일 팀에 전달

### iOS 앱 추가
1. Firebase 프로젝트 > 프로젝트 설정
2. "앱 추가" > iOS 선택
3. iOS 번들 ID 입력
4. GoogleService-Info.plist 다운로드 → 모바일 팀에 전달

## 3. 서비스 계정 키 발급

1. Firebase Console > 프로젝트 설정 > 서비스 계정
2. "새 비공개 키 생성" 클릭
3. JSON 키 파일 다운로드
4. 파일명을 `service-account-key-dev.json`으로 변경

## 4. 백엔드 프로젝트 설정

### 4.1 키 파일 위치

프로젝트 루트에서 다음 디렉토리 구조를 생성:

```
mykku-be/
├── config/
│   └── firebase/
│       └── service-account-key-dev.json  ← 여기에 키 파일 저장
```

### 4.2 디렉토리 생성

```bash
mkdir -p config/firebase
mv ~/Downloads/service-account-key-dev.json config/firebase/
```

### 4.3 환경 변수 설정 (선택)

환경 변수로 경로를 오버라이드할 수 있습니다:

```bash
export FIREBASE_SERVICE_ACCOUNT_KEY_PATH=/path/to/your/key.json
```

## 5. 운영 환경 설정

### dev-secret.yml 추가

`src/main/resources/dev-secret.yml` 파일에 다음 추가:

```yaml
firebase:
  service-account-key-path: config/firebase/service-account-key-dev.json
```

## 6. 설정 검증

### 6.1 애플리케이션 실행

```bash
./gradlew bootRun
```

### 6.2 로그 확인

애플리케이션 시작 시 다음과 같은 로그가 출력되어야 합니다:

```
Successfully initialized Firebase app: [DEFAULT]
```

오류 발생 시:
- 키 파일 경로 확인
- 키 파일 권한 확인 (읽기 가능 여부)
- 키 파일 형식 확인 (유효한 JSON)

## 7. 보안 주의사항

### ⚠️ 절대 Git에 커밋하지 마세요

`.gitignore`에 이미 다음이 포함되어 있습니다:

```
config/firebase/**/*.json
```

### 키 파일 노출 시 대응

1. Firebase Console > 서비스 계정
2. 노출된 키 삭제
3. 새 키 생성 및 재배포

## 8. FCM 토큰 테스트

### 8.1 토큰 등록

```bash
curl -X POST http://localhost:8080/api/v1/fcm-tokens \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_FCM_TOKEN",
    "deviceId": "test-device-001",
    "deviceType": "ANDROID"
  }'
```

### 8.2 테스트 알림 발송

모바일 앱에서 FCM 토큰을 등록한 후:
1. 피드에 좋아요 추가
2. 댓글 작성
3. 다른 사용자 팔로우

해당 이벤트에 대한 푸시 알림이 모바일 기기로 전송됩니다.

## 9. 문제 해결

### Firebase 초기화 실패

**오류**: `FileNotFoundException: config/firebase/service-account-key-dev.json`

**해결**:
- 키 파일이 올바른 위치에 있는지 확인
- 상대 경로가 프로젝트 루트 기준인지 확인

### 알림 전송 실패

**오류**: `FirebaseMessagingException: Invalid registration token`

**해결**:
- FCM 토큰이 유효한지 확인
- 모바일 앱에서 최신 토큰 재등록
- 토큰이 올바른 Firebase 프로젝트와 연결되었는지 확인

### 권한 오류

**오류**: `Permission denied`

**해결**:
```bash
chmod 600 config/firebase/service-account-key-dev.json
```

## 10. 참고 자료

- [Firebase Admin SDK 설정](https://firebase.google.com/docs/admin/setup)
- [FCM 서버 설정](https://firebase.google.com/docs/cloud-messaging/server)
- [FCM 메시지 전송](https://firebase.google.com/docs/cloud-messaging/send-message)
