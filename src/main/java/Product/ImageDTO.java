package Product;

import java.sql.Timestamp;

public class ImageDTO {
	private int imageId;
    private int productId;
    private String imagePath;
    private boolean isThumbnail;
    private Timestamp uploadDate;
    
    
 // 게터/세터
    public int getImageId() { return imageId; }
    public void setImageId(int imageId) { this.imageId = imageId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public boolean isThumbnail() { return isThumbnail; }
    public void setThumbnail(boolean isThumbnail) { this.isThumbnail = isThumbnail; }
    
    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }
    
    
}
