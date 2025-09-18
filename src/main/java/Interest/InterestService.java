package main.java.Interest;

import java.util.ArrayList;

import main.java.Product.ProductDTO;

public class InterestService {

	InterestDAO dao = new InterestDAO();

	// 추가
	public void addInterest(String userId, int productId) {
		dao.addInterest(userId, productId);
	}

	// 삭제
	public void deleteInterest(String userId, int productId) {
		dao.deleteInterest(userId, productId);
	}

	// 조회
	public ArrayList<ProductDTO> showInterestList(String userId) {

		ArrayList<ProductDTO> list = dao.viewInterest(userId);

		return list;

	}

	// 상태
	public boolean isInteresting(String userId, int productId) {

		return dao.isInteresting(userId, productId);

	}
}
