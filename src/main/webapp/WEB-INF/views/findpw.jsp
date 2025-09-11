<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>비밀번호 찾긔</title>
	<style>
	    form {
	        max-width: 400px;
	        margin: 50px auto;
	        padding: 24px;
	        border: 1px solid #e0e0e0;
	        border-radius: 12px;
	        font-family: 'Noto Sans KR', sans-serif;
	        background-color: #fff;
	        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
	    }
	    form h2 {
	        text-align: center;
	        font-size: 24px;
	        margin-bottom: 24px;
	    }
	    form label {
	        display: block;
	        margin-top: 12px;
	        font-weight: bold;
	    }
	    form input[type="text"],
	    form input[type="email"] {
	        width: 100%;
	        padding: 10px;
	        margin-top: 4px;
	        border: 1px solid #ccc;
	        border-radius: 6px;
	        box-sizing: border-box;
	    }
	    form input[type="submit"] {
	        width: 100%;
	        height:50px;
	        padding: 10px;
	        margin-top: 24px;
	        background-color: #555; 
	        color: white;
	        border: none;
	        border-radius: 6px;
	        cursor: pointer;
	        font-weight: bold;
	        transition: 0.2s;
	    }
	    form input[type="submit"]:hover {
	        background-color: #ccc;
	        color: black;
	    }
	    
	    .mainBtn{
            width: 100px;
            height: 40px;
		    background-color: white;
		    color: #333;
		    border-radius: 6px;
		    border: none;
		    font-weight: bold;
		    cursor: pointer;
		    transition: 0.2s;
		}
		
		.mainBtn:hover {
		    background-color: #ccc;
		    color: black;
		}
	</style>

</head>
<body>
	 <form method="post" action="<%= request.getContextPath() %>/FindPw">
        <button class="mainBtn" type="button" onclick="location.href='<%= request.getContextPath() %>/index'">🡐 메인화면</button>
        
        <h2>비밀번호 찾기</h2>

        <label for="username">이름</label>
        <input type="text" id="username" name="username" placeholder="이름 입력" required>

        <label for="email">이메일</label>
        <input type="email" id="email" name="email" placeholder="이메일 입력" required>

        <label for="phone">전화번호</label>
        <input type="text" id="phone" name="phone" placeholder="- 없이 숫자만 입력" required>

        <label for="user_id">아이디</label>
        <input type="text" id="user_id" name="user_id" placeholder="아이디 입력" required>

        <input type="submit" value="비밀번호 찾기">
    </form>

	<%String password=(String)request.getAttribute("password");
    if (password != null) {%>

    <script>
    	alert("당신의 비밀번호는 '<%=password%>' 입니다.");
        <%-- alert("당신의 아이디는 '<%= userId.substring(0, 2) %>****' 입니다."); --%>
        location.href = '<%= request.getContextPath() %>/login';
    </script>
    
	<%} else if ("POST".equalsIgnoreCase(request.getMethod())) {%>
    <script>
        alert("일치하는 정보가 없습니다.");
    </script>
	<%}%>
</body>
</html>