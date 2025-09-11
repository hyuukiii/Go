<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="Product.ProductDTO"%>
<%@ page import="BidsTransaction.BidDTO"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%
ProductDTO product = (ProductDTO) request.getAttribute("product");
String userId = (String) session.getAttribute("user_id");
Boolean isFavorited = (Boolean) request.getAttribute("isFavorited");
Integer currentBid = (Integer) request.getAttribute("current_bid");
ArrayList<BidDTO> bidHistory = (ArrayList<BidDTO>) request.getAttribute("bidHistory");
Boolean isProductOwner = (Boolean) request.getAttribute("isProductOwner");

// 경매 종료 시간 포맷
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String auctionEndTimeString = product.getAuction_end_time() != null ? sdf.format(product.getAuction_end_time()) : "";
long auctionEndTimeMs = product.getAuction_end_time() != null ? product.getAuction_end_time().getTime() : 0;
%>
<html>
<head>
<title>상품 상세 정보</title>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<style>
.container {
	display: flex;
	justify-content: center;
	margin-top: 40px;
}

.image-section {
	margin-right: 20px;
}

.product-image {
	width: 350px;
	height: 350px;
	object-fit: cover;
	border: 1px solid #ccc;
	margin-bottom: 20px;
}

.info-section {
	max-width: 600px;
}

.category-name {
	font-size: 14px;
	color: gray;
	margin-bottom: 5px;
}

.product-title {
	margin: 5px 0 10px;
}

.price-row {
	display: flex;
	justify-content: space-between;
	font-size: 18px;
	margin: 20px 0;
}

.price-left, .price-right {
	width: 45%;
	text-align: center;
	font-weight: bold;
}

.price-separator {
	width: 10%;
	text-align: center;
}

.current-bid-row {
	font-size: 16px;
	margin-bottom: 10px;
}

.meta-row {
	display: flex;
	justify-content: space-between;
	font-size: 15px;
	color: #555;
}

.description-box {
	margin: 30px 0 20px;
	padding: 20px;
	background-color: #f9f9f9;
	border: 2px solid #000;
}

.bid-section {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-top: 20px;
}

.favorite-btn, .bid-btn, .bid-input {
	height: 42px;
	font-size: 16px;
}

.favorite-btn {
	background: none;
	border: 1px solid lightgray;
	cursor: pointer;
	width: 42px;
	padding: 0;
	font-size: 20px;
}

.bid-input {
	flex-grow: 1;
	padding: 0 10px;
	box-sizing: border-box;
}

.bid-btn {
	padding: 0 15px;
	border: none;
	background-color: black;
	color: white;
	cursor: pointer;
}

.action-buttons {
	display: flex;
	gap: 10px;
	margin-top: 20px;
}

.delete-btn {
	padding: 8px 15px;
	background-color: #ff4136;
	color: white;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}

.delete-btn:hover {
	background-color: #e60000;
}

.countdown-container {
	margin: 15px 0;
	padding: 10px;
	background-color: #f8f9fa;
	border-radius: 5px;
	border: 1px solid #dee2e6;
	text-align: center;
}

.countdown-timer {
	font-size: 18px;
	font-weight: bold;
	color: #dc3545;
}

.countdown-expired {
	color: #dc3545;
	font-weight: bold;
}

.auction-end-time {
	color: #6c757d;
	font-size: 14px;
	margin-top: 5px;
}

/* 입찰 내역 섹션 스타일 */
.bid-history-section {
	margin-top: 30px;
	border: 1px solid #dee2e6;
	border-radius: 5px;
	padding: 15px;
	background-color: #f8f9fa;
}

.bid-history-title {
	font-size: 18px;
	margin-bottom: 15px;
	color: #495057;
	border-bottom: 1px solid #dee2e6;
	padding-bottom: 10px;
}

.bid-history-table {
	width: 100%;
	border-collapse: collapse;
}

