package gov.sls.ods.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.threeten.bp.LocalDateTime;

/**
 * 排程固定執行的作業。
 */
@Slf4j
@Component
// 可被排程執行的作業一定要是 Spring Bean (@Service, @Component...)
public class ScheduledService {

    // 只有沒有回傳值也沒有任何參數的 Method 才能標 {@code @Scheduled}。
    // 這個範例是在每小時的 3 的倍數分鐘 0 秒時執行作業
    @Scheduled(cron = "0 0/3 * * * ?")
    public void runPeriodically() {
        log.debug("Scheduled task running at {}", LocalDateTime.now());
    }
}
