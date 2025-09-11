<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>상품 등록</title>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 20px;
    }
    
    h2 {
        color: #333;
    }
    
    .form-container {
        max-width: 600px;
        padding: 20px;
        border: 1px solid #ddd;
        border-radius: 5px;
        margin-bottom: 20px;
    }
    
    .form-group {
        margin-bottom: 15px;
    }
    
    label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    
    input[type="text"], 
    input[type="number"],
    textarea,
    select {
        width: 100%;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-sizing: border-box;
    }
    
    textarea {
        height: 100px;
    }
    
    input[type="submit"],
    button {
        background-color: #4CAF50;
        color: white;
        padding: 10px 15px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }
    
    input[type="submit"]:hover,
    button:hover {
        background-color: #45a049;
    }
    
    .time-selection {
        display: flex;
        gap: 10px;
    }
    
    .time-selection select {
        flex: 1;
    }
    
    .help-text {
        font-size: 0.8em;
        color: #666;
        margin-top: 3px;
    }
    
    .error-message {
        color: #d9534f;
        font-size: 0.9em;
        margin-top: 5px;
        display: none;
    }
    
    .server-error {
        background-color: #f8d7da;
        color: #721c24;
        padding: 10px;
        margin-bottom: 15px;
        border: 1px solid #f5c6cb;
        border-radius: 4px;
    }
</style>
<script>
    function validateForm() {
        var hours = document.getElementById("auction_hours").value;
        var minutes = document.getElementById("auction_minutes").value;
        var seconds = document.getElementById("auction_seconds").value;
        
        if (hours == "0" && minutes == "0" && seconds == "0") {
            document.getElementById("time-error").style.display = "block";
            return false;
        }
        
        return true;
    }
</script>
</head>
<body>
	<h2>상품 등록하기</h2>

	<div class="form-container">
	    <c:if test="${not empty errorMessage}">
	        <div class="server-error">${errorMessage}</div>
	    </c:if>
	    
		<form action="registerproduct" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">
			<input type="hidden" name="product_id" value="${product.product_id}" />
			
			<div class="form-group">
				<label for="title">상품 이름:</label>
				<input type="text" id="title" name="title" value="${title}" required />
			</div>
			
			<div class="form-group">
				<label for="category">카테고리:</label>
				<select id="category" name="category" required>
					<option value="">카테고리 선택</option>
					<c:forEach var="category" items="${categories}">
						<option value="${category.categoryId}" ${category.categoryId eq selectedCategory ? 'selected' : ''}>${category.categoryName}</option>
					</c:forEach>
				</select>
			</div>
			
			<div class="form-group">
				<label for="description">상세 내용:</label>
				<textarea id="description" name="description" required>${description}</textarea>
			</div>
			
			<div class="form-group">
				<label for="maxprice">입찰 희망가:</label>
				<input type="number" id="maxprice" name="maxprice" min="0" value="${maxprice}" required />
			</div>
			
			<div class="form-group">
				<label for="minprice">최소 입찰가:</label>
				<input type="number" id="minprice" name="minprice" min="0" value="${minprice}" required />
			</div>
			
			<div class="form-group">
				<label>경매 지속 시간 (필수):</label>
				<div class="time-selection">
					<select id="auction_hours" name="auction_hours">
						<option value="0">0시간</option>
						<option value="1">1시간</option>
						<option value="2">2시간</option>
						<option value="3">3시간</option>
						<option value="6">6시간</option>
						<option value="12">12시간</option>
						<option value="24">24시간</option>
						<option value="48">48시간</option>
					</select>
					<select id="auction_minutes" name="auction_minutes">
						<option value="0">0분</option>
						<option value="1">1분</option>
						<option value="5">5분</option>
						<option value="10">10분</option>
						<option value="15">15분</option>
						<option value="30">30분</option>
						<option value="45">45분</option>
					</select>
					<select id="auction_seconds" name="auction_seconds">
						<option value="0">0초</option>
						<option value="10">10초</option>
						<option value="30">30초</option>
						<option value="45">45초</option>
					</select>
				</div>
				<div class="help-text">경매 시간이 지나면 자동으로 경매가 종료됩니다.</div>
				<div id="time-error" class="error-message">경매 시간을 선택해주세요.</div>
			</div>
			
			<div class="form-group">
				<label for="image">이미지:</label>
				<input type="file" id="image" name="image" accept="image/*">
			</div>

			<input type="submit" value="상품 등록">
		</form>
	</div>
	
	<form action="index" method="get">
		<button type="submit">← 메인으로 이동</button>
	</form>
</body>
</html>
