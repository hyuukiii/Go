package Review;


import java.util.Date;

public class ReviewDTO {
	private int reviewId;
	private String reviewerId;
	private String sellerId;
	private String description;
	private double rating;
	private Date reviewDate;
	private int productId;
	private String productTitle;

	// 기본 생성자
	public ReviewDTO() {
	}

	// 전체 필드 생성자
	public ReviewDTO(int reviewId, String reviewerId, String sellerId, String description, double rating,
			Date reviewDate) {
		this.reviewId = reviewId;
		this.reviewerId = reviewerId;
		this.sellerId = sellerId;
		this.description = description;
		this.rating = rating;
		this.reviewDate = reviewDate;
	}

	// Getter & Setter
	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public String getSellerId() {
		return sellerId;
	}

	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
}
