# Cart-RestAPI-Webserver
- Docker와 AWS EC2를 사용해 배포는 완료했지만, 수정할 부분과 개선점은 계속해서 수정하도록 하겠습니다.
### 배포 및 인프라

- `AWS EC2(Ubuntu)` `AWS RDS` `Docker`

### 백엔드

- `SpringBoot 3.1.6` `JAVA 17` `MyBatis 3.0.3`

### 데이터베이스

- `Mysql`

### 빌드

- `Gradle`

### 테스트

- `Postman`

# 주요 구현 내용
### 배포 절차

1. **로컬에서 Docker 파일 및 프로젝트 JAR 파일을 EC2로 복사**: Docker 파일과 애플리케이션의 실행에 필요한 JAR 파일을 EC2 인스턴스로 전송하여 배포용 환경을 마련했습니다.
2. **EC2에서 Docker 설치**: EC2 인스턴스에 Docker를 설치하여 도커 엔진을 사용할 수 있도록 했습니다.
3. **Docker 이미지 빌드**: Docker 파일을 기반으로 도커 이미지를 빌드하여 애플리케이션을 실행할 수 있는 이미지를 생성했습니다.
4. **Docker 컨테이너 생성 및 실행**: 도커 컨테이너를 생성하고, 해당 컨테이너에서 생성한 이미지를 -d 옵션을 통해 백그라운드에서 실행시켰습니다.
---
### 예외 처리

사용자 정의 예외, 유효성 검사 실패 등 다양한 예외를 통합하여 다루기 위해 `CustomResponseEntityExceptionHandler` 클래스로 정의해 처리했습니다.

| 메서드명 | 역할 | 상태 코드 |
| --- | --- | --- |
| allExceptions | 사용자 정의 예외 클래스 이외의 예외를 처리합니다. | 500(INTERNAL SERVER ERROR) 상태 코드를 반환합니다. |
| usersRegisterExceptions | 회원가입 시 ID,EMAIL에 대해 이미 존재하는 리소스들을 처리합니다. | 409(CONFLICT) 상태 코드를 반환합니다. |
| userNotFoundExceptions | 찾을 수 없는 회원을 처리합니다. | 404(NOT FOUND) 상태 코드를 반환합니다. |
| productNotFoundExceptions | 찾을 수 없는 상품을 처리합니다. | 404(NOT FOUND) 상태 코드를 반환합니다. |
| pageNotFoundExceptions | 찾을 수 없는 페이지를 처리합니다. | 404(NOT FOUND) 상태 코드를 반환합니다. |
| productQuantityExceptions | 상품 수량이 부족할 때 처리합니다. | 404(NOT FOUND) 상태 코드를 반환합니다. |
| handleMethodArgumentNotValid | 사용자의 입력 데이터에 대한 유효성 검증 후 에러를 처리합니다. | 400(BAD REQUEST) 상태 코드를 반환합니다. |

---
### HATEOAS

현재 리소스와 연관된 호출 가능한 정보를 제공하기 위해 데이터와 URL을 함께 담아 반환하도록 구현했습니다.

- 응답 예시

```json
{
    "productNum": 0,
    "productNo": 1,
    "productName": "짜파게티",
    "productPrice": 1500,
    "productQuantity": 780,
    "productContent": "몸에 좋고 맛도 좋은 짜파게티",
    "_links": {
        "Prev-By-Product-List": {
            "href": "http://13.124.47.242:8084/products?searchType=productName&searchContent=%EC%A7%9C%ED%8C%8C&page=1"
        },
        "Create-Carts": {
            "href": "http://13.124.47.242:8084/carts"
        }
    }
}
```
---
### 쿼리 파라미터 설정

상품 목록, 장바구니 목록, 구매 목록 요청시 쿼리 파라미터에 따른 페이징 처리된 URL을 함께 담아 반환하기 위해서 `CreateLinkService` 클래스를 정의했습니다.

