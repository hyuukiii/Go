-- Users
CREATE TABLE Users (
  user_id VARCHAR2(100) PRIMARY KEY,  -- 사용자 아이디
  username VARCHAR2(100),             -- 사용자 이름
  email VARCHAR2(200),                -- 이메일
  password VARCHAR2(100),             -- 비밀번호  
  phone VARCHAR2(20),                 -- 전화번호 (실사용X)     
  address VARCHAR2(300),              -- 주소 (현재 실사용X)
  join_date DATE,                     -- 가입일 (실사용X)    
  profile_image VARCHAR2(300),        -- 프로필 이미지 (이미지 주소 형식으로 저장)
  rating NUMBER(2,1)                  -- 별점 (두자리수, 소수점 첫째 자리까지 표현)
);

-- Categories
CREATE TABLE Categories (
  category_id VARCHAR2(100) PRIMARY KEY,  -- 카테고리 코드 (직관적으로 보기 위해 VARCHAR2로 변경)
  category_name VARCHAR2(100),            -- 카테고리 이름
  display_order NUMBER                    -- 표시 순서
);

-- Products
CREATE TABLE Products (
  product_id NUMBER PRIMARY KEY,          -- 상품 코드
  seller_id VARCHAR2(100),                -- 판매자 아이디
  title VARCHAR2(200),                    -- 제목
  description CLOB,                              -- 상세내용 (CLOB = 아주 긴 텍스트 저장)
  price NUMBER,                           -- 가격 (실제 판매된 가격)
  category_id VARCHAR2(100),              -- 카테고리 코드
  status VARCHAR2(50),                    -- 상태 (판매중, 판매완료)
  register_date DATE,                     -- 등록 날짜
  view_count NUMBER,                      -- 조회수
  maxPrice NUMBER,                        -- 입찰 희망가
  minPrice NUMBER,                        -- 최소 입찰가
  auction_end_time DATE,                  -- 경매 종료 시간
  FOREIGN KEY (seller_id) REFERENCES Users(user_id),
  FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- Reviews
CREATE TABLE Reviews (
  review_id NUMBER PRIMARY KEY,           -- 리뷰 코드
  reviewer_id VARCHAR2(100),              -- 리뷰 작성자 아이디
  seller_id VARCHAR2(100),                -- 물품 판매자 아이디 
  description CLOB,                              -- 리뷰 내용 
  rating NUMBER(2,1),                     -- 리뷰 평점 (판매자의 평점에 영향 줌)
  review_date DATE,                       -- 리뷰 작성 날짜
  FOREIGN KEY (reviewer_id) REFERENCES Users(user_id),
  FOREIGN KEY (seller_id) REFERENCES Users(user_id)
);

-- Favorites
CREATE TABLE Favorites (
  favorite_id NUMBER PRIMARY KEY,         -- 찜 코드
  user_id VARCHAR2(100),                  -- 사용자 아이디
  product_id NUMBER,                      -- 물품 코드
  favorite_date DATE,                     -- 찜 날짜
  FOREIGN KEY (user_id) REFERENCES Users(user_id),
  FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Transactions
CREATE TABLE Transactions (
  trans_id NUMBER PRIMARY KEY,            -- 거래 코드
  seller_id VARCHAR2(100),                -- 판매자 아이디
  buyer_id VARCHAR2(100),                 -- 구매자 아이디
  product_id NUMBER,                      -- 물품 코드
  trans_date DATE,                        -- 거래 날짜
  status VARCHAR2(50),                    -- 상태
  price NUMBER,                           -- 가격 
  payment_method VARCHAR2(50),            -- 거래 방식 (실사용X)
  FOREIGN KEY (seller_id) REFERENCES Users(user_id),
  FOREIGN KEY (buyer_id) REFERENCES Users(user_id),
  FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- Bids
CREATE TABLE Bids (
  bid_id NUMBER PRIMARY KEY,              -- 입찰 코드
  user_id VARCHAR2(100),                  -- 입찰자 아이디
  product_id NUMBER,                      -- 물품 코드
  bid_amount NUMBER,                      -- 입찰 금액
  bid_time DATE,                          -- 입찰 시간
  is_winning CHAR(1),                     -- 'Y' / 'N' (낙찰 여부)
  FOREIGN KEY (user_id) REFERENCES Users(user_id),
  FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

CREATE TABLE Notifications (
    notification_id NUMBER PRIMARY KEY,
    user_id VARCHAR2(100),
    content VARCHAR2(1000),
    notification_type VARCHAR2(100),
    related_product_id NUMBER,
    is_read CHAR(1) DEFAULT 'N',
    created_at DATE DEFAULT SYSDATE
);

ALTER TABLE Reviews
ADD product_id NUMBER;

ALTER TABLE Reviews
ADD CONSTRAINT fk_reviews_product
FOREIGN KEY (product_id)
REFERENCES Products(product_id);

ALTER TABLE Products ADD image_path VARCHAR2(300);

CREATE SEQUENCE favorite_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE bid_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE trans_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE review_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE product_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 1 NOCACHE;

INSERT INTO Categories (
  category_id, category_name, display_order
) VALUES (
  'Electronics', '전자기기', 1
); 

-- 판매자
INSERT INTO Users (
  user_id, username, email, password, phone, address, join_date, profile_image, rating
) VALUES (
  'testseller', '테스트판매자', 'seller@test.com', '1234', '010-1111-1111', '서울시 강남구',
  SYSDATE, NULL, 4.5
);
-- 구매자
INSERT INTO Users (
  user_id, username, email, password, phone, address, join_date, profile_image, rating
) VALUES (
  'testbuyer', '테스트구매자', 'buyer@test.com', '5678', '010-2222-2222', '서울시 종로구',
  SYSDATE, NULL, 4.7
);

select * from users;
select * from categories;
select * from products;
select * from reviews;
select * from favorites;
select * from transactions;
select * from bids;

commit;


