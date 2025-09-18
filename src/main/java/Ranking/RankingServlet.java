package Ranking;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Product.ProductDTO;

@WebServlet("/api/rankings")
public class RankingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String type = request.getParameter("type");
        int limit = 10; // 기본값
        
        try {
            if (request.getParameter("limit") != null) {
                limit = Integer.parseInt(request.getParameter("limit"));
                if (limit < 1) limit = 10;
                if (limit > 50) limit = 50; // 최대 50개로 제한
            }
        } catch (NumberFormatException e) {
            // 숫자 변환 실패 시 기본값 사용
        }
        
        ProductRankingDAO rankingDAO = new ProductRankingDAO();
        ArrayList<ProductDTO> products = null;
        
        if ("recommended".equals(type)) {
            // 로그인한 사용자를 위한 추천 상품
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("user_id");
            
            if (userId != null) {
                products = rankingDAO.getRecommendedProducts(userId, limit);
            } else {
                // 로그인하지 않은 경우 일반 랭킹 표시
                products = rankingDAO.getTopRankedProducts(limit);
            }
        } else {
            // 기본 인기 랭킹
            products = rankingDAO.getTopRankedProducts(limit);
        }
        
        // JSON 형식으로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONArray jsonArray = new JSONArray();
        
        if (products != null) {
            for (ProductDTO product : products) {
                JSONObject jsonProduct = new JSONObject();
                jsonProduct.put("product_id", product.getProduct_id());
                jsonProduct.put("title", product.getTitle());
                jsonProduct.put("price", product.getPrice());
                jsonProduct.put("image_path", product.getImage_path());
                jsonProduct.put("ranking", product.getRanking());
                jsonProduct.put("previous_ranking", product.getPrevious_ranking());
                
                // 랭킹 변동 계산
                int rankChange = product.getPrevious_ranking() == 0 ? 0 : 
                                product.getPrevious_ranking() - product.getRanking();
                jsonProduct.put("rank_change", rankChange);
                
                // 신규 진입 여부
                jsonProduct.put("is_new", product.getPrevious_ranking() == 0);
                
                // 기타 정보
                jsonProduct.put("view_count", product.getView_count());
                jsonProduct.put("bid_count", product.getBid_count());
                jsonProduct.put("favorite_count", product.getFavorite_count());
                
                // 경매 종료 시간
                if (product.getAuction_end_time() != null) {
                    jsonProduct.put("auction_end_time", product.getAuction_end_time().getTime());
                }
                
                jsonArray.add(jsonProduct);
            }
        }
        
        PrintWriter out = response.getWriter();
        out.print(jsonArray.toJSONString());
        out.flush();
    }
    
    // 사용자 상호작용 기록을 위한 API
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String userId = (String) session.getAttribute("user_id");
        
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        
        String action = request.getParameter("action");
        int productId;
        
        try {
            productId = Integer.parseInt(request.getParameter("product_id"));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        ProductRankingDAO rankingDAO = new ProductRankingDAO();
        boolean success = false;
        
        if ("view".equals(action)) {
            success = rankingDAO.recordUserAction(userId, productId, "view");
        } else if ("bid".equals(action)) {
            success = rankingDAO.recordUserAction(userId, productId, "bid");
        } else if ("favorite".equals(action)) {
            success = rankingDAO.recordUserAction(userId, productId, "favorite");
        } else if ("unfavorite".equals(action)) {
            success = rankingDAO.recordUserAction(userId, productId, "unfavorite");
        }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        
        PrintWriter out = response.getWriter();
        out.print(jsonResponse.toJSONString());
        out.flush();
    }
} 