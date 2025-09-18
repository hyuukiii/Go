<%@page import="main.java.Product.ProductDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
ArrayList<ProductDTO> searchResults = (ArrayList<ProductDTO>) request.getAttribute("searchResults");
String keyword = (String) request.getAttribute("keyword");
String category = (String) request.getAttribute("category");
String price = (String) request.getAttribute("price");
String status = (String) request.getAttribute("status");
String sort = (String) request.getAttribute("sort");
int currentPage = (Integer) request.getAttribute("currentPage");
int totalPages = (Integer) request.getAttribute("totalPages");
int totalItems = (Integer) request.getAttribute("totalItems");
String userId = (String) session.getAttribute("user_id");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>검색 결과 - <%=keyword != null ? keyword : "전체 상품"%></title>
<!-- Font Awesome CDN 추가 -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<style>
body {
	font-family: 'Arial', sans-serif;
	margin: 0;
	padding: 0;
	background-color: #f9f9f9;
}

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

.content {
	max-width: 1200px;
	margin: 0 auto;
	padding: 20px;
}

.search-header {
	margin-bottom: 20px;
}

.search-title {
	font-size: 24px;
	margin-bottom: 10px;
}

.search-summary {
	color: #6c757d;
	margin-bottom: 20px;
}

.search-filters {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-bottom: 20px;
	padding: 15px;
	background-color: #f8f9fa;
	border-radius: 8px;
}

.active-filter {
	background-color: #e9ecef;
	border-radius: 20px;
	padding: 5px 12px;
	font-size: 14px;
	display: flex;
	align-items: center;
}

.remove-filter {
	margin-left: 5px;
	color: #6c757d;
	cursor: pointer;
	font-size: 12px;
}

.filter-divider {
	color: #6c757d;
	margin: 0 5px;
}

.search-results {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
	gap: 20px;
}

.product-card {
	background-color: #fff;
	border-radius: 8px;
	overflow: hidden;
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
}

.product-info {
	padding: 15px;
}

.product-title {
	font-weight: bold;
	margin-bottom: 10px;
	height: 40px;
	overflow: hidden;
}

.product-price {
	color: #e44d26;
	font-weight: bold;
	margin-bottom: 10px;
}

.product-status {
	background-color: #f8d7da;
	color: #721c24;
	display: inline-block;
	padding: 2px 8px;
	border-radius: 4px;
	font-size: 12px;
	margin-top: 5px;
}

.empty-results {
	text-align: center;
	padding: 50px 0;
	color: #6c757d;
}

.empty-results-icon {
	font-size: 48px;
	margin-bottom: 20px;
	color: #adb5bd;
}

.pagination {
	display: flex;
	justify-content: center;
	margin-top: 30px;
	list-style-type: none;
	padding: 0;
}

.pagination li {
	margin: 0 5px;
}

.pagination a {
	display: block;
	padding: 8px 12px;
	background-color: #fff;
	color: #007bff;
	text-decoration: none;
	border-radius: 4px;
	border: 1px solid #dee2e6;
}

.pagination a:hover {
	background-color: #e9ecef;
}

.pagination .active a {
	background-color: #007bff;
	color: #fff;
	border-color: #007bff;
}

.pagination .disabled a {
	color: #6c757d;
	pointer-events: none;
	background-color: #fff;
	border-color: #dee2e6;
}

/* 검색 폼 스타일 */
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

.sort-options {
	display: flex;
	justify-content: flex-end;
	margin-bottom: 15px;
}

.sort-option {
	padding: 5px 10px;
	margin-left: 10px;
	background-color: transparent;
	border: 1px solid #ddd;
	border-radius: 4px;
	color: #333;
	cursor: pointer;
}

