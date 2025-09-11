package Notification;

import java.util.ArrayList;
import java.util.TimerTask;

import Product.BuyingProductDAO;
import Product.ProductDTO;

import java.util.Timer;
import java.util.Date;
import java.util.Calendar;

public class NotificationScheduler {
    
    private static Timer timer = new Timer(true);
    
    public static void initScheduler() {
        // 매 시간 실행되는 작업 등록 (경매 종료 알림 체크)
        timer.scheduleAtFixedRate(new CheckAuctionEndTask(), 0, 60 * 60 * 1000); // 1시간마다 체크
    }
    
    static class CheckAuctionEndTask extends TimerTask {
        @Override
        public void run() {
            try {
                BuyingProductDAO productDAO = new BuyingProductDAO();
                NotificationDAO notificationDAO = new NotificationDAO();
                
                // 현재 시간 기준으로 곧 종료될 경매 상품 조회
                ArrayList<ProductDTO> products = getUpcomingEndAuctions(productDAO);
                
                for (ProductDTO product : products) {
                    // 경매 종료 시간
                    Date endTime = product.getAuction_end_time();
                    // 현재 시간
                    Date now = new Date();
                    
                    // 시간 차이 계산 (밀리초)
                    long diff = endTime.getTime() - now.getTime();
                    long hoursDiff = diff / (60 * 60 * 1000);
                    
                    // 24시간 이내 종료 예정
                    if (hoursDiff <= 24 && hoursDiff > 23) {
                        sendReminderNotifications(product, "24시간", notificationDAO);
                    } 
                    // 1시간 이내 종료 예정
                    else if (hoursDiff <= 1 && hoursDiff > 0) {
                        sendReminderNotifications(product, "1시간", notificationDAO);
                    }
                    // 10분 이내 종료 예정
                    else if (diff <= 10 * 60 * 1000 && diff > 9 * 60 * 1000) {
                        sendReminderNotifications(product, "10분", notificationDAO);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void sendReminderNotifications(ProductDTO product, String timeLeft, NotificationDAO notificationDAO) {
            // 판매자에게 알림
            String content = "[" + product.getTitle() + "] 상품의 경매가 " + timeLeft + " 후에 종료됩니다.";
            notificationDAO.addNotification(product.getSeller_id(), content, "REMINDER", product.getProduct_id());
            
            // 이 상품에 입찰한 모든 사용자에게 알림
            ArrayList<String> bidders = notificationDAO.getProductBidders(product.getProduct_id());
            
            for (String bidderId : bidders) {
                content = "[" + product.getTitle() + "] 상품의 경매가 " + timeLeft + " 후에 종료됩니다.";
                notificationDAO.addNotification(bidderId, content, "REMINDER", product.getProduct_id());
            }
            
            // 이 상품을 찜한 사용자에게 알림
            ArrayList<String> favoriteUsers = notificationDAO.getProductFavoriteUsers(product.getProduct_id());
            
            for (String userId : favoriteUsers) {
                // 이미 입찰한 사용자는 제외 (중복 알림 방지)
                if (!bidders.contains(userId)) {
                    content = "찜하신 [" + product.getTitle() + "] 상품의 경매가 " + timeLeft + " 후에 종료됩니다.";
                    notificationDAO.addNotification(userId, content, "REMINDER", product.getProduct_id());
                }
            }
        }
        
        // 24시간 이내에 종료될 경매 상품 목록 조회
        private ArrayList<ProductDTO> getUpcomingEndAuctions(BuyingProductDAO productDAO) {
            ArrayList<ProductDTO> upcomingEndAuctions = new ArrayList<>();
            
            // 판매 중인 모든 상품 조회
            ArrayList<ProductDTO> availableProducts = productDAO.selectAvailableProducts();
            
            // 현재 시간
            Date now = new Date();
            // 24시간 후 시간
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.HOUR, 24);
            Date after24Hours = cal.getTime();
            
            // 24시간 이내에 종료될 경매 필터링
            for (ProductDTO product : availableProducts) {
                Date endTime = product.getAuction_end_time();
                if (endTime != null && endTime.after(now) && endTime.before(after24Hours)) {
                    upcomingEndAuctions.add(product);
                }
            }
            
            return upcomingEndAuctions;
        }
    }
    
    // 애플리케이션 종료 시 타이머 정리
    public static void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
    }
} 