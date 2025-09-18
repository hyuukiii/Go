<%@page import="main.java.Product.ProductDTO"%>
<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관심 상품 목록</title>
<link rel="stylesheet" href="css/interesting.css">
</head>
<body>
	<% ArrayList<ProductDTO> favorites = (ArrayList<ProductDTO>) request.getAttribute("favorites"); %>
	<% String userId = (String) session.getAttribute("user_id"); %>
	
	<h2>관심 상품</h2>

	<div class="product-list">
		<%
		if (favorites == null || favorites.isEmpty()) {
		%>
			<div class="empty-list">
				<div class="empty-message">관심 등록된 상품이 없습니다.</div>
				<div>상품을 찜해보세요!</div>
			</div>
		<%
		} else {
			for (ProductDTO p : favorites) {
		%>
			<div class="product-box" onclick="location.href='buyingproduct?product_id=<%=p.getProduct_id()%>'">
				<img src="<%=request.getContextPath()%>/<%=p.getImage_path() != null ? p.getImage_path() : "images/default.jpg"%>"
					 class="product-image" alt="상품 이미지" />
				<div class="product-info">
					<div class="product-title"><%=p.getTitle()%></div>
					<div class="seller-info">판매자: <%=p.getSeller_id()%></div>
					<div class="price-info"><%=p.getPrice()%>원</div>
				</div>
			</div>
		<%
			}
		}
		%>
	</div>

	<button class="back-button" onclick="location.href='index'">← 메인으로 이동</button>

</body>
</html>
