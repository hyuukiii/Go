package main.java.Mypage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.java.User.UserDTO;

@WebServlet("/profileManager")
public class UserUpdateServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		String user_id = (String) session.getAttribute("user_id");

		if (user_id == null) {
			resp.sendRedirect("login");
			return;
		}

		UserService s = new UserService();
		UserDTO user = s.getUserInfo(user_id);

		req.setAttribute("user", user);
		req.getRequestDispatcher("WEB-INF/views/profileManager.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("utf-8");

		HttpSession session = req.getSession();
		String user_id = (String) session.getAttribute("user_id");

		String user_name = req.getParameter("user_name");
		String email = req.getParameter("email");
		String password = req.getParameter("password");
		String phone = req.getParameter("phone");
		String address = req.getParameter("address");

		if (user_id == null) {
			resp.sendRedirect("login");
			return;
		}

		UserService s = new UserService();
		s.updateUserInfo(user_id, user_name, email, password, phone, address);

		resp.sendRedirect("index");
	}
}
