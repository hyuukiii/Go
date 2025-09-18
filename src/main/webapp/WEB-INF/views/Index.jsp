<%@page import="java.text.NumberFormat"%>
<%@page import="BidsTransaction.BidDAO"%>
<%@page import="java.util.ArrayList"%>
<%@page import="Product.ProductDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
ArrayList<ProductDTO> products = (ArrayList<ProductDTO>) request.getAttribute("products");
ArrayList<ProductDTO> popularProducts = (ArrayList<ProductDTO>) request.getAttribute("popularProducts");
ArrayList<ProductDTO> completedAuctions = (ArrayList<ProductDTO>) request.getAttribute("completedAuctions");

// 입찰가 조회를 위한 DAO 객체 생성
BidDAO bidDAO = new BidDAO();
// 숫자 포맷 객체 생성
NumberFormat nf = NumberFormat.getNumberInstance();
%>
<%
String userId = (String) session.getAttribute("user_id");
String statusMessage = (String) request.getAttribute("statusMessage");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>메인화면</title>
<!-- Font Awesome CDN 추가 -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<style>
.status-message {
	padding: 10px;
	margin: 10px 0;
	border-radius: 4px;
	background-color: #4CAF50;
	color: white;
	text-align: center;
}

.section-header {
	background-color: #f5f5f5;
	padding: 10px;
	margin-top: 20px;
	border-radius: 4px;
	border-left: 4px solid #007bff;
}

.popular-header {
	border-left: 4px solid #ff9800;
}

.product-card {
	width: 200px;
	text-align: center;
	margin-bottom: 20px;
	border: 1px solid #ddd;
	border-radius: 8px;
	padding-bottom: 10px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
}

.product-card:hover {
	transform: translateY(-5px);
}

.product-image {
	width: 100%;
	height: 200px;
	object-fit: cover;
	border-top-left-radius: 8px;
	border-top-right-radius: 8px;
}

.product-title {
	margin: 10px 5px;
	font-weight: bold;
	height: 40px;
	overflow: hidden;
}

.product-price {
	color: #e44d26;
	font-weight: bold;
}

.product-status {
	background-color: #f8d7da;
	color: #721c24;
	display: inline-block;
	padding: 2px 8px;
	border-radius: 4px;
	margin-top: 5px;
	font-size: 12px;
}

.view-count {
	font-size: 12px;
	color: #6c757d;
	margin-top: 5px;
}

.popular-badge {
	position: absolute;
	top: 10px;
	right: 10px;
	background-color: #ff9800;
	color: white;
	padding: 3px 8px;
	border-radius: 4px;
	font-size: 12px;
	font-weight: bold;
}

.products-container {
	display: flex;
	flex-wrap: wrap;
	gap: 20px;
}

/* 스택 카드 스와이프 스타일 제거됨 */
.header {
	background-color: #ffffff;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	padding: 15px 20px;
	margin-bottom: 20px;
}

.header-content {
	display: flex;
	align-items: center;
	justify-content: space-between;
	max-width: 1200px;
	margin: 0 auto;
}

.header-left {
	display: flex;
	align-items: center;
}

.header-right {
	display: flex;
	align-items: center;
	gap: 15px;
}

.welcome-msg {
	margin-right: 10px;
	font-weight: bold;
}

.header-btn {
	text-decoration: none;
	color: #333;
	padding: 5px 10px;
	border-radius: 4px;
	border: 1px solid #ddd;
	transition: all 0.3s;
}

.header-btn:hover {
	background-color: #f5f5f5;
}

.notification-icon-container {
	position: relative;
	margin-left: 10px;
}

.notification-icon {
	color: #333;
	font-size: 18px;
	text-decoration: none;
}

