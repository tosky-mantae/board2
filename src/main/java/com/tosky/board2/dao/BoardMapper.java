package com.tosky.board2.dao;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
public interface BoardMapper {
//    public List<Map<String,String>> getBoardList(Map<String,Integer>param);

    public int writeBoardArticle(Map<String,String> param);

    public int deleteArticle(Map<String,String>param);

    public int modifyArticle(Map<String,String>param);

    public List<Map<String,String>> getAllBoardData (Map<String,Object>param);

    public int articleTotalCount(Map<String,Object>param);

    public int signUp(Map<String,Object> param);

    public int signUpCheck(Map<String,Object>param);

    public Map<String,Object> loginCheck(Map<String,Object>param);

    public Map<String,String> getViewArticle(int boardNo);

    public Map<String,String> getViewArticlePwCheck(int boardNo);

    public Map<String,String> pwJoinData(int boardNo);
}