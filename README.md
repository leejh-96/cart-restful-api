# Cart-RestAPI-Webserver
- Docker와 AWS EC2를 사용해 배포는 완료했지만, 수정할 부분과 개선점은 계속해서 수정하도록 하겠습니다.
- 아이디 : outlier1 , 비밀번호 : outlier1
## API 명세서
- 아래의 링크를 통해서도 API 명세서를 보실 수 있습니다.
- https://documenter.getpostman.com/view/28000436/2s9YkraKHx
---
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
| 장바구니 선택 삭제 | PATCH | http://13.124.47.242:8084/carts/1 |

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


