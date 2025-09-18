package Mypage;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import User.UserDAO;
import User.UserDTO;

@WebServlet("/mypage")
public class UserInfoServlet extends HttpServlet {

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

		UserDAO userDao = new UserDAO();
		
		req.setAttribute("biddingList", userDao.getBiddingProducts(user_id));
		req.setAttribute("wonList", userDao.getWonProducts(user_id));
		req.setAttribute("sellingList", userDao.getSellingProducts(user_id));
		req.setAttribute("soldList", userDao.getSoldProducts(user_id));
		
		req.setAttribute("user", user);
		req.getRequestDispatcher("WEB-INF/views/mypage.jsp").forward(req, resp);
	}

}
