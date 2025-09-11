<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,Product.ProductDTO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<html>
<head>
<title>리뷰 작성 대상 목록</title>
<style>
body {
	font-family: 'Noto Sans KR', sans-serif;
	padding: 30px;
	background-color: #f5f5f5;
	max-width: 800px;
	margin: 0 auto;
}

h2 {
	text-align: center;
	margin-bottom: 30px;
	color: #333;
}

.product-list {
	max-width: 600px;
	margin: 0 auto;
}

.product-box {
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
	padding: 20px;
	margin-bottom: 15px;
	transition: 0.3s;
	display: flex;
	align-items: center;
}

.product-box:hover {
	background-color: #e8f4ff;
	transform: translateY(-3px);
	cursor: pointer;
}

.product-image {
	width: 80px;
	height: 80px;
	object-fit: cover;
	border-radius: 8px;
	margin-right: 15px;
}

.product-info {
	flex-grow: 1;
}

.product-title {
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 5px;
}

.seller-info {
	font-size: 14px;
	color: #666;
	margin-bottom: 5px;
}

.price-info {
	font-weight: bold;
	color: #e44d26;
}

.review-button {
	background-color: #4CAF50;
	color: white;
	border: none;
	border-radius: 5px;
	padding: 8px 15px;
	margin-left: 10px;
	cursor: pointer;
	transition: 0.2s;
}

.review-button:hover {
	background-color: #3e8e41;
}

.back-button {
	background-color: #f0f0f0;
	color: #333;
	border: 1px solid #ddd;
	border-radius: 5px;
	padding: 10px 20px;
	cursor: pointer;
	transition: 0.2s;
	display: block;
	margin: 20px auto;
	font-weight: bold;
}

.back-button:hover {
	background-color: #e0e0e0;
}

.empty-list {
	text-align: center;
	padding: 40px;
	background: #fff;
	border-radius: 12px;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.empty-message {
	color: #777;
	font-size: 16px;
	margin-bottom: 20px;
}
</style>
</head>
<body>
	<h2>리뷰 작성할 수 있는 상품</h2>
	<div class="product-list">
		<c:if test="${empty winningProducts}">
			<div class="empty-list">
				<div class="empty-message">리뷰를 작성할 수 있는 낙찰 상품이 없습니다.</div>
				<div>낙찰받은 상품이 있다면 리뷰를 작성해 보세요!</div>
			</div>
		</c:if>
		<c:forEach var="product" items="${winningProducts}">
			<div class="product-box"
				onclick="location.href='reviewform?product_id=${product.product_id}'">
				<img src="${pageContext.request.contextPath}/${product.image_path != null ? product.image_path : 'images/default.jpg'}" 
					alt="상품 이미지" class="product-image">
				<div class="product-info">
				<div class="product-title">${product.title}</div>
					<div class="seller-info">판매자: ${product.seller_id}</div>
					<div class="price-info">${product.price}원</div>
				</div>
				<button class="review-button">리뷰 작성</button>
			</div>
		</c:forEach>
	</div>
	<button class="back-button" onclick="location.href='index'">← 메인으로 이동</button>
</body>
</html>
