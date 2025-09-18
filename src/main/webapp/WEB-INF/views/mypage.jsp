<%@page import="main.java.User.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" href="css/mypage.css">

</head>
<body>
	<%
	UserDTO user = (UserDTO) request.getAttribute("user");
	%>
	<%
	String userId = (String) session.getAttribute("user_id");
	String statusMessage = (String) request.getAttribute("statusMessage");
	%>


	<div class="layout-wrapper">

		<div class="sidebar">
			<h2>마이 페이지</h2>
			<div class="section">
				<h3>거래 정보</h3>
				<ul>
					<li><a href="registerproduct">상품 등록</a></li>
					<li><a href="reviewform">리뷰 작성</a></li>
					<li><a href="viewInterest">관심 상품</a></li>
				</ul>
			</div>

			<div class="section">
				<h3>내 정보</h3>
				<ul>
					<li><a href="${pageContext.request.contextPath}/mypage">프로필
							정보</a></li>
					<li><a
						href="${pageContext.request.contextPath}/profileManager">프로필
							관리</a></li>
					<li><a href="receivedreviews">리뷰 보기</a></li>
				</ul>
			</div>
		</div>

		<!-- 메인 내용 -->
		<div class="mypage-container">
			<div class="profile-box">
				<div class="profile-image">
					<img src="${user.profile_image}" width="70" height="70"
						style="border-radius: 50%;" />
				</div>
				<div class="profile-info">
					<div class="user_id"><%=user.getUser_id()%></div>
					<div class="content">
						가입일 :
						<%=user.getJoin_date()%><br> Rating :
						<%=user.getRating()%>
					</div>
				</div>
				<a href="${pageContext.request.contextPath}/profileManager"
					class="btn">프로필 관리</a>
			</div>

			<div class="profile">
				<h4>프로필 정보</h4>
				<h5>아이디</h5>
				<div class="info"><%=user.getUser_id()%></div>
				<h5>이름</h5>
				<div class="info"><%=user.getUsername()%></div>
			</div>

			<div class="myInfo">
				<h4>내 계정</h4>
				<h5>이메일</h5>
				<div class="info"><%=user.getEmail()%></div>
				<h5>비밀번호</h5>
				<div class="info password-box">
					<button type="button" id="togglePassword"
						style="border: none; background: none; cursor: pointer;">
						🔒</button>
					<span id="maskedPassword">****</span> <span id="realPassword"
						style="display: none;"><%=user.getPassword()%></span>

				</div>
			</div>

			<div class="privateInfo">
				<h4>개인 정보</h4>
				<h5>전화번호</h5>
				<div class="info"><%=user.getPhone()%></div>
				<h5>주소</h5>
				<div class="info"><%=user.getAddress()%></div>
			</div>

			<div class="section-container">
				<div class="section">
					<h3>📌 입찰중</h3>
					<div class="product-list">
						<c:forEach var="item" items="${biddingList}">
							<div class="product-item">
								<a href="buyingproduct?product_id=${item.product_id}">${item.title}</a>
							</div>
						</c:forEach>
					</div>
				</div>

				<div class="section">
					<h3>✅ 낙찰</h3>
					<div class="product-list">
						<c:forEach var="item" items="${wonList}">
							<div class="product-item">
								<a href="buyingproduct?product_id=${item.product_id}">${item.title}</a>
							</div>
						</c:forEach>
					</div>
				</div>

				<div class="section">
					<h3>📦 판매중</h3>
					<div class="product-list">
						<c:forEach var="item" items="${sellingList}">
							<div class="product-item">
								<a href="buyingproduct?product_id=${item.product_id}">${item.title}</a>
							</div>
						</c:forEach>
					</div>
				</div>

				<div class="section">
					<h3>🛑 판매완료</h3>
					<div class="product-list">
						<c:forEach var="item" items="${soldList}">
							<div class="product-item">
								<a href="buyingproduct?product_id=${item.product_id}">${item.title}</a>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>


		</div>
	</div>
	<script>
		const toggleBtn = document.getElementById("togglePassword");
		const masked = document.getElementById("maskedPassword");
		const real = document.getElementById("realPassword");

		toggleBtn.addEventListener("mouseenter", function() {
			masked.style.display = "none";
			real.style.display = "inline";
			this.textContent = "🔓";
		});

		toggleBtn.addEventListener("mouseleave", function() {
			masked.style.display = "inline";
			real.style.display = "none";
			this.textContent = "🔒";
		});
	</script>
</body>
</html>