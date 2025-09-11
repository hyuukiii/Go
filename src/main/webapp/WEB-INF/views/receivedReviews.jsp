<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList, java.util.List,Review.ReviewDTO, java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>받은 리뷰 목록</title>
    <!-- Font Awesome CDN 추가 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
    <style>
        body {
            font-family: 'Arial', sans-serif;
            line-height: 1.6;
            margin: 0;
            padding: 0;
            background-color: #f9f9f9;
        }
        
        .container {
            width: 90%;
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        h1 {
            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }
        
        .review-list {
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            padding: 20px;
        }
        
        .review-item {
            border-bottom: 1px solid #eee;
            padding: 20px 0;
            margin-bottom: 10px;
        }
        
        .review-item:last-child {
            border-bottom: none;
        }
        
        .review-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }
        
        .review-info {
            flex: 1;
        }
        
        .reviewer-id {
            font-weight: bold;
            color: #333;
            margin-right: 10px;
        }
        
        .review-date {
            color: #777;
            font-size: 0.9em;
        }
        
        .product-title {
            font-weight: bold;
            margin-bottom: 10px;
            color: #2c3e50;
        }
        
        .rating {
            margin-bottom: 10px;
            color: #f39c12;
        }
        
        .review-content {
            margin-top: 10px;
            color: #444;
            line-height: 1.5;
        }
        
        .no-reviews {
            text-align: center;
            padding: 30px;
            color: #777;
            font-style: italic;
        }
        
        .back-btn {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .back-btn:hover {
            background-color: #2980b9;
        }
        
        .header {
            background-color: #ffffff;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 15px 20px;
            margin-bottom: 20px;
        }
        
        .header-content {
            display: flex;
            align-items: center;
            justify-content: space-between;
            max-width: 1200px;
            margin: 0 auto;
        }
    </style>
</head>
<body>
    <!-- 헤더 -->
    <div class="header">
        <div class="header-content">
            <h2><a href="index" style="text-decoration: none; color: inherit;">옥션 플랫폼</a></h2>
        </div>
    </div>

    <div class="container">
        <h1>받은 리뷰 목록</h1>
        
        <div class="review-list">
            <%
            List<ReviewDTO> receivedReviews = (List<ReviewDTO>) request.getAttribute("receivedReviews");
            
            if(receivedReviews != null && !receivedReviews.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                for(ReviewDTO review : receivedReviews) {
            %>
                <div class="review-item">
                    <div class="review-header">
                        <div class="review-info">
                            <span class="reviewer-id"><%=review.getReviewerId()%></span>
                            <span class="review-date"><%=sdf.format(review.getReviewDate())%></span>
                        </div>
                        <div class="rating">
                            <% for(int i = 0; i < review.getRating(); i++) { %>
                                <i class="fas fa-star"></i>
                            <% } %>
                            <% for(int i = 0; i < 5 - review.getRating(); i++) { %>
                                <i class="far fa-star"></i>
                            <% } %>
                            (<%=review.getRating()%>/5)
                        </div>
                    </div>
                    <div class="product-title">상품: <%=review.getProductTitle()%></div>
                    <div class="review-content"><%=review.getDescription()%></div>
                </div>
            <%
                }
            } else {
            %>
                <div class="no-reviews">
                    <p>받은 리뷰가 없습니다.</p>
                </div>
            <%
            }
            %>
        </div>
        
        <a href="index" class="back-btn">메인으로 돌아가기</a>
    </div>
</body>
</html> 