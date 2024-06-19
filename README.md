# 커뮤니티 게시판 프로젝트
### 개요 
### [프로젝트 바로가기](http://spiaminto.ap-northeast-2.elasticbeanstalk.com/spiaboard/boards)     
___테스트용 ID: sample / PW: sample123___ , 자유롭게 이용 가능합니다.
위의 프로젝트 바로가기는 [이 프로젝트](https://github.com/spiamint/boardJpa) 를 배포한 링크 입니다.

학습과 병행하며 진행한 프로젝트입니다.
단순한 이론 학습 대신, 실제 커뮤니티 게시판의 기능을 직접 구현해봄으로써 더 깊은 학습에 대한 동기를 부여하고자 하였습니다.
기능을 작성할때는 사용자가 납득할 수 있는 동작을 만들어 내는것에 대해 고민했고, 작성 후에는 검증, 성능, 확장성 등을 고려하여 리팩토링도 진행해 보았습니다.

### 사용 기술 

* Spring(Boot), Gradle
* Mybatis
* MySql
* Thymeleaf
* HTML, CSS(BootStrap), JavaScript(Jquery)
* AWS ElasticBeanstalk, RDS, S3, CloudWatch

<br>

 __아래의 이미지들은 클릭하면 커집니다.__
<br>

## ERD

<img width="75%" alt="ERD" src="https://user-images.githubusercontent.com/122969954/231452459-45d5478e-a2d3-46b2-b34d-798e761cfe58.png">


## 기능 

### ㅇ 게시글 관리

<img width="48%" alt="APPWRITE" src="https://user-images.githubusercontent.com/122969954/230583252-4d7093e5-9dd6-426e-86b8-c0b1fd91c868.png"> <img width="48%" alt="APPHOME" src="https://user-images.githubusercontent.com/122969954/230583227-345c7165-5e56-453a-b832-200eb4f07bf0.png">

글 목록은 전체 글 목록, 내가 쓴 글 목록 을 카테고리 별로 확인 할 수 있으며, 페이징과 검색 기능도 구현하였습니다.  
글 쓰기에는 위지윅에디터(ckeditor5) 적용되어 있습니다.

  
### ㅇ 이미지 업로드
<img width="65%" alt="APPIMAGE" src="https://user-images.githubusercontent.com/122969954/230591439-e8c95f69-3304-4066-9c41-ea92ddc72cb0.png">

 ckeditor5 의 이미지 업로드 기능을 이용합니다.  
 업로드 요청된 이미지 파일을 Amazon S3 클라우드 스토리지에 업로드하고, 업로드한 이미지의 정보를 DB 에 저장합니다.  
 이미지 파일의 용량은 파일당 3MB 로 제한했으며, 5장을 초과하여 업로드 할 수 없도록 하였습니다.

### ㅇ 댓글 관리
<img width="48%" alt="APPCOMMENT" src="https://user-images.githubusercontent.com/122969954/230583219-462ba81b-6ccb-48db-9b00-2084885208fd.png"><img width="48%" alt="APPMYCOMMENT" src="https://user-images.githubusercontent.com/122969954/230873920-2fc7b354-955a-48a2-a990-6e90bb4753a7.png">

 ajax 를 이용해 댓글, 대댓글(댓글의 댓글) 기능을 구현했습니다.  
 대댓글은 대댓글의 대상이 되는 댓글의 닉네임이 표시되도록 하였습니다.  
 내가 쓴 댓글 목록을 확인 할 수 있으며, 댓글을 클릭하면 해당 댓글로 바로 이동합니다.

### ㅇ 회원 관리
<img width="48%" alt="ADDMEMBER_V26" src="https://user-images.githubusercontent.com/122969954/234501941-9aeef3ad-d735-41ad-88bc-521032a306f1.png"><img width="48%" alt="SMTP_GMAIL" src="https://user-images.githubusercontent.com/122969954/234500642-f844f37c-e21a-4e14-b7e2-9db6ffca8449.png">

SpringSecurity 라이브러리를 이용해 로그인 인증, 권한 인가 등을 구현합니다.  
필요한 경우 이메일 인증을 진행할 수 있으며, 구글 SMTP 서버를 이용해 인증 코드를 메일로 전송합니다.  
이메일 인증 정보를 이용하여 로그인 아이디를 찾거나, 비밀번호를 변경할 수 있습니다.

* __소셜회원__

<img width="48%" alt="LOGIN_V26" src="https://user-images.githubusercontent.com/122969954/234501949-24f1a707-5b80-4989-bc0e-bb9fabfb1911.png"><img width="48%" alt="APPOAUTHSIGNIN" src="https://user-images.githubusercontent.com/122969954/230873923-aad91c47-bed3-4560-90d8-233842dbb46c.png">

oauth2-client 라이브러리를 이용한 소셜계정 로그인/가입 기능을 포함 합니다.  
소셜가입은 각 서비스에서 사용정보 동의를 받으면, 해당 정보를 바탕으로 별도의 회원가입 폼으로 이동하여 가입을 진행합니다.

네이버 로그인 서비스는 검수를 받지 않으면 개발자 계정 외의 가입이 불가능하여, 현재는 사용할 수 없습니다. (개발자 본인의 계정으로 테스트는 하였습니다.)  
카카오 로그인 서비스는 검수를 받지 않으면 카카오 계정 제공을 필수로 설정할 수 없어, 주의 메시지를 띄우도록 하였습니다.

### ㅇ 유효성 검사
<img width="48%" alt="APPVAL2" src="https://user-images.githubusercontent.com/122969954/230583241-c68edb70-9672-4ab7-9388-c5cfe3fa89bb.png"><img width="48%" alt="APPVAL1" src="https://user-images.githubusercontent.com/122969954/230583240-e5706f93-273c-43a3-bc7c-7bbe184cf910.png">

 beanvalidation, 자바스크립트 등을 이용하여 글, 댓글, 회원정보 에 validation 이 적용되어있습니다.  
사용자가 의도하지 않은 데이터를 입력하는 것을 방지하고, 서버에서도 검증을 위해 노력했습니다.

### ㅇ 로깅
<img width="48%" alt="LOGGING_LOCAL" src="https://user-images.githubusercontent.com/122969954/234510091-c497c9bb-f37a-47b5-abf5-3745d785eaff.png"><img width="48%" alt="스크린샷 2023-04-10 183227" src="https://user-images.githubusercontent.com/122969954/230875569-12e1815f-c496-4a45-b646-4afc37fa887c.png">

 @Aspect 등 으로 로그를 찍고, logback 라이브러리를 이용하여 로그 출력을 커스텀 하고, 파일로 작성합니다.  
 파일은 전체로그, 에러로그, 요청요약 로그 등으로 세분화 하여 작성 해 보았습니다.  
 파일로 작성된 로그를 Amazon cloudwatch 로 실시간 스트리밍 합니다. 아래의 cloudwatch 사용 부분을 참고해 주세요.
  
## 이용한 서비스

### ㅇ Amazon Elastic Beanstalk
<img width="65%" alt="EBHOME" src="https://user-images.githubusercontent.com/122969954/230583282-53fe5ccd-bd6a-4c2b-aba6-b136b2e596a3.png">

 프로젝트를 배포 및 관리하기 위해 사용 했으며, EC2 인스턴스 연결을 통해 인스턴스에 접속할 수 있도록 하였습니다.   
EC2 인스턴스의 시간대 설정이나 로그 스트리밍 등을 위해 .ebextensions 구성 파일을 작성하였습니다.   
S3 버킷과 Amazon RDS 의 인스턴스에 연결하여 해당 리소스 를 사용합니다.    
tailLog, bundleLog 요청 시 기본 로그에 애플리케이션의 logback 에서 작성한 로그파일도 같이 보여주도록 설정하였습니다.


### ㅇ Amazon S3
<img width="65%" alt="S3STOREAGE" src="https://user-images.githubusercontent.com/122969954/230873925-d63186ff-fefe-4176-87c1-7643423c8c79.png">

 이미지 파일을 클라우드 스토리지에 업로드 하기 위해 사용했습니다.  
이미지는 각각 식별을 위해 데이터베이스에 저장되는 UUID 를 파일명으로 사용합니다.  
 로컬 테스트는 /upload_image_test , 배포된 애플리케이션 에서는 /upload_image 로 이미지 파일을 업로드 합니다.

### ㅇ Amazon RDS
<img width="54%" alt="MYSQLWORKBENCH" src="https://user-images.githubusercontent.com/122969954/234514859-a18c9359-a6bc-4a3a-ba6b-e9dba0524a57.png"><img width="41%" alt="RDSHOME" src="https://user-images.githubusercontent.com/122969954/230583295-843f4dcf-4ad8-4150-bd3c-2f328f3debad.png">

 로컬 DB 와 배포된 애플리케이션의 DB 를 분리하고, 온라인에서 DB 를 이용하기 위해 사용했습니다. MySql WorkBench 를 통해 연결하여 사용했습니다.


### ㅇ Amazon CloudWatch
<img width="65%" alt="CWLOG" src="https://user-images.githubusercontent.com/122969954/230583259-9e501d97-a926-4b83-bb6a-5f94cd4345f9.png">

 배포된 애플리케이션의 로그를 쉽게, 실시간으로 확인하기 위해 사용했습니다.  
.ebextensions 파일에 awslogs 로그 드라이버의 설정을 추가하여, 로그를 스트리밍 하도록 설정했습니다.

 
