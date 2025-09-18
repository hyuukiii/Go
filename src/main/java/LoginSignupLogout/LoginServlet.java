package LoginSignupLogout;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Product.BuyingProductDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/views/login.jsp").forward(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		BuyingProductDAO dao = new BuyingProductDAO();

		String id = request.getParameter("user_id");
		String pw = request.getParameter("password");
		
		try (Connection con = dao.dbcon();
				PreparedStatement pst = con
						.prepareStatement("SELECT * FROM Users WHERE user_id = ? AND password = ?")) {

			pst.setString(1, id);
			pst.setString(2, pw);
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", id);
				
				response.sendRedirect("index");
			} else {
				showloginRedirect(response, "알맞지 않은 아이디 혹은 비밀번호입니다.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void showloginRedirect(HttpServletResponse resp, String errorMessage) throws IOException {
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.println("<script>");
		out.println("alert('" + errorMessage + "');");
		out.println("history.back();");
		out.println("</script>");
	}
	
}
