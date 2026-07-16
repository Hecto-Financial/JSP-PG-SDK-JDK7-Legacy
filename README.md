# JSP PG SDK - JDK 7 Legacy Sample

JDK 7 가맹점 환경에서 헥토파이낸셜 PG를 연동하기 위한 JSP 레거시 샘플 코드입니다.

이 저장소는 결제, 취소, 결과 수신 및 노티 처리 방법을 설명하는 **참고용 샘플**입니다.
인증·인가, 입력값 검증, 화면 출력 인코딩, CSRF 방어, 키 관리, 로그 마스킹 및
가맹점 내부 데이터 처리는 실제 운영 환경의 정책에 맞게 가맹점에서 구현해야 합니다.

## 제공 목적

기존 JDK 7 환경에서 발생하는 다음 호환성 문제를 해결합니다.

1. 상위 JDK로 컴파일된 `com.settle.pg` 클래스의 로딩 오류
   - 전체 클래스를 JDK 7 바이트코드인 `major version 51`로 제공합니다.
2. JCE의 AES 최대 키 길이가 128비트인 환경에서 발생하는 AES-256 오류
   - Bouncy Castle Lightweight API를 사용해 JCE policy 변경 없이 AES-256을 처리합니다.
3. PG 서비스의 TLS 1.2 이상 통신 요구사항
   - TLS 1.0과 1.1은 비활성화하고 실행 JVM이 지원하는 TLS 1.2 이상만 사용합니다.

## 샘플 사용 시 주의사항

> 이 저장소는 운영용 완성 모듈이 아닙니다.

- JDK 7과 Tomcat 7은 지원이 종료된 런타임입니다. 기존 가맹점의 불가피한 호환을
  위해서만 사용하고, 신규 구축에는 지원 중인 최신 LTS JDK와 WAS를 사용하십시오.
- 샘플 JSP를 인터넷에 그대로 노출하거나 운영 서비스에 그대로 배포하지 마십시오.
- 취소 및 자동결제 API에는 가맹점의 인증·권한·CSRF 보호를 적용하십시오.
- 테스트 MID와 테스트 키는 운영에 사용할 수 없습니다. 운영 키를 소스 저장소에
  커밋하지 말고 가맹점의 안전한 설정 관리 방식을 적용하십시오.
- 샘플에는 연동 확인을 위한 요청·응답 및 암호화 대상 값 로그가 포함되어 있습니다.
  운영 적용 전 개인정보와 결제정보를 제거하거나 마스킹하십시오.
- 결과 출력 JSP는 연동 결과를 확인하기 위한 샘플 화면입니다. 운영 화면에서는
  출력 위치에 맞는 HTML 및 JavaScript 인코딩을 적용하십시오.

## 지원 환경

- JDK 7 이상
- Apache Tomcat 7
- Servlet 3.0
- AES-256-ECB / PKCS7Padding
- 외부 API 통신: TLS 1.2 이상

이 저장소 전체가 JDK 7 레거시 샘플입니다. Bouncy Castle Lightweight API를 사용하므로
JCE가 AES 키를 128비트로 제한하는 서버에서도 policy 파일 변경 없이 AES-256을 처리합니다.
기존 `AES/ECB/PKCS5Padding` 결과와 호환되며 JSP 호출부는 변경하지 않았습니다.

## TLS 보안

`HttpClientUtil`은 TLS 1.2 이상만 활성화합니다. JDK 7에서는 TLS 1.2를 사용하고,
실행 JVM이 TLS 1.3을 지원하면 TLS 1.2와 1.3을 모두 허용합니다. TLS 1.0과 1.1은
활성화하지 않습니다. JRE 기본 TrustStore로 서버 인증서 체인을 검증하고
`HttpsURLConnection`의 기본 호스트명 검증을 유지합니다. 모든 인증서나 호스트명을
신뢰하는 우회 설정은 사용하지 않습니다.

실제 JDK 7에서 헥토파이낸셜 테스트 결제·취소 서버와 TLS 핸드셰이크를 확인했습니다.

```text
tbnpg.settlebank.co.kr protocol=TLSv1.2
tbgw.settlebank.co.kr protocol=TLSv1.2
cipher=TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
```

## JDK 7 검증 결과

실제 JDK 7의 제한 정책 환경에서 전체 Java 소스 컴파일과 암호화 호환성 테스트를 수행했습니다.
Tomcat 7.0.109의 Jasper 컴파일러로 전체 JSP 10개도 JDK 7 source/target 기준 사전 컴파일했습니다.

