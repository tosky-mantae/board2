package com.tosky.board2.dao;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;


@Mapper
public interface SchedulingMapper {
    public List<Map<String,String>> loginCheck();
}