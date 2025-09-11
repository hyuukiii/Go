package FindIdPw;

//이름, 이메일, 전화번호, 아이디가 맞으면 아이디 출력
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FindPw")
public class FindPwServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/views/findpw.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		String userId = req.getParameter("user_id");
		String username = req.getParameter("username");
		String email = req.getParameter("email");
		String phone = req.getParameter("phone");

		FindPwDTO dto = new FindPwDTO(userId, username, email, phone);
		FindPwService service = new FindPwService();
		String password = service.findPassword(dto);

		// 비밀번호 찾으면 리디렉션
		if (password != null) {
			req.setAttribute("password", password);
			req.getRequestDispatcher("WEB-INF/views/findpw.jsp").forward(req, resp);

		} else {
			req.setAttribute("password", null);
			req.getRequestDispatcher("WEB-INF/views/findpw.jsp").forward(req, resp);
		}
	}
}
