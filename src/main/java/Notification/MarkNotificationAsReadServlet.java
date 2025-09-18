package Notification;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/markNotificationAsRead")
public class MarkNotificationAsReadServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");
        
        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }
        
        String notificationIdStr = req.getParameter("notificationId");
        if (notificationIdStr != null && !notificationIdStr.isEmpty()) {
            try {
                int notificationId = Integer.parseInt(notificationIdStr);
                NotificationDAO notificationDAO = new NotificationDAO();
                notificationDAO.markAsRead(notificationId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        resp.sendRedirect("notifications");
    }
} 