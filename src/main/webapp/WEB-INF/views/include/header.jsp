<%@ page import="Notification.NotificationDAO" %>
<%
// 미읽은 알림 개수 가져오기
String currentUserId = (String) session.getAttribute("user_id");
int unreadNotificationCount = 0;
if (currentUserId != null) {
    NotificationDAO notificationDAO = new NotificationDAO();
    unreadNotificationCount = notificationDAO.getUnreadNotificationCount(currentUserId);
}
%>
<!-- 기존 헤더 코드 -->

<!-- 알림 아이콘 추가 -->
<% if (currentUserId != null) { %>
<div class="notification-icon-container">
    <a href="notifications" class="notification-icon">
        <i class="fa fa-bell"></i>
        <% if (unreadNotificationCount > 0) { %>
            <span class="notification-badge"><%= unreadNotificationCount %></span>
        <% } %>
    </a>
</div>
<% } %>

<style>
.notification-icon-container {
    display: inline-block;
    position: relative;
    margin-left: 20px;
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
</style>

<!-- Font Awesome CDN 추가 -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" /> 