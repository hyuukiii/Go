package BidsTransaction;


import java.sql.Timestamp;

public class BidDTO {
	private int bid_id;
	private String user_id;
	private int product_id;
	private int bid_amount;
	private Timestamp bid_time;
	private String is_winning;
	private String product_title;
	private String seller_id;
	private String username;

	public int getBid_id() {
		return bid_id;
	}

	public void setBid_id(int bid_id) {
		this.bid_id = bid_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getBid_amount() {
		return bid_amount;
	}

	public void setBid_amount(int bid_amount) {
		this.bid_amount = bid_amount;
	}

	public Timestamp getBid_time() {
		return bid_time;
	}

	public void setBid_time(Timestamp bid_time) {
		this.bid_time = bid_time;
	}

	public String getIs_winning() {
		return is_winning;
	}

	public void setIs_winning(String is_winning) {
		this.is_winning = is_winning;
	}

	public String getProduct_title() {
		return product_title;
	}

	public void setProduct_title(String product_title) {
		this.product_title = product_title;
	}

	public String getSeller_id() {
		return seller_id;
	}

	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
