package Review;


import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import Product.BuyingProductDAO;
import Product.ProductDTO;

@WebServlet("/reviewform")
public class ReviewFormServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String productIdStr = request.getParameter("product_id");
		if (productIdStr == null) {
			response.sendRedirect("reviewlist");
			return;
		}
		int productId = Integer.parseInt(productIdStr);

		BuyingProductDAO productDao = new BuyingProductDAO();
		ProductDTO product = productDao.selectProductById(productId);

		if (product == null) {
			response.sendRedirect("reviewlist");
			return;
		}

		// 상품 정보를 request에 담아서 포워딩
		request.setAttribute("product", product);
		RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/views/reviewform.jsp");
		dispatcher.forward(request, response);
	}
}
