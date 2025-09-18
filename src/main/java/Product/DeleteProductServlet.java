package Product;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/deleteproduct")
public class DeleteProductServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 사용자 로그인 확인
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("user_id");
                
        if (userId == null) {
            resp.sendRedirect("login");
            return;
        }
        
        // 상품 ID 파라미터 가져오기
        String productIdParam = req.getParameter("product_id");
        if (productIdParam == null || productIdParam.trim().isEmpty()) {
            showErrorAndRedirect(resp, "상품 ID가 제공되지 않았습니다.");
            return;
        }
        
        try {
            int productId = Integer.parseInt(productIdParam);
            
            // 상품 삭제 처리
            BuyingProductDAO dao = new BuyingProductDAO();
            boolean isDeleted = dao.deleteProduct(productId, userId);
            
            if (isDeleted) {
                // 삭제 성공 시 메인 페이지로 리다이렉트
                resp.sendRedirect("index?message=deleted");
            } else {
                // 삭제 실패 시 에러 메시지 표시
                showErrorAndRedirect(resp, "상품 삭제에 실패했습니다. 본인이 등록한 상품만 삭제할 수 있습니다.");
            }
        } catch (NumberFormatException e) {
            showErrorAndRedirect(resp, "유효하지 않은 상품 ID입니다.");
        }
    }
    
    private void showErrorAndRedirect(HttpServletResponse resp, String errorMessage) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<script>");
        out.println("alert('" + errorMessage + "');");
        out.println("history.back();");
        out.println("</script>");
    }
} 