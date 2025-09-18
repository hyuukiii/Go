package Ranking;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class RankingScheduler implements ServletContextListener {
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("알림 스케줄러가 초기화되었습니다.");
		scheduler = Executors.newScheduledThreadPool(1);

		// 랭킹 계산 작업 정의
		Runnable rankingTask = () -> {
			try {
				System.out.println("랭킹 계산 스케줄러 실행 중... " + System.currentTimeMillis());

				ProductRankingDAO rankingDAO = new ProductRankingDAO();
				boolean success = false;

				try {
					success = rankingDAO.calculateAndUpdateRankings();
				} catch (Exception e) {
					System.out.println("랭킹 계산 중 오류 발생: " + e.getMessage());
					e.printStackTrace();
				}

				System.out.println("랭킹 계산 결과: " + (success ? "성공" : "실패"));
			} catch (Exception e) {
				System.out.println("랭킹 스케줄러 작업 중 예외 발생: " + e.getMessage());
				e.printStackTrace();
			}
		};

		// 즉시 실행 후 6시간마다 반복 실행
		scheduler.scheduleAtFixedRate(rankingTask, 0, 6, TimeUnit.HOURS);

		System.out.println("랭킹 스케줄러가 시작되었습니다.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (scheduler != null) {
			scheduler.shutdownNow();
			System.out.println("랭킹 스케줄러가 종료되었습니다.");
		}
	}
}