```text
java.version=1.7.0_352
AES max allowed key length=128
standard JCE AES-256 rejection confirmed: Illegal key size or default parameters
legacy ciphertext=z1NXwx+Muz+LTucnSi2lxjwgD3psQwUZ5/dz4mxeDYVqU0cW9bMLaSw9QELwXIEQ
ALL TESTS PASSED
TLS enabled protocols=TLSv1.2
TLS TEST PASSED
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
- 라이선스: [`LICENSES/Bouncy-Castle-LICENSE.txt`](LICENSES/Bouncy-Castle-LICENSE.txt)

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
│                  Tls12OrHigherSocketFactory.java <--- TLS 1.2 이상 통신 설정
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
        └─lib			<--- 의존성 JAR 패키지 위치
```

## 파일 설명

### 공통 페이지
- **index.html**: 인덱스 페이지입니다.
- **config.jsp**: 상점아이디, 암복호화키 등을 설정할 수 있는 설정 파일입니다.
- **receiveNoti.jsp**: 결제 또는 취소 처리가 완료된 후, 헥토파이낸셜에서 가맹점으로 전달하는 노티(결과통보)를 수신하는 페이지입니다.
- **processNoti.jsp**: receiveNoti.jsp에서 결제 또는 취소의 성공/실패에 따라 적절한 로직을 수행하는 메소드를 정의한 파일입니다.

### 결제 관련 페이지
- **pay_form.jsp**: 결제 요청 시 사용자로부터 정보를 입력받는 Form 페이지입니다. 결제는 Form POST 방식으로 처리됩니다.
- **pay_encryptParams.jsp**: pay_form.jsp에서 암호화가 필요한 파라미터들을 AJAX 통신으로 암호화하는 페이지입니다. 또한 SHA256 해시 처리도 수행합니다.
- **pay_receiveResult.jsp**: 결제창에서 결제가 완료된 이후 닫기 버튼을 누를 때, 헥토파이낸셜로부터 응답 파라미터를 수신하는 페이지입니다.
- **pay_showResult.jsp**: pay_receiveResult.jsp에서 받은 파라미터를 부모창으로 전송할 수 있는데, 이때 전송된 파라미터들을 수신하여 출력하는 페이지입니다.
- **pay_autoPayResult.jsp**: 휴대폰 자동연장결제 시 사용되는 결제 및 결과화면 페이지입니다.

### 취소 관련 페이지
- **cancel_form.jsp**: 취소 요청 시 사용자로부터 정보를 입력받는 Form 페이지입니다.
- **cancel_showResult.jsp**: 헥토파이낸셜과 Server to Server로 커넥션하여, 취소 요청을 하고 응답을 받아 결과를 출력하는 페이지입니다.

## 프로세스 처리 순서

- **결제 처리 순서**: pay_form.jsp → pay_encryptParams.jsp → pay_receiveResult.jsp → pay_showResult.jsp
- **휴대폰 자동연장 결제**: pay_form.jsp → pay_autoPayResult.jsp
- **취소 처리 순서**: cancel_form.jsp → cancel_showResult.jsp
- **노티 처리 순서**: receiveNoti.jsp → processNoti.jsp

## config.jsp 설정 항목

- **PG_MID**: 테스트 시 샘플에 기재된 테스트 MID를 사용합니다. 운영 시에는 헥토파이낸셜에서 발급한 가맹점 MID로 교체합니다.
- **LICENSE_KEY**: SHA-256 해시 검증에 사용하는 MID별 라이선스 키입니다. 운영 키는 외부에 노출하거나 저장소에 커밋하지 마십시오.
- **AES256_KEY**: 개인정보 및 민감정보의 AES-256 암·복호화 키입니다. 운영 키는 외부에 노출하거나 저장소에 커밋하지 마십시오.
- **PAYMENT_SERVER**: 헥토파이낸셜 결제 처리 서버의 URL입니다. 변경하지 마십시오.
- **CANCEL_SERVER**: 헥토파이낸셜 취소 처리 서버의 URL입니다. 변경하지 마십시오.
- **CONN_TIMEOUT**: 헥토파이낸셜 API 통신 연결 타임아웃입니다.
- **READ_TIMEOUT**: 헥토파이낸셜 API 통신 수신 타임아웃입니다.

## 노티 수신 페이지

- **파일명**: receiveNoti.jsp
- 결제 또는 취소 완료 후 헥토파이낸셜 서버에서 콜백으로 호출하게 되는 페이지이며, 헥토파이낸셜에서 가맹점으로 노티를 전송합니다.
- `nextUrl`에서는 고객에게 결제 성공 또는 실패 결과 화면을 반환합니다.
- `notiUrl`에서는 해시 검증이 성공한 요청에 한해 가맹점 내부 데이터와 DB를 처리합니다.
- 중복 노티에 대비한 멱등성 처리는 가맹점 시스템에서 구현해야 합니다.
