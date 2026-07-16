# JSP-PG-SDK-JDK7-Legacy

JDK 7 가맹점 환경을 위한 헥토파이낸셜 PG 연동 JSP 샘플 코드입니다.

## 지원 환경

- JDK 7 이상
- Apache Tomcat 7
- Servlet 3.0
- AES-256-ECB / PKCS7Padding

이 저장소 전체가 JDK 7 레거시 배포본입니다. Bouncy Castle lightweight API를 사용하므로
JCE가 AES 키를 128비트로 제한하는 서버에서도 policy 파일 변경 없이 AES-256을 처리합니다.
기존 `AES/ECB/PKCS5Padding` 결과와 호환되며 JSP 호출부는 변경하지 않았습니다.

## JDK 7 검증 결과

실제 JDK 7의 제한 정책 환경에서 전체 Java 소스 컴파일과 암호화 호환성 테스트를 수행했습니다.
Tomcat 7.0.109의 Jasper 컴파일러로 전체 JSP 10개도 JDK 7 source/target 기준 사전 컴파일했습니다.

```text
java.version=1.7.0_352
AES max allowed key length=128
standard JCE AES-256 rejection confirmed: Illegal key size or default parameters
legacy ciphertext=z1NXwx+Muz+LTucnSi2lxjwgD3psQwUZ5/dz4mxeDYVqU0cW9bMLaSw9QELwXIEQ
ALL TESTS PASSED
JSP compile errors=0
```

저장소에 포함된 클래스 파일도 JDK 7 클래스 버전인 `major 51`로 컴파일했습니다.

## 테스트 실행

실제 JDK 7 경로를 `JAVA_HOME`으로 지정한 후 실행합니다.

```sh
JAVA_HOME=/path/to/jdk7 ./run-jdk7-tests.sh
```

## 암호화 의존성

- `bcprov-jdk15to18-1.84.jar`
- 배포 위치: `WebContent/WEB-INF/lib`
- SHA-256: `a30777eebbd44aa1713c14ee04547de1df63ef3e383e910ed210e1d0f2c2ef92`
- Bouncy Castle 공식 지원 범위: JDK 1.5~1.8

## 파일 구조

```
/(Project Root Directory)
│  .classpath		<--- eclipse 프로젝트 정보
│  .project		<--- eclipse 프로젝트 정보
├─.settings		<--- eclipse 프로젝트 정보
├─src
│  │  logback.xml		<--- logback 설정파일(*자사에 맞게 변경 필요)
│  │
│  └─com
│      └─settle
│          └─pg
│                  EncryptUtil.java	<--- 암호화 유틸
│                  HttpClientUtil.java	<--- HTTP 커넥션 유틸
│                  StringUtil.java	<--- 문자열 유틸
│
└─WebContent
     |   
    │  index.html			<--- index페이지
    │  config.jsp			<--- 기본정보 설정파일(*자사에 맞게 변경 필요)
     |   
    │  pay_form.jsp		<--- 결제시 메인 폼
    │  pay_encryptParams.jsp	<--- 결제시 파라미터 암호화 및 해쉬 처리 페이지
     |   pay_autoPayResult.jsp		<--- 휴대폰 자동연장결제시 사용되는 페이지
    │  pay_receiveResult.jsp		<--- 결제 완료 후 응답파라미터 수신페이지
    │  pay_showResult.jsp		<--- 자식페이지에서 전달된 응답파라미터 출력
     |   
    │  cancel_form.jsp		<--- 취소 메인 폼
    │  cancel_showResult.jsp		<--- 취소 처리 및 결과 화면
     |   
    │  receiveNoti.jsp		<--- 결제 완료 후 노티 수신 페이지
    │  processNoti.jsp		<--- 노티 수신 후 처리하는 페이지
     |   
    └─WEB-INF
        └─lib			<--- 의종성 jar패키지 위치
```

## 📄 파일 설명

