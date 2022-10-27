package com.tosky.board2.service;

import com.tosky.board2.dao.BoardMapper;
import com.tosky.board2.dao.SchedulingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class SchedulingService {

    @Autowired
    SchedulingMapper schedulingMapper;


    public List<Map<String,String>> loginCheck() {
        List<Map<String,String>> result = schedulingMapper.loginCheck();
        return result;
    }
}
