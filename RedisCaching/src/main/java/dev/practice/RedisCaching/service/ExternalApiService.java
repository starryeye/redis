package dev.practice.RedisCaching.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ExternalApiService {

    public String getUserName(String userId) {
        //외부 API, DB 호출 가정

        System.out.println("Getting user name from external service..");

        try {
            Thread.sleep(500); //0.5초
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(userId.equals("A")) {
            return "Alice";
        } else if (userId.equals("B")) {
            return "Bob";
        }

        return "";
    }

    @Cacheable(cacheNames = "userAgeCache", key = "#userId")
    public int getUserAge(String userId) {
        //외부 API, DB 호출 가정

        System.out.println("Getting user age from external service..");

        try {
            Thread.sleep(500); //0.5초
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(userId.equals("A")) {
            return 28;
        } else if (userId.equals("B")) {
            return 32;
        }

        return 0;
    }
}