.sort-option.active {
	background-color: #007bff;
	color: white;
	border-color: #007bff;
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

			<!-- 검색 기능 -->
			<div class="search-container">
				<form action="search" method="get" class="search-form">
					<input type="text" name="keyword" class="search-input"
						placeholder="찾고 싶은 상품을 검색해보세요"
						value="<%=keyword != null ? keyword : ""%>">
					<button type="submit" class="search-btn">
						<i class="fas fa-search"></i>
					</button>
				</form>
			</div>

			<div class="header-right">
				<%
				if (userId != null) {
				%>
				<span class="welcome-msg"><%=userId%>님 환영합니다</span> <a href="mypage"
					class="header-btn">마이페이지</a> <a href="receivedreviews"
					class="header-btn">받은 리뷰 보기</a> <a href="reviewform"
					class="header-btn">리뷰 작성하기</a>
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
	</div>

	<div class="content">
		<div class="search-header">
			<h1 class="search-title">
				<%
				if (keyword != null && !keyword.trim().isEmpty()) {
				%>
				"<%=keyword%>" 검색 결과
				<%
				} else {
				%>
				전체 상품 검색
				<%
				}
				%>
			</h1>
			<div class="search-summary">
				<%
				if (totalItems > 0) {
				%>
				총
				<%=totalItems%>개의 상품이 검색되었습니다.
				<%
				} else {
				%>
				검색 결과가 없습니다.
				<%
				}
				%>
			</div>

			<!-- 활성화된 필터 표시 -->
			<%
			if (category != null || price != null || status != null || sort != null) {
			%>
			<div class="search-filters">
				<%
				if (category != null && !category.equals("all") && !category.trim().isEmpty()) {
				%>
				<div class="active-filter">
					카테고리:
					<%
				String categoryName = category;
				if (category.equals("electronics"))
					categoryName = "전자기기";
				else if (category.equals("fashion"))
					categoryName = "패션";
				else if (category.equals("books"))
					categoryName = "도서";
				else if (category.equals("beauty"))
					categoryName = "뷰티/미용";
				else if (category.equals("sports"))
					categoryName = "스포츠/레저";
				else if (category.equals("appliances"))
					categoryName = "가전제품";
				%>
					<%=categoryName%>
					<a
						href="search?keyword=<%=keyword != null ? keyword : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>"
						class="remove-filter"> <i class="fas fa-times"></i>
					</a>
				</div>
				<%
				}
				%>

				<%
				if (price != null && !price.trim().isEmpty()) {
				%>
				<div class="active-filter">
					가격:
					<%
				String priceText = "";
				if (price.equals("0-50000"))
					priceText = "5만원 이하";
				else if (price.equals("50000-100000"))
					priceText = "5만원~10만원";
				else if (price.equals("100000-300000"))
					priceText = "10만원~30만원";
				else if (price.equals("300000-"))
					priceText = "30만원 이상";
				%>
					<%=priceText%>
					<a
						href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>"
						class="remove-filter"> <i class="fas fa-times"></i>
					</a>
				</div>
				<%
				}
				%>

				<%
				if (status != null && !status.trim().isEmpty()) {
				%>
				<div class="active-filter">
					상태:
					<%
				String statusText = "";
				if (status.equals("onsale"))
					statusText = "판매중";
				else if (status.equals("closed"))
					statusText = "경매종료";
				%>
					<%=statusText%>
					<a
						href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&sort=<%=sort != null ? sort : ""%>"
						class="remove-filter"> <i class="fas fa-times"></i>
					</a>
				</div>
				<%
				}
				%>

				<%
				if (sort != null && !sort.trim().isEmpty() && !sort.equals("relevance")) {
				%>
				<div class="active-filter">
					정렬:
					<%
				String sortText = "";
				if (sort.equals("priceAsc"))
					sortText = "가격 낮은순";
				else if (sort.equals("priceDesc"))
					sortText = "가격 높은순";
				else if (sort.equals("newest"))
					sortText = "최신순";
				else if (sort.equals("endingSoon"))
					sortText = "마감임박순";
				%>
					<%=sortText%>
					<a
						href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>"
						class="remove-filter"> <i class="fas fa-times"></i>
					</a>
				</div>
				<%
				}
				%>

				<%
				if ((category != null && !category.equals("all") && !category.trim().isEmpty())
						|| (price != null && !price.trim().isEmpty()) || (status != null && !status.trim().isEmpty())
						|| (sort != null && !sort.trim().isEmpty() && !sort.equals("relevance"))) {
				%>
				<a href="search?keyword=<%=keyword != null ? keyword : ""%>"
					class="active-filter"
					style="background-color: #f8d7da; color: #721c24;"> 모든 필터 초기화 <i
					class="fas fa-times"></i>
				</a>
				<%
				}
				%>
			</div>
			<%
			}
			%>

			<!-- 정렬 옵션 -->
			<div class="sort-options">
				<a
					href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=relevance"
					class="sort-option <%=(sort == null || sort.equals("relevance")) ? "active" : ""%>">관련성순</a>
				<a
					href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=priceAsc"
					class="sort-option <%=(sort != null && sort.equals("priceAsc")) ? "active" : ""%>">가격
					낮은순</a> <a
					href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=priceDesc"
					class="sort-option <%=(sort != null && sort.equals("priceDesc")) ? "active" : ""%>">가격
					높은순</a> <a
					href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=newest"
					class="sort-option <%=(sort != null && sort.equals("newest")) ? "active" : ""%>">최신순</a>
				<a
					href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=endingSoon"
					class="sort-option <%=(sort != null && sort.equals("endingSoon")) ? "active" : ""%>">마감임박순</a>
			</div>
		</div>

		<%
		if (searchResults != null && !searchResults.isEmpty()) {
		%>
		<div class="search-results">
			<%
			for (ProductDTO product : searchResults) {
			%>
			<div class="product-card">
				<a href="buyingproduct?product_id=<%=product.getProduct_id()%>">
					<img
					src="<%=request.getContextPath()%>/<%=product.getImage_path() != null ? product.getImage_path() : "images/default.jpg"%>"
					alt="<%=product.getTitle()%>" class="product-image">
				</a>
				<div class="product-info">
					<div class="product-title"><%=product.getTitle()%></div>
					<div class="product-price"><%=product.getPrice()%>원
					</div>
					<%
					if ("경매종료".equals(product.getStatus())) {
					%>
					<div class="product-status">경매 종료</div>
					<%
					}
					%>
				</div>
			</div>
			<%
			}
			%>
		</div>

		<!-- 페이지네이션 -->
		<%
		if (totalPages > 1) {
		%>
		<ul class="pagination">
			<li class="<%=currentPage == 1 ? "disabled" : ""%>"><a
				href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>&page=<%=currentPage - 1%>">
					<i class="fas fa-chevron-left"></i>
			</a></li>

			<%
			int startPage = Math.max(1, currentPage - 2);
			int endPage = Math.min(totalPages, startPage + 4);

			if (startPage > 1) {
			%>
			<li><a
				href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>&page=1">1</a></li>
			<%
			if (startPage > 2) {
			%>
			<li class="disabled"><a>...</a></li>
			<%
			}
			%>
			<%
			}
			%>

			<%
			for (int i = startPage; i <= endPage; i++) {
			%>
			<li class="<%=i == currentPage ? "active" : ""%>"><a
				href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>&page=<%=i%>"><%=i%></a>
			</li>
			<%
			}
			%>

			<%
			if (endPage < totalPages) {
				if (endPage < totalPages - 1) {
			%>
			<li class="disabled"><a>...</a></li>
			<%
			}
			%>
			<li><a
				href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>&page=<%=totalPages%>"><%=totalPages%></a></li>
			<%
			}
			%>

			<li class="<%=currentPage == totalPages ? "disabled" : ""%>"><a
				href="search?keyword=<%=keyword != null ? keyword : ""%>&category=<%=category != null ? category : ""%>&price=<%=price != null ? price : ""%>&status=<%=status != null ? status : ""%>&sort=<%=sort != null ? sort : ""%>&page=<%=currentPage + 1%>">
					<i class="fas fa-chevron-right"></i>
			</a></li>
		</ul>
		<%
		}
		%>
		<%
		} else {
		%>
		<div class="empty-results">
			<div class="empty-results-icon">
				<i class="fas fa-search"></i>
			</div>
			<h2>검색 결과가 없습니다</h2>
			<p>다른 검색어를 입력하거나 필터를 변경해 보세요.</p>
			<a href="index" class="header-btn"
				style="display: inline-block; margin-top: 20px;">메인으로 돌아가기</a>
		</div>
		<%
		}
		%>
	</div>

	<!-- 푸터 -->
	<div class="footer">
		<div class="footer-content">
			<p>&copy; 2024 옥션 플랫폼. All rights reserved.</p>
			<div class="footer-links">
				<a href="#">이용약관</a> | <a href="#">개인정보처리방침</a> | <a href="#">고객센터</a>
				<%
				if (userId != null) {
				%>
				| <a href="receivedreviews">받은 리뷰 보기</a> | <a href="reviewform">리뷰
					작성하기</a>
				<%
				}
				%>
			</div>
		</div>
	</div>
</body>
</html>
