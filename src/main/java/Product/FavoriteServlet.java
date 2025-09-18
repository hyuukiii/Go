package Product;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/favorite")
public class FavoriteServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("user_id");

		if (userId == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int productId = Integer.parseInt(request.getParameter("product_id"));
		FavoriteDAO dao = new FavoriteDAO();

		if (dao.isFavorited(userId, productId)) {
			dao.deleteFavorite(userId, productId);
		} else {
			dao.insertFavorite(userId, productId);
		}

		response.sendRedirect("buyingproduct?product_id=" + productId);
	}
}
