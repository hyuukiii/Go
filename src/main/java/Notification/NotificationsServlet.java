package Notification;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/notifications")
public class NotificationsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");
        
        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }
        
        NotificationDAO notificationDAO = new NotificationDAO();
        ArrayList<NotificationDTO> notifications = notificationDAO.getUserNotifications(userId);
        
        req.setAttribute("notifications", notifications);
        req.getRequestDispatcher("WEB-INF/views/notifications.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");
        
        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }
        
        String action = req.getParameter("action");
        NotificationDAO notificationDAO = new NotificationDAO();
        
        if ("mark_read".equals(action)) {
            String notificationId = req.getParameter("notification_id");
            if (notificationId != null && !notificationId.isEmpty()) {
                notificationDAO.markAsRead(Integer.parseInt(notificationId));
            }
        } else if ("mark_all_read".equals(action)) {
            notificationDAO.markAllAsRead(userId);
        }
        
        resp.sendRedirect("notifications");
    }
} 