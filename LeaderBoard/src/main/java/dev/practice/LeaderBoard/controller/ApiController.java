package dev.practice.LeaderBoard.controller;

import dev.practice.LeaderBoard.service.RankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApiController {

    private final RankingService rankingService;


    @PostMapping("/user/score")
    public Boolean setScore(@Valid @RequestBody UserScore userScore) {
        return rankingService.setUserScore(
                userScore.userId(),
                userScore.score()
        );
    }

    @GetMapping("/user/{userId}/rank")
    public Long getUserRank(@PathVariable("userId") String userId) {
        return rankingService.getUserRanking(userId);
    }

    @GetMapping("/topRanks")
    public List<String> getTopRanks(@RequestParam Long limit) {
        return rankingService.getTopRank(limit);
    }
}
