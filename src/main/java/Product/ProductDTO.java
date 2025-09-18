package Product;

import java.sql.Timestamp;

public class ProductDTO {
	private int product_id;
	private String seller_id;
	private String title;
	private String description;
	private String category_id;
	private String category_name;
	private int price;
	private String status;
	private int view_count;
	private int maxPrice;
	private int minPrice;
	private Timestamp auction_end_time;
	private String image_path;
	private Timestamp register_date;
	private int ranking;
	private int previous_ranking;
	private double ranking_score;
	private int bid_count;
	private int favorite_count;
	private Timestamp ranking_updated;

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}

	public int getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}

	public Timestamp getAuction_end_time() {
		return auction_end_time;
	}

	public void setAuction_end_time(Timestamp auction_end_time) {
		this.auction_end_time = auction_end_time;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public Timestamp getRegister_date() {
		return register_date;
	}

	public void setRegister_date(Timestamp register_date) {
		this.register_date = register_date;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public int getPrevious_ranking() {
		return previous_ranking;
	}

	public void setPrevious_ranking(int previous_ranking) {
		this.previous_ranking = previous_ranking;
	}

	public double getRanking_score() {
		return ranking_score;
	}

	public void setRanking_score(double ranking_score) {
		this.ranking_score = ranking_score;
	}

	public int getBid_count() {
		return bid_count;
	}

	public void setBid_count(int bid_count) {
		this.bid_count = bid_count;
	}

	public int getFavorite_count() {
		return favorite_count;
	}

	public void setFavorite_count(int favorite_count) {
		this.favorite_count = favorite_count;
	}

	public Timestamp getRanking_updated() {
		return ranking_updated;
	}

	public void setRanking_updated(Timestamp ranking_updated) {
		this.ranking_updated = ranking_updated;
	}
}
