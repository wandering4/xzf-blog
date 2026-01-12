package com.xzf.blog.article.biz.schedule;

import com.xzf.blog.article.biz.domain.dataobject.StatisticsArticlePVDO;
import com.xzf.blog.article.biz.domain.mapper.ArticleMapper;
import com.xzf.blog.article.biz.domain.mapper.StatisticsArticlePVDOMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023/11/12 12:02
 * @description: 初始化 PV 访问量定时任务
 **/
@Component
@Slf4j
public class InitPVRecordScheduledTask {

    @Autowired
    private StatisticsArticlePVDOMapper articlePVMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Scheduled(cron = "0 1 0 * * ?") // 每天凌晨 0 点01分执行
    public void execute() {
        // 定时任务执行的业务逻辑
        log.info("==> 开始执行初始化昨日 PV 访问量记录定时任务");

        // 昨日日期
        LocalDate yesterdayDate = LocalDate.now().minusDays(1);

        // 检查昨日是否已经有记录，避免重复插入
        StatisticsArticlePVDO existingRecord = articlePVMapper.selectByPvDate(yesterdayDate);
        if (existingRecord != null) {
            log.info("==> 昨日 {} 的PV记录已存在，跳过初始化", yesterdayDate);
            return;
        }

        // 计算昨日的PV访问量
        // 方法1：获取当前总浏览量，减去前一天的浏览量快照，得到昨日的新增PV
        Long currentTotalViewCount = articleMapper.getTotalViewCount(
                com.xzf.blog.article.enums.ArticleStatusEnum.ENABLE.getCode());

        // 获取前一天的PV记录
        LocalDate dayBeforeYesterday = yesterdayDate.minusDays(1);
        StatisticsArticlePVDO previousRecord = articlePVMapper.selectByPvDate(dayBeforeYesterday);

        Long yesterdayPvCount;
        if (previousRecord != null && previousRecord.getPvCount() != null) {
            // 如果前一天有记录，用当前总浏览量减去前一天的总浏览量，得到昨日的新增PV
            yesterdayPvCount = currentTotalViewCount - previousRecord.getPvCount();
            // 确保PV数量不为负数
            yesterdayPvCount = Math.max(0L, yesterdayPvCount);
        } else {
            // 如果前一天没有记录，昨日PV就是当前总浏览量
            yesterdayPvCount = currentTotalViewCount;
        }

        // 组装插入的记录
        StatisticsArticlePVDO articlePVDO = StatisticsArticlePVDO.builder()
                .pvDate(yesterdayDate) // 昨日日期
                .pvCount(yesterdayPvCount) // 昨日的PV访问量
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        // 插入记录
        articlePVMapper.insert(articlePVDO);

        log.info("==> 成功初始化昨日 {} 的PV记录，PV数量：{}", yesterdayDate, yesterdayPvCount);
        log.info("==> 结束执行初始化昨日 PV 访问量记录定时任务");
    }
}
