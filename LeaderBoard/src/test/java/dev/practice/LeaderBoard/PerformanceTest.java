package dev.practice.LeaderBoard;

import dev.practice.LeaderBoard.service.RankingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class PerformanceTest {

    @Autowired
    RankingService rankingService;

    @Test
    void inMemorySortPerformance() {

        ArrayList<Integer> list = IntStream.range(0, 10000 * 100)
                .parallel()
                .mapToObj(i -> (int) (Math.random() * 10000 * 100))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        var stopWatch = new StopWatch();
        stopWatch.start();

        Collections.sort(list);

        stopWatch.stop();
        System.out.println("inMemorySortPerformance : " + stopWatch.getTotalTimeSeconds());
    }

    @Test
    void redisInsertData() {

        for(int i = 0; i < 10000 * 100; i++) {
            rankingService.setUserScore(
                    "user_"+ i,
                    (int) (Math.random() * 10000 * 100)
            );
        }
    }

    @Test
    void redisGetRank() {
        //redis 최초 접속 시간이 오래 걸리기 때문에 의미 없는 조회 수행
        rankingService.getUserRanking("user_100");

        //성능 측정 1, get User Ranking
        var stopWatch = new StopWatch();
        stopWatch.start();

        Long userRanking = rankingService.getUserRanking("user_10000");

        stopWatch.stop();
        System.out.printf("userRank(%s) - took time : %s\n", userRanking, stopWatch.getTotalTimeSeconds());

        //성능 측정 2, get Top 10 Ranking
        stopWatch = new StopWatch();
        stopWatch.start();

        List<String> topRankers = rankingService.getTopRank(10L);

        stopWatch.stop();
        System.out.printf("Range - took time : %s\n", stopWatch.getTotalTimeSeconds());
    }
}
