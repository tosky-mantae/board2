package com.tosky.board2.controller;


import com.tosky.board2.service.BoardService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static com.tosky.board2.util.Utility.strToInt;

@RestController
public class ajaxController {

    @Autowired
    BoardService boardService;

    @PostMapping(value = "/listAjax")  //게시물 목록 생성 로직 실행
    public Map<String, Object> articleList(HttpServletRequest request, HttpServletResponse response, Model model) {
        //검색요소
        String searchKeyword = request.getParameter("searchKeyword");
        String selectComponent = request.getParameter("selectComponent");
        // 페이지리스트번호
        String pageNumStr = request.getParameter("pageNum");
        String test = request.getParameter("test");

        // 페이지 n개씩 띄우기 지정 변수
        double pageViewSetting = 10;
        int pageView = (int) pageViewSetting;

        double pageNumDouble = Double.parseDouble(pageNumStr);
        int pageNum = (int) Math.ceil(pageNumDouble);

        // pageNum이 1보다 작을 때
        if(pageNum < 1) {
            pageNum = 1;
        }

        // 쿼리 select offset값 계산
        int pageOffset = (pageNum - 1) * pageView;


        // select Limit,offset 데이터 Map
        Map<String, Object> pageSet = new HashMap<String, Object>();
        pageSet.put("pageOffset", pageOffset);
        pageSet.put("pageView", pageView);
        //serach요소 맵에 저장
        pageSet.put("searchKeyword", searchKeyword);
        pageSet.put("selectComponent", selectComponent);

        // 리스트 호출, 게시판 목록 생성
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        result = boardService.getAllBoardData(pageSet);

        // db에서 게시물 총 갯수 전달받음
        int articleTotalCount = boardService.articleTotalCount(pageSet);

        // 페이지 겟수 산출 :전체 게시물갯수/한페이지에 보여질 페이지 갯수
        int pageCount = (int) Math.ceil(articleTotalCount / pageViewSetting);

        //게시물이 없을경우 예외처리
        int searchNull = 0;
        if(pageCount == 0) {
            searchNull = 1;
            pageNum = 1;
            pageCount=1;
        }else if(pageNum>=pageCount){
            pageNum=pageCount;
        }


        // 페이지리스트List생성
        List<Integer> pageCountList = new ArrayList<>();

        for(int i = 1; i < pageCount + 1; i++) {
            pageCountList.add(i);
        }

        //페이징 그룹 갯수
        double pagingGroupCount = 5;
        int pagingGroupCountInt = (int) pagingGroupCount;

        //페이징 그룹 총 갯수 산출
        int pagingGroupTotalCount = (int) Math.ceil(pageCount / pagingGroupCount);

        //페이징 그룹 끝페이지 산출
        int pagingEndNum = (pagingGroupTotalCount * pagingGroupCountInt);

        //페이지 그룹중 현재 그룹계산
        int pagingGroupNum = (int) Math.ceil(pageNum / pagingGroupCount);

        //그룹 시작번호
        int pagingGroupStartNum = (int) (1 + ((pagingGroupNum - 1) * pagingGroupCount));

        // 페이지그룹List생성
        List<Integer> pageGroupList = new ArrayList<>();

        for(int i = pagingGroupStartNum; i < pagingGroupStartNum + pagingGroupCount; i++) {
            pageGroupList.add(i);

            if(i == pageCount) {
                break;
            }
        }

        //json을 위한 맵
        Map<String, Object> listArticle = new HashMap<>();
        listArticle.put("searchKeyword", searchKeyword);
        listArticle.put("selectComponent", selectComponent);
        listArticle.put("searchNull", searchNull);
        listArticle.put("pagingEndNum", pagingEndNum);
        listArticle.put("pageCount", pageCount);
        listArticle.put("pagingGroupCountInt", pagingGroupCountInt);
        listArticle.put("pagingGroupStartNum", pagingGroupStartNum);
        listArticle.put("pageGroupList", pageGroupList);
        listArticle.put("pageNum", pageNum);
        listArticle.put("articleTotalCount", articleTotalCount);
        listArticle.put("pageView", pageView);
        listArticle.put("articles", result);



        return listArticle;
    }


    @PostMapping(value = "/articleWriteDbAjax")  //게시물 등록 로직 실행
    public Map<String, Object> articleWriteDb(HttpServletRequest request, HttpServletResponse response, Model model){

        // html에서 정보 받아와 변수 저장
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");

        // 맵형식으로 저장
        Map<String, String> article = new HashMap<String, String>();

        article.put("title", title);
        article.put("writer", writer);
        article.put("content", content);

        Map<String, Object> result = new HashMap<>();

        // 예외처리 문자열 공백 검사
        if(content.isBlank() || writer.isBlank() || title.isBlank()) {
            result.put("code",33);
            return result;
        } else {
            // 서비스단으로 이동
            int articles = boardService.writeBoardArticle(article);

             result.put("code",articles);

            // 목록으로 redirect
            return result;
        }
    }

    @PostMapping("/modifyArticlePageAjax")   //게시물 수정 html 이동 로직 실행
    public Map<String, Object> modifyArticlePageAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, String> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(boardNo);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        Map<String,Object>modifyResult = new HashMap<String,Object>();
        modifyResult.put("article", viewData);
        modifyResult.put("pageNum", rollBackPage);

        return modifyResult;
    }

    @PostMapping("/modifyArticleDbAjax")  //게시물 수정 로직 실행
    public Map<String, Object> modifyArticleDb(HttpServletRequest request, HttpServletResponse response, Model model) {

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
        // 수정 정보 전달받음
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        String boardNo = request.getParameter("boardNo");

        // db 전달맵
        Map<String, String> modifyArticle = new HashMap<String, String>();

        modifyArticle.put("title", title);
        modifyArticle.put("writer", writer);
        modifyArticle.put("content", content);
        modifyArticle.put("boardNo", boardNo);


        // 클라이언트 result Map
        Map<String, Object> sendResult = new HashMap<String, Object>();

        //내용 문자 공백시 예외처리
        if(content.isBlank() || writer.isBlank() || title.isBlank()) {
            sendResult.put("pageNum", rollBackPage);
            sendResult.put("code", 33);

            return sendResult;
        }else{
            // 서비스단으로 이동
            int modifyArticles = boardService.modifyArticle(modifyArticle);
            sendResult.put("code",modifyArticles);
            sendResult.put("pageNum",rollBackPage);

            return sendResult;
        }
    }

    @PostMapping("/deleteArticleAjax")  //게시물 삭제 로직 실행
    public Map<String, Object> deleteArticle(HttpServletRequest request, HttpServletResponse response, Model model) {
        // boradno전달받음
        String boardNo = request.getParameter("boardNo");

        Map<String, String> delete = new HashMap<String, String>();

        delete.put("boardNo", boardNo);

        int deleteArticles = boardService.deleteArticle(delete);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        Map<String, Object> deleteResult = new HashMap<String, Object>();
        deleteResult.put("code",deleteArticles);
        deleteResult.put("pageNum",rollBackPage);

        // 목록으로 redirect
        return deleteResult;
    }

}