- `createPaginationLinks` 메서드 내부에선 현재 컨트롤러가 `ProductsController`와 일치하는지 확인하는 조건문을 통해 각 컨트롤러에 맞게 쿼리 파라미터를 설정합니다.

```java

public List<Link> createPaginationLinks(Class<?> controllerClass, PageInfo pageInfo) {
//생략..
	UriComponentsBuilder builder = null;

		if (controllerClass.getName().equals(ProductsController.class.getName())){
		    // 상품 목록 페이징 처리를 위한 쿼리 파라미터 세팅
		    builder = ServletUriComponentsBuilder.fromCurrentRequest()
							.replaceQueryParam("searchType", pageInfo.getSearchType())
							.replaceQueryParam("searchContent", pageInfo.getSearchContent())
							.replaceQueryParam("page", pageInfo.getStartPage());
		}else {
		    // 장바구니 목록 페이징, 구매 목록 페이징 처리를 위한 쿼리 파라미터 세팅
		    builder = ServletUriComponentsBuilder.fromCurrentRequest()
							.replaceQueryParam("page", pageInfo.getStartPage());
		}

//생략..
}
```
---
### 비밀번호 암호화

사용자의 비밀번호를 암호화 하기 위해서 `Jasypt` 라이브러리를 활용하여 `EncryptPasswordService` 클래스를 정의했습니다.

- `encryptPassword` 메서드는 사용자가 제공한 비밀번호를 암호화하기 위해 `BasicPasswordEncryptor` 객체를 활용합니다. 
이 과정에서 사용자가 입력한 비밀번호는 암호화되고, 이 암호화된 값이 데이터베이스에 저장됩니다.
- `checkPassword` 메서드는 로그인 프로세스에서 사용자가 입력한 비밀번호와 데이터베이스에 저장된 비밀번호를 비교하는데 활용됩니다. 
이를 위해 `BasicPasswordEncryptor` 객체의 `checkPassword` 메서드가 사용되며, 이 메서드는 입력된 비밀번호와 저장된 비밀번호가 일치하는지를 검증합니다.
---
### 세션 활용

해당 서버는 로그인에서 세션을 활용하여 사용자의 생성 번호를 저장했습니다. 
또한, 세션에 저장된 사용자 생성 번호의 유무에 따라 다음과 같은 접근 권한을 가지도록 설계했습니다.

- **비로그인 상태**: 회원가입, 로그인, 상품 목록 리스트 및 검색에 접근할 수 있습니다. 이 상태에서는 로그인이 필요하지 않으며, 제한된 서비스를 사용할 수 있습니다.
- **로그인 상태**: 로그인 후에는 모든 경로에 자유롭게 접근할 수 있습니다. 로그인한 사용자는 모든 서비스와 기능에 접근할 권한을 가지게 됩니다.
---
### 유효성 검사

Spring Validation을 활용해 사용자의 입력 데이터의 유효성 검사를 수행하고 검사 결과를 처리했습니다.

- 적용된 기능 : 회원가입, 로그인, 쇼핑 목록 상품 추가, 장바구니 담기
---
# API 명세서
- `Cart-API-Document.postman_collection.json` 파일은 Postman Collection JSON 파일 형태로 저장된 API 명세서입니다.
    - https://github.com/leejh-96/cart-restful-api/blob/main/Cart-API-Document.postman_collection.json
        - 위의 경로에서 이 파일을 다운로드하여 Postman에서 Import하여 사용할 수 있습니다.
- 아래의 링크를 통해서도 API 명세서를 보실 수 있습니다.
    - https://documenter.getpostman.com/view/28000436/2s9YkraKHx
### 서버 주소
- http://13.124.47.242:8084
### 테스트 계정 정보
- 아이디 : outlier1 , 비밀번호 : outlier1
## `Users`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 회원가입 | POST | http://13.124.47.242:8084/users |

**HTTP Body** **Example Request**