.bid-history-table th, .bid-history-table td {
	padding: 8px 12px;
	text-align: left;
	border-bottom: 1px solid #dee2e6;
}

.bid-history-table th {
	background-color: #e9ecef;
	color: #495057;
}

.bid-history-table tr:nth-child(even) {
	background-color: #f8f9fa;
}

.bid-history-table tr:hover {
	background-color: #e9ecef;
}

.bid-history-amount {
	font-weight: bold;
	color: #28a745;
}

.no-bids-message {
	text-align: center;
	padding: 20px;
	color: #6c757d;
	font-style: italic;
}

.winning-bid {
	background-color: #d4edda !important;
}

.winning-label {
	color: #28a745;
	font-weight: bold;
	font-size: 12px;
	margin-left: 5px;
}
</style>
</head>
<body>



	<input type="hidden" id="auctionEndTimeMs"
		value="<%=auctionEndTimeMs%>" />
	<input type="hidden" id="productId"
		value="<%=product.getProduct_id()%>" />

	<div class="container">
		<div class="image-section">
			<img
				src="<%=request.getContextPath()%>/<%=product.getImage_path() != null ? product.getImage_path() : "images/default.jpg"%>"
				alt="대표 이미지" class="product-image"
				onerror="this.src='<%=request.getContextPath()%>/images/default.jpg'">
		</div>

		<div class="info-section">
			<div class="category-name"><%=product.getCategory_name()%></div>
			<h2 class="product-title"><%=product.getTitle()%></h2>

			<div class="price-row">
				<div class="price-left">
					최소입찰가
					<%=product.getMinPrice()%>원
				</div>
				<div class="price-separator">|</div>
				<div class="price-right">
					희망입찰가
					<%=product.getMaxPrice()%>원
				</div>
			</div>

			<div class="current-bid-row">
				현재 입찰가:
				<%=(currentBid != null && currentBid > 0) ? currentBid + "원" : "입찰 없음"%>
			</div>

			<div id="countdown-container" class="countdown-container">
				<%
				if (!"판매완료".equals(product.getStatus()) && !"경매종료".equals(product.getStatus())) {
				%>
				<div>남은 경매 시간:</div>
				<div id="countdown" class="countdown-timer">로딩 중...</div>
				<div class="auction-end-time">
					경매 종료:
					<%=auctionEndTimeString%>
				</div>
				<%
				} else {
				%>
				<div class="countdown-expired">경매가 종료되었습니다!</div>
				<%
				}
				%>
			</div>

			<div class="meta-row">
				<div>
					조회수:
					<%=product.getView_count()%>회
				</div>
				<div>
					등록 날짜:
					<%=product.getRegister_date() != null ? sdf.format(product.getRegister_date()) : ""%>
				</div>
			</div>

			<div class="description-box">
				<%=product.getDescription()%>
			</div>

			<div class="bid-section">
				<%
				if (userId != null) {
				%>
				<%
				if ("판매완료".equals(product.getStatus()) || "경매종료".equals(product.getStatus())) {
				%>
				<p style="font-weight: bold; color: red;">경매가 종료된 물품입니다.</p>
				<%
				} else {
				%>
				<form action="favorite" method="post">
					<input type="hidden" name="product_id"
						value="<%=product.getProduct_id()%>" />
					<button type="submit" class="favorite-btn">
						<%=(isFavorited != null && isFavorited) ? "♡" : "찜"%>
					</button>
				</form>

				<form action="bid" method="post" class="bid-form">
					<input type="hidden" name="product_id"
						value="<%=product.getProduct_id()%>" /> <input type="number"
						name="bid_price" class="bid-input" placeholder="입찰 가격 입력" required />
					<button type="submit" class="bid-btn">입찰하기</button>
				</form>
				<%
				}
				%>
				<%
				} else {
				%>
				<p>로그인 후 입찰 및 찜이 가능합니다.</p>
				<%
				}
				%>
			</div>

			<%
			if (isProductOwner) {
			%>
			<div class="action-buttons">
				<button class="delete-btn" onclick="confirmDelete()">상품 삭제</button>
			</div>
			<script>
 		    function confirmDelete() {
        		if (confirm('정말로 이 상품을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
            		window.location.href = 'deleteproduct?product_id=<%=product.getProduct_id()%>
				';
					}
				}
			</script>
			<%
			}
			%>

			<!-- 입찰 내역 섹션 -->
			<div class="bid-history-section">
				<h3 class="bid-history-title">입찰 내역</h3>
				<%
				if (bidHistory != null && !bidHistory.isEmpty()) {
				%>
				<table class="bid-history-table">
					<thead>
						<tr>
							<th>입찰자</th>
							<th>입찰 금액</th>
							<th>입찰 시간</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<%
						for (BidDTO bid : bidHistory) {
						%>
						<tr
							class="<%="Y".equals(bid.getIs_winning()) ? "winning-bid" : ""%>">
							<td><%=bid.getUser_id().equals(userId) ? "나" : bid.getUsername()%>
							</td>
							<td class="bid-history-amount"><%=bid.getBid_amount()%>원</td>
							<td><%=bid.getBid_time() != null ? sdf.format(bid.getBid_time()) : ""%>
							</td>
							<td>
								<%
								if ("Y".equals(bid.getIs_winning())) {
								%> <span class="winning-label">낙찰</span> <%
 }
 %>
							</td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
				<%
				} else {
				%>
				<p class="no-bids-message">아직 입찰 내역이 없습니다.</p>
				<%
				}
				%>
			</div>

		</div>
	</div>
	
	<script>
		// 경매 종료 시간을 히든 필드에서 가져옴
		var countDownDate = parseInt(document
				.getElementById("auctionEndTimeMs").value);
		var productId = document.getElementById("productId").value;

		// 1초마다 카운트다운 업데이트
		var countdownTimer = setInterval(
				function() {
					// 현재 시간
					var now = new Date().getTime();

					// 남은 시간 계산
					var distance = countDownDate - now;

					// 시간 계산
					var days = Math.floor(distance / (1000 * 60 * 60 * 24));
					var hours = Math.floor((distance % (1000 * 60 * 60 * 24))
							/ (1000 * 60 * 60));
					var minutes = Math.floor((distance % (1000 * 60 * 60))
							/ (1000 * 60));
					var seconds = Math.floor((distance % (1000 * 60)) / 1000);

					// 카운트다운 표시
					var countdownDisplay = "";
					if (days > 0)
						countdownDisplay += days + "일 ";
					if (hours > 0 || days > 0)
						countdownDisplay += hours + "시간 ";
					if (minutes > 0 || hours > 0 || days > 0)
						countdownDisplay += minutes + "분 ";
					countdownDisplay += seconds + "초";

					var countdownEl = document.getElementById("countdown");
					if (countdownEl) {
						countdownEl.innerHTML = countdownDisplay;
					}

					// 경매가 끝나면 카운트다운 중지 및 메시지 변경
					if (distance < 0) {
						clearInterval(countdownTimer);
						var containerEl = document
								.getElementById("countdown-container");
						if (containerEl) {
							containerEl.innerHTML = "<div class='countdown-expired'>경매가 종료되었습니다!</div>";
						}

						// 경매 종료 후 입찰 폼 비활성화
						var bidForm = document.querySelector(".bid-form");
						var bidInput = document.querySelector(".bid-input");
						var bidBtn = document.querySelector(".bid-btn");

						if (bidForm && bidInput && bidBtn) {
							bidInput.disabled = true;
							bidBtn.disabled = true;
							bidBtn.style.backgroundColor = "#6c757d";
						}

						// 서버에 경매 종료 상태 업데이트 요청
						fetch('auctionend?product_id=' + productId, {
							method : 'POST'
						});
					}
				}, 1000);
	</script>
</body>
</html>
