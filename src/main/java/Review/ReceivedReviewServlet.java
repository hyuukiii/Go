package Review;

import java.io.IOException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/receivedreviews")
public class ReceivedReviewServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("user_id");

        if (userId == null) {
            response.sendRedirect("login");
            return;
        }

        // ReviewDAO를 사용하여 해당 사용자가 받은 리뷰 목록을 가져옴
        ReviewDAO reviewDao = new ReviewDAO();
        List<ReviewDTO> receivedReviews = reviewDao.getReceivedReviews(userId);
        request.setAttribute("receivedReviews", receivedReviews);

        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/views/receivedReviews.jsp");
        dispatcher.forward(request, response);
    }
} 