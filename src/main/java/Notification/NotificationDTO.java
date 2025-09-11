package Notification;

import java.sql.Timestamp;

public class NotificationDTO {
    private int notification_id;
    private String user_id;
    private String content;
    private String notification_type;
    private int related_product_id;
    private Timestamp created_at;
    private String is_read;
    private String product_title; // 관련 상품 제목 (조인용)
    
    // 게터와 세터 메서드
    public int getNotification_id() {
        return notification_id;
    }
    
    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    // 알림 내용을 반환하는 메서드 (content 필드와 동일)
    public String getMessage() {
        return content;
    }
    
    public String getNotification_type() {
        return notification_type;
    }
    
    public void setNotification_type(String notification_type) {
        this.notification_type = notification_type;
    }
    
    public int getRelated_product_id() {
        return related_product_id;
    }
    
    public void setRelated_product_id(int related_product_id) {
        this.related_product_id = related_product_id;
    }
    
    public Timestamp getCreated_at() {
        return created_at;
    }
    
    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
    
    public char getIs_read() {
        return is_read != null && !is_read.isEmpty() ? is_read.charAt(0) : 'N';
    }
    
    public void setIs_read(String is_read) {
        this.is_read = is_read;
    }
    
    // read_status를 반환하는 메서드 (is_read 필드와 동일)
    public String getRead_status() {
        return is_read;
    }
    
    public String getProduct_title() {
        return product_title;
    }
    
    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }
} 