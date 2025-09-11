package Review;


import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import User.UserDAO;

@WebServlet("/submitreview")
public class SubmitReviewServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();
		String reviewerId = (String) session.getAttribute("user_id");

		if (reviewerId == null) {
			response.sendRedirect("login");
			return;
		}

		int productId = Integer.parseInt(request.getParameter("product_id"));
		String sellerId = request.getParameter("seller_id");
		String description = request.getParameter("description");
		double rating = Double.parseDouble(request.getParameter("rating"));

		ReviewDTO review = new ReviewDTO();
		review.setReviewerId(reviewerId);
		review.setProductId(productId); 
		review.setSellerId(sellerId);
		review.setDescription(description);
		review.setRating(rating);

		ReviewDAO reviewDao = new ReviewDAO();
		reviewDao.insertReview(review);
		
		UserDAO userDAO = new UserDAO();
		userDAO.updateSellerRatings();

		response.sendRedirect("index");
	}
}
