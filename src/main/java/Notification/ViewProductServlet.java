package Notification;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/viewProduct")
public class ViewProductServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");
        
        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }
        
        String productIdStr = req.getParameter("productId");
        if (productIdStr != null && !productIdStr.isEmpty()) {
            try {
                int productId = Integer.parseInt(productIdStr);
                // 상품 보기 페이지로 리다이렉트
                resp.sendRedirect("buyingproduct?product_id=" + productId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                resp.sendRedirect("notifications"); // 오류 발생 시 알림 페이지로 돌아감
            }
        } else {
            resp.sendRedirect("notifications"); // productId가 없으면 알림 페이지로 돌아감
        }
    }
} 