<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="Product.ProductDTO"%>
<%
ProductDTO product = (ProductDTO) request.getAttribute("product");
if (product == null) {
    response.sendRedirect("reviewlist");
    return;
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>리뷰 작성</title>
<style>
body {
	font-family: 'Noto Sans KR', sans-serif;
	background-color: #f5f5f5;
	max-width: 800px;
	margin: 0 auto;
	padding: 30px;
}

h2 {
	text-align: center;
	margin-bottom: 30px;
	color: #333;
}

.review-container {
	background: #fff;
	padding: 30px;
	border-radius: 12px;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.product-info {
	display: flex;
	align-items: center;
	margin-bottom: 25px;
	padding-bottom: 20px;
	border-bottom: 1px solid #eee;
}

.product-image {
	width: 100px;
	height: 100px;
	object-fit: cover;
	border-radius: 8px;
	margin-right: 20px;
}

.product-details {
	flex-grow: 1;
}

.product-title {
	font-size: 20px;
	font-weight: bold;
	margin-bottom: 5px;
}

.seller-info {
	color: #666;
	margin-bottom: 5px;
}

.price-info {
	font-weight: bold;
	color: #e44d26;
}

.form-group {
	margin-bottom: 20px;
}

label {
	display: block;
	margin-bottom: 8px;
	font-weight: bold;
	color: #444;
}

.star-rating {
	display: flex;
	flex-direction: row-reverse;
	margin-bottom: 15px;
}

.star-rating input {
	display: none;
}

.star-rating label {
	cursor: pointer;
	width: 30px;
	height: 30px;
	font-size: 30px;
	color: #ddd;
}

.star-rating label:before {
	content: "★";
}

.star-rating input:checked ~ label {
	color: gold;
}

.star-rating label:hover,
.star-rating label:hover ~ label {
	color: gold;
}

textarea {
	width: 100%;
	padding: 12px;
	border: 1px solid #ddd;
	border-radius: 8px;
	resize: vertical;
	min-height: 150px;
	font-family: inherit;
}

.submit-button {
	background-color: #4CAF50;
	color: white;
	border: none;
	border-radius: 5px;
	padding: 12px 20px;
	font-size: 16px;
	cursor: pointer;
	transition: 0.2s;
	display: block;
	width: 100%;
	margin-top: 10px;
}

.submit-button:hover {
	background-color: #3e8e41;
}

.cancel-button {
	background-color: #f0f0f0;
	color: #333;
	border: 1px solid #ddd;
	border-radius: 5px;
	padding: 12px 20px;
	font-size: 16px;
	cursor: pointer;
	transition: 0.2s;
	display: block;
	width: 100%;
	margin-top: 10px;
	text-align: center;
	text-decoration: none;
}

.cancel-button:hover {
	background-color: #e0e0e0;
}

.button-group {
	display: flex;
	gap: 15px;
	margin-top: 20px;
}

.button-group .submit-button, 
.button-group .cancel-button {
	flex: 1;
	margin-top: 0;
}
</style>
</head>
<body>
		<h2>리뷰 작성</h2>
	<div class="review-container">
		<div class="product-info">
			<img src="${pageContext.request.contextPath}/<%= product.getImage_path() != null ? product.getImage_path() : "images/default.jpg" %>" 
				 alt="상품 이미지" class="product-image">
			<div class="product-details">
				<div class="product-title"><%= product.getTitle() %></div>
				<div class="seller-info">판매자: <%= product.getSeller_id() %></div>
				<div class="price-info"><%= product.getPrice() %>원</div>
			</div>
		</div>
		
		<form action="submitreview" method="post">
			<input type="hidden" name="product_id" value="<%= product.getProduct_id() %>">
			<input type="hidden" name="seller_id" value="<%= product.getSeller_id() %>">

			<div class="form-group">
				<label>별점</label>
				<div class="star-rating">
					<input type="radio" id="star5" name="rating" value="5.0" checked>
					<label for="star5"></label>
					<input type="radio" id="star4" name="rating" value="4.0">
					<label for="star4"></label>
					<input type="radio" id="star3" name="rating" value="3.0">
					<label for="star3"></label>
					<input type="radio" id="star2" name="rating" value="2.0">
					<label for="star2"></label>
					<input type="radio" id="star1" name="rating" value="1.0">
					<label for="star1"></label>
				</div>
			</div>
			
			<div class="form-group">
				<label for="description">리뷰 내용</label>
				<textarea id="description" name="description" required placeholder="이 상품과 판매자에 대한 솔직한 평가를 남겨주세요."></textarea>
			</div>
			
			<div class="button-group">
				<button type="submit" class="submit-button">리뷰 등록</button>
				<a href="reviewlist" class="cancel-button">취소</a>
			</div>
		</form>
	</div>
</body>
</html>