```json
{
    "userId": "outlier1",            
    "userPassword": "outlier1",      
    "userName": "홍길동",             
    "userEmail": "outlier1@abc.com" 
}
```

**Response Http Status Code**

- 새로운 User가 생성되면 201(CREATED) HTTP Status Code 를 반환합니다.

---

## `Users`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 로그인 | POST | http://13.124.47.242:8084/users/login |

**HTTP Body** **Example Request**

```json
{
    "userId": "outlier1", 
    "userPassword": "outlier1" 
}
```

**Response Http Status Code**

- 로그인이 완료되면 200(OK) HTTP Status Code 를 반환합니다.

---

## `Users`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 로그아웃 | POST | http://13.124.47.242:8084/users/logout |

**HTTP Body** **Example Request**

- 로그아웃은 HTTP Body에 별도의 전달 데이터가 없으며, 세션에서 유저 정보를 확인한 후 세션을 삭제합니다.

**Response Http Status Code**

- 로그아웃이 완료되면 200(OK) HTTP Status Code 를 반환합니다.

---

## `Products`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 쇼핑 목록 추가 | POST | http://13.124.47.242:8084/products |

**HTTP Body** **Example Request**

```json
{
		// 상품 이름 : 최대 33까지 허용되며, null이나 빈 문자열은 허용하지 않습니다.
    "productName": "짜파게티", 
		// 상품 가격 : 최소 1,000원 ~ 최대 100,000원까지 허용됩니다.
    "productPrice": 1500, 
		// 상품 수량 : 최소 10개 ~ 최대 1,000개까지 허용됩니다.
    "productQuantity": 800, 
		// 상품 설명 : 최대 100자까지 허용되며, null이나 빈 문자열은 허용하지 않습니다.
    "productContent": "몸에 좋고 맛도 좋은 짜파게티" 
}
```

**Response Http Status Code**

- 새로운 Product가 생성되면 201(CREATED) HTTP Status Code 를 반환합니다.

---

## `Products`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 쇼핑 목록 리스트 및 검색 | GET | http://13.124.47.242:8084/products |

**Parameters for Product Search**

| Parameter | Description | Example |
| --- | --- | --- |
| searchType | productName , productPrice , productContent 중 하나의 카테고리 선택이 가능하며, 카테고리를 선택하지 않아도 검색이 가능합니다. | productName |
| searchContent | 검색하고자 하는 내용은 생략 가능하며, 검색하고자 하는 내용을 작성할 수 있습니다. | 짜파 |
| page | 페이지는 생략이 가능하며, 생략할 경우 기본값으로 1페이지가 검색됩니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/products?searchType=productName&searchContent=짜파&page=1
```

**Response Http Status Code**

- Products 목록 리스트와 검색을 완료하면 200(OK) HTTP Status Code 를 반환합니다.

---

## `Products`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 상품 상세 보기 | GET | http://13.124.47.242:8084/products/{productNo} |

**Parameters for Product Detail**

| Parameter | Description | Example |
| --- | --- | --- |
| {productNo} | 상품을 상세 보기 하고자 한다면, 상품 번호가 필요합니다. | 1 |
| searchType | 이전의(상품 목록 리스트 및 검색)에서 검색한 검색 카테고리 타입입니다. | productName |
| searchContent | 이전의(상품 목록 리스트 및 검색)에서 검색한 검색 내용입니다. | 짜파 |
| page | 이전의(상품 목록 리스트 및 검색)에서 검색한 검색타입,검색내용에 대한 페이지 정보 번호입니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/products/1?searchType=productName&searchContent=짜파&page=1
```

**Response Http Status Code**

- Product 상세 정보와 200(OK) HTTP Status Code 를 반환합니다.

---

## `Carts`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 담기 | POST | http://13.124.47.242:8084/carts |

**HTTP Body** **Example Request**

```json
{
		//장바구니에 담을 상품 번호
    "productNo": 1, 
		//해당 상품 수량
    "productQuantity": 20 
}
```

