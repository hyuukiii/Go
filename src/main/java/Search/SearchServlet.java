package Search;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Product.ProductDTO;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");
        String price = request.getParameter("price");
        String status = request.getParameter("status");
        String sort = request.getParameter("sort");
        
        // 검색 결과 페이지 번호 (기본값 1)
        int page = 1;
        if (request.getParameter("page") != null) {
            try {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                // 숫자 변환 실패 시 기본값 사용
            }
        }
        
        // 페이지당 상품 개수
        int itemsPerPage = 16;
        
        // 검색 DAO 호출
        SearchDAO searchDAO = new SearchDAO();
        ArrayList<ProductDTO> searchResults = searchDAO.searchProducts(keyword, category, price, status, sort, page, itemsPerPage);
        
        // 총 검색 결과 수 (페이지네이션 계산용)
        int totalItems = searchDAO.countSearchResults(keyword, category, price, status);
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        
        // 검색 결과를 request에 저장
        request.setAttribute("searchResults", searchResults);
        request.setAttribute("keyword", keyword);
        request.setAttribute("category", category);
        request.setAttribute("price", price);
        request.setAttribute("status", status);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        
        // 검색 결과 페이지로 포워드
        RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/views/searchResults.jsp");
        dispatcher.forward(request, response);
    }
} 