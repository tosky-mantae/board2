package com.tosky.board2.controller;

import com.tosky.board2.service.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class scheduler {
    @Autowired
    private SchedulingService schedulingService;

    @Scheduled(fixedDelay = 10000) // 메소드 호출이 종료되는 시간에서 10000ms 이후 재 호출
    public void doFixedDelayJob() {
        List<Map<String, String>> mailSendUser = new ArrayList<Map<String, String>>();
        mailSendUser = schedulingService.loginCheck();

        System.out.println(mailSendUser);

    }
}
