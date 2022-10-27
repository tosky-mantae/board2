package com.tosky.board2.dao;

import com.tosky.board2.Vo.UserVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    // 로그인
    UserVo getUserAccount(String userId);


    // 회원가입
    void saveUser(UserVo userVo);

    //로그인 시간 기록
    int loginTime(String userId);
}