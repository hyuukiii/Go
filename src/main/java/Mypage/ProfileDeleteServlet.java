package Mypage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/profileDelete")
public class ProfileDeleteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");

        // 기본 이미지 경로 지정
        String defaultImagePath = "images/User.png";

        // DB 업데이트
        UserService s = new UserService();
        s.updateProfileImg(userId, defaultImagePath);

        // 응답 (필요 시 경로 전달)
        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().write(defaultImagePath);
		
	}
	
}
