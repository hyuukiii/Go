package Review;


import java.io.IOException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import BidsTransaction.BidDAO;
import Product.ProductDTO;

@WebServlet("/reviewlist")
public class ReviewListServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("user_id");

		if (userId == null) {
			response.sendRedirect("login");
			return;
		}

		BidDAO bidDao = new BidDAO();
		List<ProductDTO> winningProducts = bidDao.selectWinningProducts(userId);
		request.setAttribute("winningProducts", winningProducts);

		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/views/reviewlist.jsp");
		dispatcher.forward(request, response);
	}
}