.notification-badge {
	position: absolute;
	top: -8px;
	right: -8px;
	background-color: #ff4136;
	color: white;
	border-radius: 50%;
	width: 18px;
	height: 18px;
	font-size: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.footer {
	background-color: #f8f9fa;
	padding: 20px 0;
	margin-top: 50px;
	border-top: 1px solid #e7e7e7;
}

.footer-content {
	max-width: 1200px;
	margin: 0 auto;
	text-align: center;
	color: #6c757d;
}

.footer-links {
	margin-top: 10px;
}

.footer-links a {
	color: #6c757d;
	text-decoration: none;
	margin: 0 5px;
}

.footer-links a:hover {
	text-decoration: underline;
}

.content {
	max-width: 1200px;
	margin: 0 auto;
	padding: 0 20px;
}

.actions {
	margin-bottom: 20px;
	text-align: right;
}

.action-btn {
	display: inline-block;
	padding: 8px 16px;
	background-color: #007bff;
	color: white;
	text-decoration: none;
	border-radius: 4px;
	transition: background-color 0.3s;
}

.action-btn:hover {
	background-color: #0056b3;
}

/* 검색 기능 스타일 */
.search-container {
	flex: 1;
	max-width: 500px;
	margin: 0 20px;
	position: relative;
}

.search-form {
	display: flex;
	width: 100%;
}

.search-input {
	flex: 1;
	border: 2px solid #e0e0e0;
	border-radius: 20px 0 0 20px;
	padding: 8px 15px;
	font-size: 14px;
	transition: border-color 0.3s;
}

.search-input:focus {
	outline: none;
	border-color: #007bff;
}

.search-btn {
	background-color: #007bff;
	color: white;
	border: none;
	border-radius: 0 20px 20px 0;
	padding: 8px 15px;
	cursor: pointer;
	transition: background-color 0.3s;
}

.search-btn:hover {
	background-color: #0056b3;
}

.search-filters {
	display: none;
	position: absolute;
	top: 100%;
	left: 0;
	width: 100%;
	background-color: white;
	border: 1px solid #e0e0e0;
	border-radius: 4px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
	padding: 10px;
	z-index: 9999;
	margin-top: 5px;
}

.filter-group {
	margin-bottom: 10px;
}

.filter-title {
	font-weight: bold;
	margin-bottom: 5px;
	font-size: 14px;
}

.filter-options {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.filter-option {
	background-color: #f5f5f5;
	border: 1px solid #ddd;
	border-radius: 20px;
	padding: 4px 10px;
	font-size: 12px;
	cursor: pointer;
	transition: all 0.2s;
}

.filter-option:hover, .filter-option.active {
	background-color: #e3f2fd;
	border-color: #007bff;
	color: #007bff;
}

.filter-button {
	background-color: transparent;
	border: none;
	color: #6c757d;
	font-size: 14px;
	cursor: pointer;
	display: flex;
	align-items: center;
	position: absolute;
	right: 60px;
	top: 50%;
	transform: translateY(-50%);
}

.filter-button i {
	margin-right: 4px;
}

.product-status {
	background-color: #f8d7da;
	color: #721c24;
	display: inline-block;
	padding: 2px 8px;
	border-radius: 4px;
	margin-top: 5px;
	font-size: 12px;
}

/* 인기 상품 랭킹 스타일 복원 */
/* 랭킹 관련 CSS 스타일 제거됨 */

/* 랭킹 컨테이너 스타일 */
.ranking-container {
	background-color: #f8f9fa;
	border-radius: 8px;
	padding: 15px;
	margin-bottom: 30px;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.ranking-title {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15px;
	font-size: 18px;
	font-weight: bold;
}

.ranking-tabs {
	display: flex;
	gap: 5px;
}

.ranking-tab {
	padding: 5px 10px;
	border-radius: 4px;
	background-color: #e9ecef;
	cursor: pointer;
	font-size: 14px;
	font-weight: normal;
	transition: all 0.3s;
}

.ranking-tab:hover {
	background-color: #d1e7ff;
}

.ranking-tab.active {
	background-color: #007bff;
	color: white;
}

.ranking-carousel {
	position: relative;
	display: flex;
	align-items: center;
	gap: 10px;
}

.carousel-arrow {
	background: rgba(255, 255, 255, 0.8);
	border: none;
	border-radius: 50%;
	width: 30px;
	height: 30px;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
	z-index: 2;
}

.carousel-arrow:hover {
	background: rgba(255, 255, 255, 1);
}

.ranking-items {
	flex: 1;
	display: flex;
	overflow-x: auto;
	gap: 15px;
	padding: 10px 5px;
	scroll-behavior: smooth;
	-ms-overflow-style: none;
	scrollbar-width: none;
}

.ranking-items::-webkit-scrollbar {
	display: none;
}

.ranking-item {
	flex: 0 0 auto;
	width: 200px;
	border-radius: 8px;
	background-color: white;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
	padding: 10px;
	position: relative;
	cursor: pointer;
	transition: transform 0.2s, box-shadow 0.2s;
}

.ranking-item:hover {
	transform: translateY(-5px);
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.rank-badge {
	position: absolute;
	top: 10px;
	left: 10px;
	width: 24px;
	height: 24px;
	border-radius: 50%;
	background-color: #6c757d;
	color: white;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: bold;
	font-size: 12px;
	z-index: 1;
}

.rank-badge.top-3 {
	background-color: #ff9800;
	width: 26px;
	height: 26px;
	font-size: 14px;
}

.rank-badge.new {
	left: auto;
	right: 10px;
	background-color: #28a745;
	width: auto;
	padding: 0 8px;
	border-radius: 10px;
	font-size: 10px;
}

.rank-badge.up, .rank-badge.down {
	left: 40px;
	width: auto;
	padding: 0 5px;
	border-radius: 10px;
	font-size: 10px;
}

.rank-badge.up {
	background-color: #28a745;
}

.rank-badge.down {
	background-color: #dc3545;
}

.rank-image {
	width: 100%;
	height: 150px;
	overflow: hidden;
	border-radius: 4px;
	margin-bottom: 10px;
}

.rank-item-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.rank-info {
	text-align: center;
}

.rank-title {
	font-weight: bold;
	margin-bottom: 5px;
	height: 40px;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	font-size: 14px;
}

.rank-price {
	color: #e44d26;
	font-weight: bold;
	font-size: 16px;
}

@media ( max-width : 768px) {
	/* 랭킹 관련 스타일 제거됨 */
	.ranking-title {
		flex-direction: column;
		align-items: flex-start;
		gap: 10px;
	}
	.ranking-tabs {
		width: 100%;
		justify-content: space-between;
	}
	.ranking-tab {
		flex: 1;
		text-align: center;
		font-size: 12px;
		padding: 5px;
	}
	.ranking-item {
		width: 160px;
	}
	.rank-title {
		font-size: 12px;
		height: 36px;
	}
	.rank-price {
		font-size: 14px;
	}
}

/* 로딩 및 에러 메시지 스타일 유지 */
.loading-indicator {
	width: 100%;
	text-align: center;
	padding: 20px;
	color: #888;
}

.error-message {
	text-align: center;
	padding: 20px;
	color: #d9534f;
}

@media ( max-width : 768px) { /* 랭킹 관련 스타일 제거됨 */
}

/* 추가: 카테고리 네비게이션 스타일 */
.category-nav {
	display: flex;
	justify-content: center;
	margin: 20px auto;
	max-width: 1200px;
	background-color: #f8f9fa;
	border-radius: 8px;
	padding: 10px;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.category-container {
	max-width: 1200px;
	margin: 0 auto;
	display: flex;
	overflow-x: auto;
	white-space: nowrap;
	-ms-overflow-style: none; /* IE and Edge */
	scrollbar-width: none; /* Firefox */
	padding: 0 20px;
}

.category-container::-webkit-scrollbar {
	display: none; /* Chrome, Safari, Opera */
}

.category-item {
	padding: 8px 15px;
	margin: 0 5px;
	cursor: pointer;
	border-radius: 4px;
	transition: all 0.3s;
	text-decoration: none;
	color: #333;
}

.category-item:hover, .category-item.active {
	background-color: #007bff;
	color: white;
}

@media ( max-width : 768px) {
	.category-nav {
		flex-wrap: wrap;
	}
	.category-item {
		margin: 5px;
	}
}

/* 상품 카드에 카테고리 데이터 속성 추가를 위한 스타일 */
.product-card[data-category] {
	transition: opacity 0.3s, transform 0.3s;
}

.product-card.hidden {
	display: none;
}

/* 추가: 카테고리 드롭다운 스타일 */
.category-menu {
	display: flex;
	justify-content: center;
	background-color: #f8f9fa;
	border-bottom: 1px solid #e7e7e7;
	margin-bottom: 20px;
}

.category-menu-container {
	max-width: 1200px;
	width: 100%;
	display: flex;
	justify-content: space-between;
	padding: 0;
	margin: 0;
	list-style: none;
}

.category-item {
	position: relative;
	padding: 12px 15px;
	cursor: pointer;
	transition: all 0.3s;
	font-weight: bold;
}

.category-item:hover {
	background-color: #e9ecef;
}

.dropdown-content {
	display: none;
	position: absolute;
	top: 100%;
	left: 0;
	background-color: white;
	min-width: 200px;
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
	border-radius: 4px;
	z-index: 9999;
	padding: 10px 0;
}

.category-item:hover .dropdown-content {
	display: block;
}

.dropdown-item {
	padding: 8px 15px;
	display: block;
	text-decoration: none;
	color: #333;
	transition: background-color 0.2s;
}

.dropdown-item:hover {
	background-color: #f5f5f5;
}

.category-item i {
	margin-left: 5px;
	font-size: 12px;
}
</style>
</head>
<body>
	<!-- 헤더 포함 -->
	<div class="header">
		<div class="header-content">
			<div class="header-left">
				<h1>
					<a href="index" style="text-decoration: none; color: inherit;">옥션
						플랫폼</a>
				</h1>
			</div>

			<!-- 검색 기능 추가 -->
			<div class="search-container">
				<form action="search" method="get" class="search-form">
					<input type="text" name="keyword" class="search-input"
						placeholder="찾고 싶은 상품을 검색해보세요">
					<button type="button" class="filter-button" id="show-filters">
						<i class="fas fa-sliders-h"></i> 필터
					</button>
					<button type="submit" class="search-btn">
						<i class="fas fa-search"></i>
					</button>
				</form>

				<!-- 검색 필터 드롭다운 -->
				<div class="search-filters" id="search-filters"
					style="display: none;">
					<div class="filter-group">
						<div class="filter-title">카테고리</div>
						<div class="filter-options">
							<span class="filter-option" data-category="all">전체</span> <span
								class="filter-option" data-category="electronics">전자기기</span> <span
								class="filter-option" data-category="fashion">패션</span> <span
								class="filter-option" data-category="beauty">뷰티/미용</span> <span
								class="filter-option" data-category="sports">스포츠/레저</span> <span
								class="filter-option" data-category="books">도서</span> <span
								class="filter-option" data-category="appliances">가전제품</span>
						</div>
					</div>

					<div class="filter-group">
						<div class="filter-title">가격 범위</div>
						<div class="filter-options">
							<span class="filter-option" data-price="0-50000">5만원 이하</span> <span
								class="filter-option" data-price="50000-100000">5만원~10만원</span>
							<span class="filter-option" data-price="100000-300000">10만원~30만원</span>
							<span class="filter-option" data-price="300000-">30만원 이상</span>
						</div>
					</div>

					<div class="filter-group">
						<div class="filter-title">상품 상태</div>
						<div class="filter-options">
							<span class="filter-option" data-status="onsale">판매중</span> <span
								class="filter-option" data-status="closed">경매종료</span>
						</div>
					</div>

					<div class="filter-group">
						<div class="filter-title">정렬 방식</div>
						<div class="filter-options">
							<span class="filter-option" data-sort="relevance">관련성순</span> <span
								class="filter-option" data-sort="priceAsc">가격 낮은순</span> <span
								class="filter-option" data-sort="priceDesc">가격 높은순</span> <span
								class="filter-option" data-sort="newest">최신순</span> <span
								class="filter-option" data-sort="endingSoon">마감임박순</span>
						</div>
					</div>
				</div>
			</div>

			<div class="header-right">
				<%
				if (userId != null) {
				%>
				<span class="welcome-msg"><%=userId%>님 환영합니다</span> <a href="mypage"
					class="header-btn">마이페이지</a>
				<form action="logout" method="post" style="display: inline;">
					<button type="submit" class="header-btn"
						style="cursor: pointer; background: none; border: 1px solid #ddd; padding: 5px 10px; border-radius: 4px;">로그아웃</button>
				</form>

				<!-- 알림 아이콘 추가 -->
				<div class="notification-icon-container">
					<a href="notifications" class="notification-icon"> <i
						class="fa fa-bell"></i> <%
						// 미읽은 알림 개수 가져오기
						int unreadNotificationCount = 0;
						try {
							Notification.NotificationDAO notificationDAO = new Notification.NotificationDAO();
							unreadNotificationCount = notificationDAO.getUnreadNotificationCount(userId);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (unreadNotificationCount > 0) {
						%> <span class="notification-badge"><%=unreadNotificationCount%></span>
						<%
						}
						%>
					</a>
				</div>
				<%
				} else {
				%>
				<a href="login" class="header-btn">로그인</a> <a href="signup"
					class="header-btn">회원가입</a>
				<%
				}
				%>
			</div>
		</div>

		<!-- 카테고리 메뉴 추가 -->
		<div class="category-menu">
			<ul class="category-menu-container">
				<li class="category-item">전자기기 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=electronics&subcategory=mobile"
							class="dropdown-item">스마트폰/태블릿</a> <a
							href="search?category=electronics&subcategory=computer"
							class="dropdown-item">노트북/PC</a> <a
							href="search?category=electronics&subcategory=camera"
							class="dropdown-item">카메라/캠코더</a> <a
							href="search?category=electronics&subcategory=audio"
							class="dropdown-item">오디오/이어폰/헤드셋</a> <a
							href="search?category=electronics&subcategory=wearable"
							class="dropdown-item">웨어러블 기기</a> <a
							href="search?category=electronics&subcategory=gaming"
							class="dropdown-item">게임기/게임타이틀</a>
					</div>
				</li>
				<li class="category-item">패션 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=fashion&subcategory=women"
							class="dropdown-item">여성의류</a> <a
							href="search?category=fashion&subcategory=men"
							class="dropdown-item">남성의류</a> <a
							href="search?category=fashion&subcategory=kids"
							class="dropdown-item">아동의류</a> <a
							href="search?category=fashion&subcategory=bags"
							class="dropdown-item">가방/지갑</a> <a
							href="search?category=fashion&subcategory=shoes"
							class="dropdown-item">신발</a> <a
							href="search?category=fashion&subcategory=accessories"
							class="dropdown-item">액세서리/주얼리</a>
					</div>
				</li>
				<li class="category-item">뷰티/미용 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=beauty&subcategory=skincare"
							class="dropdown-item">스킨케어</a> <a
							href="search?category=beauty&subcategory=makeup"
							class="dropdown-item">메이크업</a> <a
							href="search?category=beauty&subcategory=haircare"
							class="dropdown-item">헤어케어</a> <a
							href="search?category=beauty&subcategory=bodycare"
							class="dropdown-item">바디케어</a> <a
							href="search?category=beauty&subcategory=perfume"
							class="dropdown-item">향수</a> <a
							href="search?category=beauty&subcategory=menbeauty"
							class="dropdown-item">남성화장품</a>
					</div>
				</li>
				<li class="category-item">스포츠/레저 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=sports&subcategory=fitness"
							class="dropdown-item">헬스/요가/필라테스</a> <a
							href="search?category=sports&subcategory=golf"
							class="dropdown-item">골프</a> <a
							href="search?category=sports&subcategory=outdoor"
							class="dropdown-item">등산/아웃도어</a> <a
							href="search?category=sports&subcategory=bicycle"
							class="dropdown-item">자전거</a> <a
							href="search?category=sports&subcategory=swimming"
							class="dropdown-item">수영/수상스포츠</a> <a
							href="search?category=sports&subcategory=ballgame"
							class="dropdown-item">구기스포츠</a>
					</div>
				</li>
				<li class="category-item">도서 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=books&subcategory=fiction"
							class="dropdown-item">소설/문학</a> <a
							href="search?category=books&subcategory=education"
							class="dropdown-item">교육/학습</a> <a
							href="search?category=books&subcategory=kids"
							class="dropdown-item">아동/청소년</a> <a
							href="search?category=books&subcategory=comic"
							class="dropdown-item">만화/웹툰</a> <a
							href="search?category=books&subcategory=magazine"
							class="dropdown-item">잡지/정기간행물</a> <a
							href="search?category=books&subcategory=foreign"
							class="dropdown-item">외국도서</a>
					</div>
				</li>
				<li class="category-item">가전제품 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?category=appliances&subcategory=tv"
							class="dropdown-item">TV/프로젝터</a> <a
							href="search?category=appliances&subcategory=refrigerator"
							class="dropdown-item">냉장고/김치냉장고</a> <a
							href="search?category=appliances&subcategory=washer"
							class="dropdown-item">세탁기/건조기</a> <a
							href="search?category=appliances&subcategory=kitchen"
							class="dropdown-item">주방가전</a> <a
							href="search?category=appliances&subcategory=aircon"
							class="dropdown-item">에어컨/제습기</a> <a
							href="search?category=appliances&subcategory=smallappliance"
							class="dropdown-item">소형가전</a>
					</div>
				</li>
				<li class="category-item">신상품 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?sort=newest&days=3" class="dropdown-item">최근
							3일</a> <a href="search?sort=newest&days=7" class="dropdown-item">최근
							7일</a> <a href="search?sort=newest&days=30" class="dropdown-item">최근
							30일</a>
					</div>
				</li>
				<li class="category-item">마감임박 <i class="fas fa-caret-down"></i>
					<div class="dropdown-content">
						<a href="search?endingSoon=1" class="dropdown-item">1시간 이내</a> <a
							href="search?endingSoon=3" class="dropdown-item">3시간 이내</a> <a
							href="search?endingSoon=6" class="dropdown-item">6시간 이내</a> <a
							href="search?endingSoon=24" class="dropdown-item">24시간 이내</a>
					</div>
				</li>
			</ul>
		</div>
	</div>

	<!-- ----------------------------------------------------------------------------------------------여기까지가 헤더----------------------------------------------------------------------------------------------------- -->

	<div class="content">
		<!-- 인기 상품 랭킹 캐러셀 (최상단 배치) -->
		<div class="ranking-container">
			<div class="ranking-title">
				<span>인기 상품 랭킹</span>
				<div class="ranking-tabs">
					<div class="ranking-tab active" data-type="views">조회순</div>
					<div class="ranking-tab" data-type="bids">입찰순</div>
					<div class="ranking-tab" data-type="favorites">찜순</div>
				</div>
			</div>
			<div class="ranking-carousel">
				<button class="carousel-arrow prev-arrow">
					<i class="fas fa-chevron-left"></i>
				</button>
				<div class="ranking-items">
					<div class="loading-indicator">랭킹 로딩 중...</div>
				</div>
				<button class="carousel-arrow next-arrow">
					<i class="fas fa-chevron-right"></i>
				</button>
			</div>
		</div>

		<!-- 기존 코드 유지 -->


		<%
		if (statusMessage != null && !statusMessage.isEmpty()) {
		%>
		<div class="status-message"><%=statusMessage%></div>
		<%
		}
		%>

		<h2 class="section-header">판매중인 상품 목록</h2>

		<%
		if (products == null || products.isEmpty()) {
		%>
		<p style="color: gray; font-weight: bold;">현재 판매중인 물품이 없습니다.</p>
		<%
		} else {
		%>
		<div class="products-container">
			<%
			for (ProductDTO p : products) {
				// 현재 최고 입찰가 조회
				Integer currentBid = bidDAO.getCurrentBid(p.getProduct_id());
				// 표시할 가격 설정 (입찰가가 있으면 입찰가, 없으면 기본 가격)
				int displayPrice = (currentBid != null && currentBid > 0) ? currentBid : p.getPrice();
				String formattedPrice = nf.format(displayPrice);
				
				// 카테고리 ID를 카테고리 코드로 변환
				String categoryCode = getCategoryCode(p.getCategory_id());
			%>
			<div class="product-card" data-category="<%=categoryCode%>">
				<a
					href="<%=request.getContextPath()%>/buyingproduct?product_id=<%=p.getProduct_id()%>">
					<img
					src="<%=request.getContextPath()%>/<%=p.getImage_path() != null ? p.getImage_path() : "images/default.jpg"%>"
					alt="상품 이미지" class="product-image">
				</a>
				<div class="product-title"><%=p.getTitle()%></div>
				<div class="product-price">
					<%=formattedPrice%>원
					<% if (currentBid != null && currentBid > 0) { %>
					<span
						style="font-size: 0.8rem; font-weight: normal; margin-left: 3px; color: #FF9800;">
						(현재 입찰가)</span>
					<% } %>
				</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>

		<!-- 경매 종료된 상품 목록 섹션 -->
		<h2 class="section-header">경매가 종료된 상품 목록</h2>

		<%
		if (completedAuctions == null || completedAuctions.isEmpty()) {
		%>
		<p style="color: gray; font-weight: bold;">경매가 종료된 물품이 없습니다.</p>
		<%
		} else {
		%>
		<div class="products-container">
			<%
			for (ProductDTO p : completedAuctions) {
				// 경매 종료된 상품은 price가 최종 낙찰가임
				String formattedPrice = nf.format(p.getPrice());
				
				// 카테고리 ID를 카테고리 코드로 변환
				String categoryCode = getCategoryCode(p.getCategory_id());
			%>
			<div class="product-card" data-category="<%=categoryCode%>">
				<a
					href="<%=request.getContextPath()%>/buyingproduct?product_id=<%=p.getProduct_id()%>">
					<img
					src="<%=request.getContextPath()%>/<%=p.getImage_path() != null ? p.getImage_path() : "images/default.jpg"%>"
					alt="상품 이미지" class="product-image">
				</a>
				<div class="product-title"><%=p.getTitle()%></div>
				<div class="product-price">
					<%=formattedPrice%>원 <span
						style="font-size: 0.8rem; font-weight: normal; margin-left: 3px; color: #6c757d;">
						(낙찰가)</span>
				</div>
				<div class="product-status">경매 종료</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>

		<h2 class="section-header">내가 찜한 상품</h2>
		<%
		ArrayList<ProductDTO> favorites = (ArrayList<ProductDTO>) request.getAttribute("favorites");
		if (userId == null) {
		%>
		<p style="color: red;">로그인 후 이용 가능합니다.</p>
		<%
		} else if (favorites == null || favorites.isEmpty()) {
		%>
		<p>찜한 상품이 없습니다.</p>
		<%
		} else {
		%>
		<div class="products-container">
			<%
			for (ProductDTO p : favorites) {
				// 현재 최고 입찰가 조회
				Integer currentBid = bidDAO.getCurrentBid(p.getProduct_id());
				// 표시할 가격 설정 (입찰가가 있으면 입찰가, 없으면 기본 가격)
				int displayPrice = (currentBid != null && currentBid > 0) ? currentBid : p.getPrice();
				String formattedPrice = nf.format(displayPrice);
				
				// 카테고리 ID를 카테고리 코드로 변환
				String categoryCode = getCategoryCode(p.getCategory_id());
			%>
			<div class="product-card" data-category="<%=categoryCode%>">
				<a href="buyingproduct?product_id=<%=p.getProduct_id()%>"> <img
					src="<%=request.getContextPath()%>/<%=p.getImage_path() != null ? p.getImage_path() : "images/default.jpg"%>"
					class="product-image">
				</a>
				<div class="product-title"><%=p.getTitle()%></div>
				<div class="product-price">
					<%=formattedPrice%>원
					<% if (currentBid != null && currentBid > 0) { %>
					<span
						style="font-size: 0.8rem; font-weight: normal; margin-left: 3px; color: #FF9800;">
						(현재 입찰가)</span>
					<% } %>
				</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>

		<h2 class="section-header">낙찰한 상품</h2>

		<%
		if (userId == null) {
		%>
		<p style="color: red;">로그인 후 이용 가능합니다.</p>
		<%
		} else {
		ArrayList<ProductDTO> winningProducts = (ArrayList<ProductDTO>) request.getAttribute("winningProducts");
		if (winningProducts == null || winningProducts.isEmpty()) {
		%>
		<p style="color: gray;">현재 낙찰한 상품이 없습니다.</p>
		<%
		} else {
		%>
		<div class="products-container">
			<%
			for (ProductDTO p : winningProducts) {
				// 낙찰된 상품은 price가 낙찰가임
				String formattedPrice = nf.format(p.getPrice());
				
				// 카테고리 ID를 카테고리 코드로 변환
				String categoryCode = getCategoryCode(p.getCategory_id());
			%>
			<div class="product-card" data-category="<%=categoryCode%>">
				<a href="buyingproduct?product_id=<%=p.getProduct_id()%>"> <img
					src="<%=request.getContextPath()%>/<%=p.getImage_path() != null ? p.getImage_path() : "images/default.jpg"%>"
					alt="상품 이미지" class="product-image">
				</a>
				<div class="product-title"><%=p.getTitle()%></div>
				<div class="product-price">
					<%=formattedPrice%>원 <span
						style="font-size: 0.8rem; font-weight: normal; margin-left: 3px; color: #6c757d;">
						(낙찰가)</span>
				</div>
				<div class="product-status">낙찰됨</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		}
		%>
		<%
		}
		%>
	</div>

	<!-- 푸터 추가 -->
	<div class="footer">
		<div class="footer-content">
			<p>&copy; 2024 옥션 플랫폼. All rights reserved.</p>
			<div class="footer-links">
				<a href="#">이용약관</a> | <a href="#">개인정보처리방침</a> | <a href="#">고객센터</a>
				<% if (userId != null) { %>
				| <a href="receivedreviews">받은 리뷰 보기</a> | <a href="reviewform">리뷰
					작성하기</a>
				<% } %>
			</div>
		</div>
	</div>
</body>
<script>
	const contextPath = '<%= request.getContextPath() %>';

	// 전역 변수로 필터 버튼과 컨테이너 참조를 저장
	let showFiltersBtn;
	let filtersContainer;

	// 페이지 로드 시 초기화 함수
	document.addEventListener('DOMContentLoaded', function() {
		console.log('DOM이 로드되었습니다. 필터 및 랭킹 기능 초기화 중...');
		
		// 필터 관련 요소 참조 얻기
		showFiltersBtn = document.getElementById('show-filters');
		filtersContainer = document.getElementById('search-filters');
		
		// 필터 기능 초기화
		initFilterUI();
		
		// 인기 상품 랭킹 기능 초기화
		initRankings();
		
		// 이미지 오류 처리를 위한 전역 핸들러
		document.querySelectorAll('img').forEach(img => {
			if (!img.hasAttribute('onerror')) {
				img.onerror = function() {
					this.src = '<%=request.getContextPath()%>/images/default.jpg';
					console.log('이미지 로드 실패 (전역 핸들러): ' + this.alt);
				};
			}
		});
	});
	
	// 필터 UI 초기화 함수
	function initFilterUI() {
		console.log('필터 UI 초기화 중...');
		
		if (!showFiltersBtn || !filtersContainer) {
			console.error('필터 버튼 또는 필터 컨테이너를 찾을 수 없습니다.', {
				'showFiltersBtn': showFiltersBtn, 
				'filtersContainer': filtersContainer
			});
			return;
		}
		
		console.log('필터 버튼 및 컨테이너 참조 얻음:', {
			'버튼': showFiltersBtn.id, 
			'컨테이너': filtersContainer.id
		});
		
		// 필터 버튼 클릭 이벤트
		showFiltersBtn.addEventListener('click', function(e) {
			e.preventDefault();
			e.stopPropagation();
			console.log('필터 버튼 클릭됨, 현재 표시 상태:', filtersContainer.style.display);
			
			// 표시 상태 토글
			if (filtersContainer.style.display === 'block') {
				filtersContainer.style.display = 'none';
				console.log('필터 패널 숨김');
			} else {
				filtersContainer.style.display = 'block';
				console.log('필터 패널 표시');
			}
		});
		
		// 필터 옵션 클릭 이벤트
		const filterOptions = document.querySelectorAll('.filter-option');
		filterOptions.forEach(option => {
			option.addEventListener('click', function(e) {
				e.preventDefault();
				e.stopPropagation();
				
				console.log('필터 옵션 클릭됨:', this.textContent);
				
				// 같은 그룹 내에서는 하나만 선택 가능하도록 처리
				const groupOptions = this.parentElement.querySelectorAll('.filter-option');
				groupOptions.forEach(opt => opt.classList.remove('active'));
				this.classList.add('active');
				
				// 선택된 필터 값을 폼에 추가
				const type = this.dataset.category ? 'category' : 
					this.dataset.price ? 'price' : 
					this.dataset.status ? 'status' : 
					this.dataset.sort ? 'sort' : '';
				
				const value = this.dataset.category || this.dataset.price || this.dataset.status || this.dataset.sort;
				
				console.log('필터 값 설정:', type, '=', value);
				
				// 기존 히든 필드가 있으면 업데이트, 없으면 생성
				let hiddenField = document.querySelector(`input[name="${type}"]`);
				if (!hiddenField) {
					hiddenField = document.createElement('input');
					hiddenField.type = 'hidden';
					hiddenField.name = type;
					document.querySelector('.search-form').appendChild(hiddenField);
				}
				
				hiddenField.value = value;
			});
		});
		
		// 검색 버튼 클릭 이벤트 - 폼 제출 처리
		const searchBtn = document.querySelector('.search-btn');
		if (searchBtn) {
			searchBtn.addEventListener('click', function(e) {
				e.preventDefault();
				
				console.log('검색 버튼 클릭, 모든 필터 적용');
				
				// 선택된 필터 옵션 가져오기
				const categoryField = document.querySelector('input[name="category"]');
				const priceField = document.querySelector('input[name="price"]');
				const sortField = document.querySelector('input[name="sort"]');
				const statusField = document.querySelector('input[name="status"]');
				
				// 카테고리 필터 적용
				if (categoryField && categoryField.value) {
					filterProductsByCategory(categoryField.value);
				}
				
				// 가격 필터 적용
				if (priceField && priceField.value) {
					applyPriceFilter(priceField.value);
				}
				
				// 정렬 필터 적용
				if (sortField && sortField.value) {
					applySortFilter(sortField.value);
				}
				
				// 상품 상태 필터 적용
				if (statusField && statusField.value) {
					filterByStatus(statusField.value);
				}
				
				// 폼 제출
				// 검색어가 입력된 경우에만 서버에 폼을 제출
				const searchInput = document.querySelector('.search-input');
				if (searchInput && searchInput.value.trim() !== '') {
					console.log('검색어 입력됨, 서버에 검색 요청:', searchInput.value);
					document.querySelector('.search-form').submit();
				}
			});
		}
		
		// 문서 다른 곳 클릭 시 필터 패널 닫기
		document.addEventListener('click', function(e) {
			// 필터 컨테이너나 버튼이 클릭 대상 또는 그 상위에 있는지 확인
			if (filtersContainer && showFiltersBtn && 
				!filtersContainer.contains(e.target) && 
				!showFiltersBtn.contains(e.target)) {
				console.log('필터 패널 외부 클릭 감지, 패널 닫기');
				filtersContainer.style.display = 'none';
			}
		});
		
		console.log('필터 UI 초기화 완료');
	}

	// 랭킹 기능 초기화
	function initRankings() {
		console.log('랭킹 기능 초기화');
		
		// 조회순 탭 (기본)
		const viewsTab = document.querySelector('.ranking-tab[data-type="views"]');
		if (viewsTab) {
			viewsTab.addEventListener('click', function() {
				console.log('조회순 탭 클릭');
				document.querySelectorAll('.ranking-tab').forEach(t => t.classList.remove('active'));
				this.classList.add('active');
				loadViewsRanking();
			});
		}
		
		// 입찰순 탭
		const bidsTab = document.querySelector('.ranking-tab[data-type="bids"]');
		if (bidsTab) {
			bidsTab.addEventListener('click', function() {
				console.log('입찰순 탭 클릭');
				document.querySelectorAll('.ranking-tab').forEach(t => t.classList.remove('active'));
				this.classList.add('active');
				loadBidsRanking();
			});
		}
		
		// 찜순 탭
		const favoritesTab = document.querySelector('.ranking-tab[data-type="favorites"]');
		if (favoritesTab) {
			favoritesTab.addEventListener('click', function() {
				console.log('찜순 탭 클릭');
				document.querySelectorAll('.ranking-tab').forEach(t => t.classList.remove('active'));
				this.classList.add('active');
				loadFavoritesRanking();
			});
		}
		
		// 캐러셀 화살표 이벤트 설정
		setupCarouselArrows();
		
		// 기본값 (조회순) 랭킹 로드
		loadViewsRanking();
	}
	
	// 조회순 랭킹 로드
	function loadViewsRanking() {
		console.log('조회순 랭킹 데이터 요청 시작');
		const container = document.querySelector('.ranking-items');
		if (!container) {
			console.error('랭킹 컨테이너를 찾을 수 없습니다');
			return;
		}
		
		container.innerHTML = '<div class="loading-indicator">랭킹 로딩 중...</div>';
		
		const url = `${pageContext.request.contextPath}/product/rankings?type=views&limit=10`;
		console.log('요청 URL:', url);
		
		fetch(url)
			.then(response => {
				console.log(`서버 응답 상태: ${response.status}`);
				if (!response.ok) {
					throw new Error(`서버 응답 오류: ${response.status}`);
				}
				return response.text();
			})
			.then(text => {
				if (!text || text.trim() === '') {
					throw new Error('서버에서 빈 응답을 받았습니다');
				}
				
				console.log('서버 응답 원본 (처음 100자):', text.substring(0, 100) + '...');
				
				try {
					return JSON.parse(text);
				} catch (e) {
					console.error('JSON 파싱 오류:', e);
					throw new Error('JSON 파싱 오류');
				}
			})
			.then(data => {
				if (!data || !Array.isArray(data) || data.length === 0) {
					throw new Error('유효한 랭킹 데이터가 없습니다');
				}
				
				console.log(`조회순 랭킹 데이터 수신:`, data.length, '건');
				displayRankings(data, 'views');
			})
			.catch(error => {
				console.error('랭킹 데이터 로드 실패:', error);
				container.innerHTML = `
					<div class="error-message">
						<i class="fas fa-exclamation-triangle"></i> 
						랭킹을 불러오는 중 오류가 발생했습니다<br>
						${error.message}
					</div>`;
			});
	}
	
	// 입찰순 랭킹 로드
	function loadBidsRanking() {
		console.log('입찰순 랭킹 데이터 요청 시작');
		const container = document.querySelector('.ranking-items');
		if (!container) {
			console.error('랭킹 컨테이너를 찾을 수 없습니다');
			return;
		}
		
		container.innerHTML = '<div class="loading-indicator">랭킹 로딩 중...</div>';
		
		const url = `${pageContext.request.contextPath}/product/rankings?type=bids&limit=10`;
		console.log('요청 URL:', url);
		
		fetch(url)
			.then(response => {
				console.log(`서버 응답 상태: ${response.status}`);
				if (!response.ok) {
					throw new Error(`서버 응답 오류: ${response.status}`);
				}
				return response.text();
			})
			.then(text => {
				if (!text || text.trim() === '') {
					throw new Error('서버에서 빈 응답을 받았습니다');
				}
				
				console.log('서버 응답 원본 (처음 100자):', text.substring(0, 100) + '...');
				
				try {
					return JSON.parse(text);
				} catch (e) {
					console.error('JSON 파싱 오류:', e);
					throw new Error('JSON 파싱 오류');
				}
			})
			.then(data => {
				if (!data || !Array.isArray(data) || data.length === 0) {
					throw new Error('유효한 랭킹 데이터가 없습니다');
				}
				
				console.log(`입찰순 랭킹 데이터 수신:`, data.length, '건');
				displayRankings(data, 'bids');
			})
			.catch(error => {
				console.error('랭킹 데이터 로드 실패:', error);
				container.innerHTML = `
					<div class="error-message">
						<i class="fas fa-exclamation-triangle"></i> 
						랭킹을 불러오는 중 오류가 발생했습니다<br>
						${error.message}
					</div>`;
			});
	}
	
	// 찜순 랭킹 로드
	function loadFavoritesRanking() {
		console.log('찜순 랭킹 데이터 요청 시작');
		const container = document.querySelector('.ranking-items');
		if (!container) {
			console.error('랭킹 컨테이너를 찾을 수 없습니다');
			return;
		}
		
		container.innerHTML = '<div class="loading-indicator">랭킹 로딩 중...</div>';
		
		const url = `${pageContext.request.contextPath}/product/rankings?type=favorites&limit=10`;
		console.log('요청 URL:', url);
		
		fetch(url)
			.then(response => {
				console.log(`서버 응답 상태: ${response.status}`);
				if (!response.ok) {
					throw new Error(`서버 응답 오류: ${response.status}`);
				}
				return response.text();
			})
			.then(text => {
				if (!text || text.trim() === '') {
					throw new Error('서버에서 빈 응답을 받았습니다');
				}
				
				console.log('서버 응답 원본 (처음 100자):', text.substring(0, 100) + '...');
				
				try {
					return JSON.parse(text);
				} catch (e) {
					console.error('JSON 파싱 오류:', e);
					throw new Error('JSON 파싱 오류');
				}
			})
			.then(data => {
				if (!data || !Array.isArray(data) || data.length === 0) {
					throw new Error('유효한 랭킹 데이터가 없습니다');
				}
				
				console.log(`찜순 랭킹 데이터 수신:`, data.length, '건');
				displayRankings(data, 'favorites');
			})
			.catch(error => {
				console.error('랭킹 데이터 로드 실패:', error);
				container.innerHTML = `
					<div class="error-message">
						<i class="fas fa-exclamation-triangle"></i> 
						랭킹을 불러오는 중 오류가 발생했습니다<br>
						${error.message}
					</div>`;
			});
	}
	
	// 랭킹 표시 함수
	function displayRankings(products, type) {
		console.log('랭킹 표시 시작');
		
		const container = document.querySelector('.ranking-items');
		if (!container) return;
		
		// 컨테이너 초기화
		container.innerHTML = '';
		
		// 각 상품에 대한 랭킹 아이템 생성
		products.forEach((product, index) => {
			// 상품 ID 검증
			const productId = Number(product.product_id);
			console.log(`상품 ${index+1} 조회: ID=${productId}, 제목=${product.title || '제목 없음'}`);
			
			if (!productId) {
				console.warn(`상품 ${index+1}의 ID가 유효하지 않습니다:`, product);
				return;
			}
			
			// 제목 및 가격 처리
			const title = product.title || '제목 없음';
			const price = Number(product.price) || 0;
			const formattedPrice = new Intl.NumberFormat('ko-KR').format(price);
			
			// 현재 최고 입찰가 처리
			const currentBid = Number(product.current_bid) || 0;
			// 현재 입찰가가 0인 경우 기본 가격을 표시, 그렇지 않으면 현재 입찰가를 표시
			const displayPrice = currentBid > 0 ? currentBid : price;
			const formattedDisplayPrice = new Intl.NumberFormat('ko-KR').format(displayPrice);
			
			// 이미지 경로 처리
			let imagePath = product.image_path || '';
			if (!imagePath || imagePath.trim() === '') {
				imagePath = '<%=request.getContextPath()%>/images/default.jpg';
			} else {
				// 상대 경로/절대 경로 처리
				if (imagePath.startsWith('/')) {
					imagePath = '<%=request.getContextPath()%>' + imagePath;
				} else if (!imagePath.startsWith('http')) {
					imagePath = '<%=request.getContextPath()%>/' + imagePath;
				}
				
				// 경로 정리
				imagePath = imagePath.replace(/ /g, '%20').replace(/\\/g, '/');
			}
			
			// 추가 속성 (신규 상품, 순위 변동)
			const isNew = product.is_new === true || product.is_new === 'true';
			const rankChange = Number(product.rank_change) || 0;
			
			// 랭킹 아이템 요소 생성
			const rankItem = document.createElement('div');
			rankItem.className = 'ranking-item';
			rankItem.dataset.productId = productId;
			
			// 순위 배지 클래스 설정
			let badgeClass = '';
			if (index < 3) badgeClass = 'top-3';
			
			// 랭킹 아이템 내용 설정
			rankItem.innerHTML = 
				'<div class="rank-badge ' + badgeClass + '">' + (index + 1) + '</div>' +
				(isNew ? '<div class="rank-badge new">NEW</div>' : '') +
				(rankChange > 0 ? '<div class="rank-badge up"><i class="fas fa-caret-up"></i>' + rankChange + '</div>' : '') +
				(rankChange < 0 ? '<div class="rank-badge down"><i class="fas fa-caret-down"></i>' + Math.abs(rankChange) + '</div>' : '') +
				'<div class="rank-image">' +
					'<img src="' + imagePath + '"' + 
						' alt="' + title + '"' + 
						' class="rank-item-image"' +
						' onerror="this.onerror=null; this.src=\'' + '<%=request.getContextPath()%>/images/default.jpg' + '\';"' +
					'>' +
				'</div>' +
				'<div class="rank-info">' +
					'<div class="rank-title">' + title + '</div>' +
					'<div class="rank-price">' + formattedDisplayPrice + 
					(currentBid > 0 ? '<span style="font-size: 0.8rem; font-weight: normal; margin-left: 3px; color: #FF9800;"> (현재 입찰가)</span>' : '') + 
					'원</div>' +
				'</div>';
			
			// 클릭 이벤트 추가
			rankItem.addEventListener('click', function() {
				const clickedProductId = this.dataset.productId;
				console.log('상품 클릭:', clickedProductId);
				
				if (!clickedProductId || isNaN(parseInt(clickedProductId, 10))) {
					console.error('유효하지 않은 상품 ID:', clickedProductId);
					alert('유효하지 않은 상품입니다. 다른 상품을 선택해주세요.');
					return;
				}
				
				// 조회수 증가 API 호출
				fetch(contextPath + '/product/rankings?action=view&product_id=' + clickedProductId, {
					method: 'POST',
					headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
				})
				.then(res => console.log('조회수 증가 요청:', res.status))
				.catch(err => console.error('조회수 증가 실패:', err));
				
				// 상품 상세 페이지로 이동
				window.location.href = contextPath + '/buyingproduct?product_id=' + clickedProductId;
			});
			
			// 컨테이너에 추가
			container.appendChild(rankItem);
		});
		
		console.log('랭킹 표시 완료');
	}
	
	// 캐러셀 화살표 설정
	function setupCarouselArrows() {
		const container = document.querySelector('.ranking-items');
		const prevBtn = document.querySelector('.prev-arrow');
		const nextBtn = document.querySelector('.next-arrow');
		
		if (!container || !prevBtn || !nextBtn) {
			console.warn('캐러셀 요소를 찾을 수 없습니다');
			return;
		}
		
		// 화면 크기에 따른 스크롤 아이템 수 계산
		function getVisibleItems() {
			return window.innerWidth < 768 ? 2 : 4;
		}
		
		// 이전 버튼 클릭 (왼쪽으로 스크롤)
		prevBtn.addEventListener('click', function() {
			const items = container.querySelectorAll('.ranking-item');
			if (items.length === 0) return;
			
			const firstItem = items[0];
			const itemWidth = firstItem.offsetWidth + 15; // 마진 포함
			
			container.scrollBy({ 
				left: -itemWidth * getVisibleItems(), 
				behavior: 'smooth' 
			});
		});
		
		// 다음 버튼 클릭 (오른쪽으로 스크롤)
		nextBtn.addEventListener('click', function() {
			const items = container.querySelectorAll('.ranking-item');
			if (items.length === 0) return;
			
			const firstItem = items[0];
			const itemWidth = firstItem.offsetWidth + 15; // 마진 포함
			
			container.scrollBy({ 
				left: itemWidth * getVisibleItems(), 
				behavior: 'smooth' 
			});
		});
	}

	// 가격 필터링 함수
	function applyPriceFilter(priceRange) {
		console.log('가격 필터 적용:', priceRange);
		const productCards = document.querySelectorAll('.product-card');
		
		// 모든 카드를 일단 표시
		productCards.forEach(card => {
			card.classList.remove('hidden');
		});
		
		if (priceRange === 'all') return; // 전체 선택 시 모든 카드 표시
		
		// 가격 범위 파싱
		const [minStr, maxStr] = priceRange.split('-');
		const min = parseInt(minStr, 10) || 0;
		const max = maxStr ? parseInt(maxStr, 10) : Number.MAX_SAFE_INTEGER;
		
		console.log(`가격 범위: ${min} ~ ${max}`);
		
		// 각 카드의 가격을 확인하여 필터링
		productCards.forEach(card => {
			const priceElement = card.querySelector('.product-price');
			if (!priceElement) return;
			
			// 가격 텍스트에서 숫자만 추출 (예: "50,000원" -> 50000)
			const priceText = priceElement.textContent;
			const priceMatch = priceText.match(/[\d,]+/);
			if (!priceMatch) return;
			
			const price = parseInt(priceMatch[0].replace(/,/g, ''), 10);
			console.log(`카드 가격: ${price}, 범위: ${min}-${max}`);
			
			// 가격 범위에 맞지 않으면 숨김
			if (price < min || price > max) {
				card.classList.add('hidden');
			}
		});
	}
	
	// 상품 카드에서 가격 정보 추출 함수
	function extractPrice(card) {
		const priceElement = card.querySelector('.product-price');
		if (!priceElement) return 0;
		
		const priceText = priceElement.textContent;
		const priceMatch = priceText.match(/[\d,]+/);
		if (!priceMatch) return 0;
		
		return parseInt(priceMatch[0].replace(/,/g, ''), 10);
	}
	
	// 카테고리 필터링 함수 복원
	function initCategoryFilter() {
		const productCards = document.querySelectorAll('.product-card');
		
		// 상품 카드에 카테고리 정보 추가 (데이터 속성)
		productCards.forEach(card => {
			// 실제 구현에서는 서버에서 카테고리 정보를 가져와야 함
			if (!card.dataset.category) {
				card.dataset.category = 'all';
			}
		});
		
		// 카테고리에 따라 상품 필터링 함수
		function filterProductsByCategory(category) {
			console.log('카테고리 필터 적용:', category);
			const productCards = document.querySelectorAll('.product-card');
			
			productCards.forEach(card => {
				if (category === 'all' || card.dataset.category === category) {
					card.classList.remove('hidden');
				} else {
					card.classList.add('hidden');
				}
			});
		}

		// 정렬 기능 함수 복원
		function applySortFilter(sortType) {
			console.log('정렬 적용:', sortType);
			const productsContainer = document.querySelector('.products-container');
			if (!productsContainer) return;
			
			const productCards = Array.from(productsContainer.querySelectorAll('.product-card:not(.hidden)'));
			if (productCards.length === 0) return;
			
			// 정렬 전에 모든 카드 제거
			productCards.forEach(card => card.remove());
			
			// 정렬 타입에 따라 카드 정렬
			switch(sortType) {
				case 'priceAsc':
					// 가격 낮은순 정렬
					productCards.sort((a, b) => {
						const priceA = extractPrice(a);
						const priceB = extractPrice(b);
						return priceA - priceB;
					});
					break;
					
				case 'priceDesc':
					// 가격 높은순 정렬
					productCards.sort((a, b) => {
						const priceA = extractPrice(a);
						const priceB = extractPrice(b);
						return priceB - priceA;
					});
					break;
					
				case 'newest':
					// 최신순 정렬 (현재는 DOM 순서를 기준으로 함)
					// 실제로는 서버에서 날짜 정보를 가져와 정렬해야 함
					// 여기서는 기본 순서(역순)로 유지
					productCards.reverse();
					break;
					
				case 'endingSoon':
					// 마감임박순 (현재는 임의 순서)
					// 실제로는 서버에서 경매 종료 시간 정보를 가져와 정렬해야 함
					// 여기서는 기본 순서로 유지
					break;
					
				case 'relevance':
				default:
					// 기본 정렬 (변경 없음)
					break;
			}
			
			// 정렬된 카드를 다시 추가
			productCards.forEach(card => {
				productsContainer.appendChild(card);
			});
		}

		// 상품 상태 필터링 함수 추가
		function filterByStatus(status) {
			console.log('상태 필터 적용:', status);
			const productCards = document.querySelectorAll('.product-card');
			
			productCards.forEach(card => {
				const statusElement = card.querySelector('.product-status');
				
				if (status === 'all') {
					// 전체 선택 시 모든 카드 표시
					card.classList.remove('hidden');
					return;
				}
				
				if (status === 'onsale') {
					// 판매중 상품: product-status가 없거나 '경매 종료'가 아닌 상품
					if (!statusElement || !statusElement.textContent.includes('경매 종료')) {
						card.classList.remove('hidden');
					} else {
						card.classList.add('hidden');
					}
				} else if (status === 'closed') {
					// 경매종료 상품: product-status가 있고 '경매 종료'를 포함하는 상품
					if (statusElement && statusElement.textContent.includes('경매 종료')) {
						card.classList.remove('hidden');
					} else {
						card.classList.add('hidden');
					}
				}
			});
		}
		
		// 임의의 카테고리 할당 함수 (실제 구현에서는 사용하지 않음)
		function getRandomCategory() {
			const categories = ['electronics', 'fashion', 'beauty', 'sports', 'books', 'appliances'];
			return categories[Math.floor(Math.random() * categories.length)];
		}
	}
</script>

<%!
    // 카테고리 ID를 카테고리 코드로 변환하는 헬퍼 메서드
    private String getCategoryCode(String categoryId) {
        if (categoryId == null || categoryId.isEmpty()) {
            return "all";
        }

        // if-else로 변경 (Java 6 호환)
        if ("1".equals(categoryId)) {
            return "electronics"; // 전자기기
        } else if ("2".equals(categoryId)) {
            return "fashion"; // 패션
        } else if ("3".equals(categoryId)) {
            return "beauty"; // 뷰티/미용
        } else if ("4".equals(categoryId)) {
            return "sports"; // 스포츠/레저
        } else if ("5".equals(categoryId)) {
            return "books"; // 도서
        } else if ("6".equals(categoryId)) {
            return "appliances"; // 가전제품
        } else {
            return "all";
        }
    }
%>
</html>