package Interest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/interesting")
public class InterestServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		String userId = (String) session.getAttribute("user_id");

		// 로그인 안 했을 경우
		if (userId == null) {
			resp.sendRedirect("login.jsp");
			return;
		}

		int productId = Integer.parseInt(req.getParameter("product_id"));

		// 관심상품 추가 서비스 호출
		InterestService s = new InterestService();

		if (s.isInteresting(userId, productId)) {
			s.deleteInterest(userId, productId);
		} else {
			s.addInterest(userId, productId);
		}

		// 다시 상품 상세 페이지로 이동
		resp.sendRedirect("buyingproduct?product_id=" + productId);

	}

}
