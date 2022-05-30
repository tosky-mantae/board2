package com.tosky.board2.service;


import com.tosky.board2.dao.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardService {

    @Autowired
    BoardMapper boardMapper;

//    public List<Map<String,String>> getBoardList(Map<String,Integer>param){
//        List<Map<String,String>> boardList = boardMapper.getBoardList(param);
//        return boardList;

    public int writeBoardArticle(Map<String, String> param) {
        int result = boardMapper.writeBoardArticle(param);
        return result;
    }

    public int deleteArticle(Map<String, String> param) {
        int result = boardMapper.deleteArticle(param);
        return result;
    }

    public int modifyArticle(Map<String, String> param) {
        int result = boardMapper.modifyArticle(param);
        return result;
    }

    public List<Map<String,String>> getAllBoardData(Map<String, Object> param) {
        List<Map<String,String>> result = boardMapper.getAllBoardData(param);
        return result;
    }

    public int articleTotalCount(Map<String,Object>param) {
        int result = boardMapper.articleTotalCount(param);
        return result;
    }

    public Map<String,String> getViewArticle(int boardNo) {
        Map<String,String> viewData = boardMapper.getViewArticle(boardNo);
        return viewData;
    }
    public Map<String,Object> getViewArticle2(int boardNo) {
        Map<String,Object> viewData = boardMapper.getViewArticle2(boardNo);
        return viewData;
    }

    public List<Map<String,String>> searchArticle(Map<String, String> param){
        List<Map<String,String>> searchReturn = boardMapper.searchArticle(param);
        return searchReturn;
    }
}