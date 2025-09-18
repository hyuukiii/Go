package main.java.Interest;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.java.Product.ProductDTO;

@WebServlet("/viewInterest")
public class InterestViewServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		InterestService s = new InterestService();

		String userId = (String) req.getSession().getAttribute("user_id");

		if (userId != null) {
			ArrayList<ProductDTO> favorites = s.showInterestList(userId);
			req.setAttribute("favorites", favorites);

		} else {
			resp.sendRedirect("login");
			return;
		}

		req.getRequestDispatcher("WEB-INF/views/interest.jsp").forward(req, resp);

	}

}