### 🔧 공통 페이지
- **index.html**: 인덱스 페이지입니다.
- **config.jsp**: 상점아이디, 암복호화키 등을 설정할 수 있는 설정 파일입니다.
- **receiveNoti.jsp**: 결제 또는 취소 처리가 완료된 후, 헥토파이낸셜에서 가맹점으로 전달하는 노티(결과통보)를 수신하는 페이지입니다.
- **processNoti.jsp**: receiveNoti.jsp에서 결제 또는 취소의 성공/실패에 따라 적절한 로직을 수행하는 메소드를 정의한 파일입니다.

### 💳 결제 관련 페이지
- **pay_form.jsp**: 결제 요청 시 사용자로부터 정보를 입력받는 Form 페이지입니다. 결제는 Form POST 방식으로 처리됩니다.
- **pay_encryptParams.jsp**: pay_form.jsp에서 암호화가 필요한 파라미터들을 AJAX 통신으로 암호화하는 페이지입니다. 또한 SHA256 해시 처리도 수행합니다.
- **pay_receiveResult.jsp**: 결제창에서 결제가 완료된 이후 닫기 버튼을 누를 때, 헥토파이낸셜로부터 응답 파라미터를 수신하는 페이지입니다.
- **pay_showResult.jsp**: pay_receiveResult.jsp에서 받은 파라미터를 부모창으로 전송할 수 있는데, 이때 전송된 파라미터들을 수신하여 출력하는 페이지입니다.
- **pay_autoPayResult.jsp**: 휴대폰 자동연장결제 시 사용되는 결제 및 결과화면 페이지입니다.

### ❌ 취소 관련 페이지
- **cancel_form.jsp**: 취소 요청 시 사용자로부터 정보를 입력받는 Form 페이지입니다.
- **cancel_showResult.jsp**: 헥토파이낸셜과 Server to Server로 커넥션하여, 취소 요청을 하고 응답을 받아 결과를 출력하는 페이지입니다.

## 🔄 프로세스 처리 순서

- **결제 처리 순서**: pay_form.jsp → pay_encryptParams.jsp → pay_receiveResult.jsp → pay_showResult.jsp
- **휴대폰 자동연장 결제**: pay_form.jsp → pay_autoPayResult.jsp
- **취소 처리 순서**: cancel_form.jsp → cancel_showResult.jsp
- **노티 처리 순서**: receiveNoti.jsp → processNoti.jsp

## ⚙️ config.jsp 설정 파일 변수 설명

- **PG_MID**: 상점아이디. 테스트환경에서의 상점아이디는 샘플소스에 기재되어 있습니다. 상용테스트 시에는 헥토파이낸셜에서 발급한 MID로 설정하셔야 합니다. 이 값은 외부에 노출되어서는 안됩니다.
- **LICENSE_KEY**: MID당 하나의 라이센스키가 발급됩니다. SHA256 해시체크 용도로 사용됩니다. 이 값은 외부에 노출되어서는 안됩니다.
- **AES256_KEY**: 개인정보/민감정보를 암복호화하는데 사용되는 키로서, 외부에 노출되어서는 안됩니다.
- **PAYMENT_SERVER**: 헥토파이낸셜 결제 처리 서버의 URL입니다. 변경하지 마십시오.
- **CANCEL_SERVER**: 헥토파이낸셜 취소 처리 서버의 URL입니다. 변경하지 마십시오.
- **CONN_TIMEOUT**: 헥토파이낸셜 API 통신 연결 타임아웃입니다.
- **READ_TIMEOUT**: 헥토파이낸셜 API 통신 수신 타임아웃입니다.

## 📢 노티 수신 페이지

- **파일명**: receiveNoti.jsp
- 결제 또는 취소 완료 후 헥토파이낸셜 서버에서 콜백으로 호출하게 되는 페이지이며, 헥토파이낸셜에서 가맹점으로 노티를 전송합니다.
- nextUrl(결과페이지)에서는 성공/실패에 대한 결과 화면을 고객에게 리턴하여 주시고,
- notiUrl(노티수신페이지)에서는 가맹점의 실제 내부데이터, DB를 처리하시면 됩니다.
