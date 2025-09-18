package main.java.FindIdPw;

//이름, 이메일, 전화번호가 맞으면 아이디 출력

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FindId")
public class FindIdServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/views/findid.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");

		FindIdDTO dto = new FindIdDTO(username, email, phone);
		FindIdService service = new FindIdService();
		String userId = service.findId(dto);

		// 아이디 찾으면 리디렉션
		if (userId != null) {
			req.setAttribute("userId", userId);
			req.getRequestDispatcher("WEB-INF/views/findid.jsp").forward(req, resp);

		} else {
			req.setAttribute("userId", null);
			req.getRequestDispatcher("WEB-INF/views/findid.jsp").forward(req, resp);
		}

	}
}
