package Notification;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// 애플리케이션 시작 시 알림 스케줄러 초기화
		NotificationScheduler.initScheduler();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// 애플리케이션 종료 시 스케줄러 종료
		NotificationScheduler.shutdown();
	}
}