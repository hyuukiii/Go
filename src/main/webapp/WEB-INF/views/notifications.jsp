<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="Notification.NotificationDTO"%>
<%
List<NotificationDTO> notifications = (List<NotificationDTO>) request.getAttribute("notifications");
String userId = (String) session.getAttribute("user_id");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>알림 목록</title>
<!-- Font Awesome CDN 추가 -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
<style>
* {
	box-sizing: border-box;
	margin: 0;
	padding: 0;
	font-family: 'Noto Sans KR', sans-serif;
}

body {
	background-color: #f8f9fa;
	color: #333;
	line-height: 1.6;
}

.container {
	max-width: 800px;
	margin: 0 auto;
	padding: 20px;
}

header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20px 0;
	border-bottom: 1px solid #e0e0e0;
	margin-bottom: 20px;
}

h1 {
	font-size: 24px;
	color: #333;
	margin-bottom: 20px;
}

.notification-list {
	background-color: white;
	border-radius: 8px;
	overflow: hidden;
	box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.notification-item {
	padding: 15px 20px;
	border-bottom: 1px solid #f0f0f0;
	position: relative;
	transition: background-color 0.2s;
	display: flex;
	align-items: center;
}

.notification-item:hover {
	background-color: #f5f5f5;
}

.notification-item:last-child {
	border-bottom: none;
}

.notification-content {
	flex-grow: 1;
}

.notification-time {
	font-size: 12px;
	color: #999;
	margin-top: 5px;
}

.notification-unread {
	background-color: #f0f7ff;
}

.notification-unread::before {
	content: "";
	position: absolute;
	left: 8px;
	top: 50%;
	transform: translateY(-50%);
	width: 8px;
	height: 8px;
	background-color: #4a90e2;
	border-radius: 50%;
}

.notification-actions {
	display: flex;
	gap: 10px;
}

.notification-actions button {
	background: none;
	border: none;
	cursor: pointer;
	color: #666;
	transition: color 0.2s;
	padding: 5px;
}

.notification-actions button:hover {
	color: #333;
}

.notification-type {
	padding: 3px 8px;
	border-radius: 12px;
	font-size: 12px;
	margin-right: 10px;
	display: inline-block;
}

.type-bid {
	background-color: #e3f2fd;
	color: #1976d2;
}

.type-auction {
	background-color: #e8f5e9;
	color: #388e3c;
}

.type-message {
	background-color: #fff3e0;
	color: #f57c00;
}

.type-system {
	background-color: #f3e5f5;
	color: #7b1fa2;
}

.mark-all-read {
	display: inline-block;
	background-color: #f0f0f0;
	color: #333;
	border: none;
	padding: 8px 15px;
	border-radius: 4px;
	cursor: pointer;
	font-size: 14px;
	transition: background-color 0.2s;
	margin-bottom: 15px;
}

.mark-all-read:hover {
	background-color: #e0e0e0;
}

.empty-list {
	text-align: center;
	padding: 30px;
	color: #999;
}

.notification-icon {
	margin-right: 15px;
	font-size: 20px;
	width: 24px;
	height: 24px;
	display: flex;
	align-items: center;
	justify-content: center;
}

footer {
	margin-top: 30px;
	text-align: center;
	color: #999;
	font-size: 14px;
	padding: 20px 0;
	border-top: 1px solid #e0e0e0;
}
</style>
</head>
<body>
	<div class="container">
		<header>
			<div>
				<h1>알림 목록</h1>
				<p>최근 활동과 업데이트를 확인하세요</p>
			</div>
			<div>
				<%
				if (userId != null && !userId.isEmpty()) {
					out.println("<p>" + userId + "님 환영합니다</p>");
				}
				%>
			</div>
		</header>

		<%
		if (notifications != null && !notifications.isEmpty()) {
		%>
		<form action="markAllNotificationsAsRead" method="post">
			<button type="submit" class="mark-all-read">모든 알림 읽음 표시</button>
		</form>

		<div class="notification-list">
			<%
			for (NotificationDTO notification : notifications) {
				String notificationType = notification.getNotification_type();
				String typeClass = "";
				String icon = "";

				if ("bid".equals(notificationType)) {
					typeClass = "type-bid";
					icon = "💰";
				} else if ("auction".equals(notificationType)) {
					typeClass = "type-auction";
					icon = "🏆";
				} else if ("message".equals(notificationType)) {
					typeClass = "type-message";
					icon = "✉️";
				} else {
					typeClass = "type-system";
					icon = "🔔";
				}

				boolean isUnread = notification.getRead_status() == null || "N".equals(notification.getRead_status());
			%>
			<div
				class="notification-item <%=isUnread ? "notification-unread" : ""%>">
				<div class="notification-icon"><%=icon%></div>
				<div class="notification-content">
					<span class="notification-type <%=typeClass%>"><%=notificationType%></span>
					<p><%=notification.getMessage()%></p>
					<div class="notification-time"><%=notification.getCreated_at()%></div>
					<%
					if (notification.getProduct_title() != null && !notification.getProduct_title().isEmpty()) {
					%>
					<p>
						관련 상품:
						<%=notification.getProduct_title()%></p>
					<%
					}
					%>
				</div>
				<div class="notification-actions">
					<form action="markNotificationAsRead" method="post"
						style="display: inline;">
						<input type="hidden" name="notificationId"
							value="<%=notification.getNotification_id()%>">
						<button type="submit" title="읽음 표시">✓</button>
					</form>
					<%
					if (notification.getRelated_product_id() > 0) {
					%>
					<a
						href="viewProduct?productId=<%=notification.getRelated_product_id()%>"
						title="상품 보기" style="text-decoration: none;">👁️</a>
					<%
					}
					%>
				</div>
			</div>
			<%
			}
			%>
		</div>
		<%
		} else {
		%>
		<div class="empty-list">
			<p>새로운 알림이 없습니다.</p>
		</div>
		<%
		}
		%>

		<form action="index" method="get">
			<button type="submit" style="padding: 8px 16px;">← 메인으로 이동</button>
		</form>

		<footer>
			<p>&copy; 2023 경매 시스템. 모든 권리 보유.</p>
		</footer>
	</div>
</body>
</html>
