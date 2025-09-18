<%@page import="main.java.User.UserDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>프로필 관리</title>
<link rel="stylesheet" href="css/profileManager.css">
<%
UserDTO user = (UserDTO) request.getAttribute("user");
if (user == null) {
   response.sendRedirect("login");
   return;
}
%>
</head>
<body>

<div class="layout-wrapper">
	<!--사이드 메뉴바-->
    <div class="sidebar">
        <h2>프로필 관리</h2>
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
                <li><a href="${pageContext.request.contextPath}/mypage">프로필 정보</a></li>
                <li><a href="${pageContext.request.contextPath}/profileManager">프로필 관리</a></li>
                <li><a href="receivedreviews">리뷰 보기</a></li>
            </ul>
        </div>
    </div>

	<!-- 메인 내용 -->
    <div class="mypage-container">

        <!-- 프로필 이미지 수정 -->
        <div class="profile-box">
            <form id="profileForm" enctype="multipart/form-data">
                <div class="user_profile">
                    <input id="imageUpload" name="profileImage" type="file" accept="image/jpeg,image/png" hidden>
                    <img id="profileImagePreview" src="<%=user.getProfile_image()%>" alt="프로필 이미지">
                    <div class="profile-detail">
                        <div class="id"><%=user.getUser_id()%></div>
                        <div class="image-btn">
                            <button type="button" id="uploadTrigger">이미지 변경</button>
                            <button type="button" id="deleteProfileImage">삭제</button>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <!-- 프로필 정보 수정 -->
        <form action="${pageContext.request.contextPath}/profileManager" method="post">
            <div class="profile">
                <h4>프로필 정보</h4>
                <div class="unit column">
                    <div class="info-label">이름</div>
                    <div class="input-row">
                        <div class="info-value">
                            <input type="text" name="user_name" value="<%=user.getUsername()%>">
                        </div>
                        <button class="edit-button">변경</button>
                    </div>
                </div>
            </div>

            <div class="myInfo">
                <h4>내 계정</h4>
                <div class="unit column">
                    <div class="info-label">이메일</div>
                    <div class="input-row">
                        <div class="info-value">
                            <input type="text" name="email" value="<%=user.getEmail()%>">
                        </div>
                        <button class="edit-button">변경</button>
                    </div>
                </div>

                <div class="unit column">
                    <div class="info-label">비밀번호</div>
                    <div class="input-row">
                        <div class="info-value">
                            <input type="text" name="password" value="<%=user.getPassword()%>">
                        </div>
                        <button class="edit-button">변경</button>
                    </div>
                </div>
            </div>

            <div class="privateInfo">
                <h4>개인 정보</h4>
                <div class="unit column">
                    <div class="info-label">전화번호</div>
                    <div class="input-row">
                        <div class="info-value">
                            <input type="text" name="phone" value="<%=user.getPhone()%>">
                        </div>
                        <button class="edit-button">변경</button>
                    </div>
                </div>

                <div class="unit column">
                    <div class="info-label">주소</div>
                    <div class="input-row">
                        <div class="info-value">
                            <input type="text" name="address" value="<%=user.getAddress()%>">
                        </div>
                        <button class="edit-button">변경</button>
                    </div>
                </div>
            </div>
        </form>
        
    </div>
</div>

<script>
const imageInput = document.getElementById("imageUpload");
const uploadButton = document.getElementById("uploadTrigger");
const deleteButton = document.getElementById("deleteProfileImage");

uploadButton.addEventListener("click", () => {
    imageInput.click();
});

imageInput.addEventListener("change", () => {
    const file = imageInput.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append("profileImage", file);

    fetch("profileUpdate", {
        method: "POST",
        body: formData
    }).then(res => res.text())
    .then(path => {
        document.getElementById("profileImagePreview").src = path;
    }).catch(err => {
        console.error(err);
        alert("업로드 실패");
    });
});

deleteButton.addEventListener("click", () => {
    fetch("profileDelete", {
        method: "POST"
    }).then(res => res.text())
    .then(path => {
        document.getElementById("profileImagePreview").src = path;
    }).catch(err => {
        console.error(err);
        alert("삭제 실패");
    });
});
</script>

</body>
</html>