**Response Http Status Code**

- 새로운 Cart가 생성되면 201(CREATED) HTTP Status Code 를 반환합니다.

---

## `Carts`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 목록 리스트 | GET | http://13.124.47.242:8084/carts |

**Parameters for Cart Items List**

| Parameter | Description | Example |
| --- | --- | --- |
| page | 페이지는 생략이 가능하며, 생략할 경우 기본값으로 1페이지가 검색됩니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/carts?page=1
```

**Response Http Status Code**

- Carts 목록 리스트와 200(OK) HTTP Status Code 를 반환합니다.

---

## `Carts`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 상세 보기 | GET | http://13.124.47.242:8084/carts/{cartNo} |

**Parameters for Cart Details**

| Parameter | Description | Example |
| --- | --- | --- |
| {cartNo} | 장바구니에 담긴 상품을 상세 보기 하고자 한다면, 장바구니 번호가 필요합니다. | 1 |
| page | 이전의(장바구니 목록 리스트) 페이지 정보 번호입니다. 페이지는 생략이 가능하며, 생략할 경우 기본값으로 1페이지가 검색됩니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/carts/23?page=1
```

**Response Http Status Code**

- Cart 에 대한 상세 정보와 200(OK) HTTP Status Code 를 반환합니다.

---

## `Carts`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 선택 삭제 | PATCH | http://13.124.47.242:8084/carts/{cartNo} |

**Parameters for Removing Selected from Cart**

| Parameter | Description | Example |
| --- | --- | --- |
| {cartNo} | 장바구니에 담긴 상품을 선택 삭제 하고자 한다면, 장바구니 번호가 필요합니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/carts/1
```

**Response Http Status Code**

- 장바구니 선택 삭제를 완료하면 204(NO CONTENT) HTTP Status Code 를 반환합니다.

---

## `Carts`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 전체 삭제 | PATCH | http://13.124.47.242:8084/carts |

**Example Request**

```json
http://13.124.47.242:8084/carts
```

**Response Http Status Code**

- 장바구니 전체 삭제를 완료하면 204(NO CONTENT) HTTP Status Code 를 반환합니다.

---

## `Purchases`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 장바구니 물품 구매 | POST | http://13.124.47.242:8084/purchases-items |

**HTTP Body** **Example Request**

```json
{
		//구매할 장바구니 번호
    "cartNo": 12, 
		//구매 유저 번호
    "userNo": 1  
}
```

**Response Http Status Code**

- 새로운 Purchase가 생성되면 201(CREATED) HTTP Status Code 를 반환합니다.

---

## `Purchases`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 구매 목록 리스트 | GET | http://13.124.47.242:8084/purchases-items |

**Parameters for Purchase List**

| Parameter | Description | Example |
| --- | --- | --- |
| page | 페이지는 생략이 가능하며, 생략할 경우 기본값으로 1페이지가 검색됩니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/purchases-items?page=1
```

**Response Http Status Code**

- Purchases Items 목록 리스트와 200(OK) HTTP Status Code 를 반환합니다.

---

## `Purchases`

| 기능 | HTTP Method | API Path |
| --- | --- | --- |
| 구매 상세 보기 | GET | http://13.124.47.242:8084/purchases-items/{purchasesItemsNo} |

**Parameters for Purchase Detail**

| Parameter | Description | Example |
| --- | --- | --- |
| {purchasesItemsNo} | 구매한 상품을 상세 보기 하고자 한다면, 구매 번호가 필요합니다. | 1 |
| page | 이전의(구매 목록 리스트) 페이지 정보 번호입니다. 페이지는 생략이 가능하며, 생략할 경우 기본값으로 1페이지가 검색됩니다. | 1 |

**Example Request**

```json
http://13.124.47.242:8084/purchases-items/1?page=1
```

**Response Http Status Code**

- Purchase Item 상세 정와 200(OK) HTTP Status Code 를 반환합니다.

---


