package com.tosky.board2.controller;


import com.tosky.board2.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.tosky.board2.util.Utility.strToInt;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;

    @PostMapping(value = "/articleWritePage")  //게시물 등록 html 이동
    public String articleWritePage(HttpServletRequest request, HttpServletResponse response, Model model) {
        //예외처리시 기존 입력정보 받아와 변수 저장
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        String pageNum = request.getParameter("pageNum");

        // 맵형식으로 저장
        Map<String, String> article = new HashMap<String, String>();

        article.put("title", title);
        article.put("writer", writer);
        article.put("content", content);
        article.put("pageNum", pageNum);

        model.addAttribute("article", article);

//        return "BoardWrite";
        return "BoardWriteTableVerJs";
    }

    @PostMapping(value = "/articleWriteDb")  //게시물 등록 로직 실행
    public String articleWriteDb(HttpServletRequest request, HttpServletResponse response, Model model) {
        //  html에서 정보 받아와 변수 저장
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        String pageNum = request.getParameter("pageNum");

        // 맵형식으로 저장
        Map<String, Object> article = new HashMap<String, Object>();

        article.put("title", title);
        article.put("writer", writer);
        article.put("content", content);
        article.put("pageNum", pageNum);

        //등록첫페이지 오류문구 출력방지
        int countTry = 0;

        //  예외처리 문자열 공백 검사
        if(content.isBlank() || writer.isBlank() || title.isBlank()) {
            countTry = 1;
            model.addAttribute("countTry", countTry);
            model.addAttribute("article", article);

//            return "BoardWrite";
            return "BoardWriteTableVerJs";
        } else {
            // 서비스단으로 이동
            int articles = boardService.writeBoardArticle(article);

            //  목록으로 redirect
            return "redirect:/list?pageNum=1";
        }
    }

    @GetMapping("/viewArticle")   //게시물 조회 html 이동 로직 실행
    public String viewArticle(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //게시물목록에서 조회할 게시물번호 전달받음
        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, Object> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(boardNo);
        model.addAttribute("article", viewData);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
        model.addAttribute("pageNum", rollBackPage);

        return "BoardViewTableVer";
    }

    @PostMapping("/modifyArticlePage")   //게시물 수정 html 이동 로직 실행
    public String modifyArticlePage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //게시물목록에서 조회할 게시물번호 전달받음
        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, Object> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(boardNo);
        model.addAttribute("article", viewData);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
        model.addAttribute("pageNum", rollBackPage);

//        return "BoardModify";
//        return "BoardModifyTableVer";
        return "BoardModifyTableVerJs";
    }

    @PostMapping("/modifyArticleDb")  //게시물 수정 로직 실행
    public String modifyArticleDb(HttpServletRequest request, HttpServletResponse response, Model model) {

        // 수정 정보 전달받음
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        String boardNo = request.getParameter("boardNo");

        // 맵형식으로 저장
        Map<String, Object> modifyArticle = new HashMap<String, Object>();

        modifyArticle.put("title", title);
        modifyArticle.put("writer", writer);
        modifyArticle.put("content", content);
        modifyArticle.put("boardNo", boardNo);

        // 서비스단으로 이동
        int modifyArticles = boardService.modifyArticle(modifyArticle);

        //내용 문자 공백시 예외처리
        if(content.isBlank() || writer.isBlank() || title.isBlank()) {
            int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
            model.addAttribute("pageNum", rollBackPage);
            model.addAttribute("article", modifyArticle);

//            return "BoardModifyTableVer";
            return "BoardModifyTableVerJs";
        }

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        //기존 목록으로 redirect
        return "redirect:/list?pageNum=" + rollBackPage;
    }

    @PostMapping("/deleteArticle")  //게시물 삭제 로직 실행
    public String deleteArticle(HttpServletRequest request, HttpServletResponse response, Model model) {
        // boradno전달받음
        String boardNo = request.getParameter("boardNo");

        Map<String, Object> delete = new HashMap<String, Object>();

        delete.put("boardNo", boardNo);

        int deleteArticles = boardService.deleteArticle(delete);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        //  목록으로 redirect
        return "redirect:/list?pageNum=" + rollBackPage;
    }

    @GetMapping(value = "/list")  //게시물 목록 생성 로직 실행
    public String articleList(HttpServletRequest request, HttpServletResponse response, Model model) {
        //검색요소
        String searchKeyword = request.getParameter("searchKeyword");
        String selectComponent = request.getParameter("selectComponent");

        // 페이지 n개씩 띄우기 지정 변수
        double pageViewSetting = 10;
        int pageView = (int) pageViewSetting;

        // 페이지리스트번호
        String pageNumStr = request.getParameter("pageNum");


        // pageNum이 문자열로 들어올경우 처리
        // FIXME: pageNum 사용 수정
        if(strToInt(pageNumStr)) {
            double pageNum = Double.parseDouble(pageNumStr);
        } else {
            return "BoardListException";
        }

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
            searchNull=1;
        }

        // 페이지리스트List생성
        List<Integer> pageCountList = new ArrayList<>();

        for(int i = 1; i < pageCount + 1; i++) {
            pageCountList.add(i);
        }

        //페이징 그룹 갯수
        double pagingGroupCount = 5;
        int pagingGroupCountInt = (int)pagingGroupCount;

        //페이징 그룹 총 갯수 산출
        int pagingGroupTotalCount = (int) Math.ceil(pageCount/pagingGroupCount);

        //페이징 그룹 끝페이지 산출
        int pagingEndNum = (pagingGroupTotalCount * pagingGroupCountInt);

        //페이지 그룹중 현재 그룹계산
        int pagingGroupNum = (int) Math.ceil(pageNum/pagingGroupCount);

        //그룹 시작번호
        int pagingGroupStartNum = (int) (1+((pagingGroupNum-1)*pagingGroupCount));

        // 페이지그룹List생성
        List<Integer> pageGroupList = new ArrayList<>();

        for(int i = pagingGroupStartNum; i<pagingGroupStartNum+pagingGroupCount;i++){
            pageGroupList.add(i);

            if(i==pageCount){
                break;
            }
        }

        //검색요소 유지위한 값 전달

        model.addAttribute("searchKeyword",searchKeyword);
        model.addAttribute("selectComponent",selectComponent);
        model.addAttribute("searchNull", searchNull);

        //페이지 그룹 페이징 그룹 끝페이지 전달
        model.addAttribute("pagingEndNum",pagingEndNum);

        //총 페이지그룹 갯수전달
        model.addAttribute("pageCount",pageCount);

        //페이지 그룹 갯수 전달
        model.addAttribute("pagingGroupCountInt",pagingGroupCountInt);

        //페이지그룹시작번호 전달
        model.addAttribute("pagingGroupStartNum",pagingGroupStartNum);

        // html에 전달하여 반복생성
        model.addAttribute("pageGroupList", pageGroupList);

        //수정,삭제 이후 기존 페이지 롤백을 위한 페이지 번호 전달
        model.addAttribute("pageNum", pageNum);

        //게시물 번호 넘버링을 위한 게시물 총갯수 전달
        model.addAttribute("articleTotalCount", articleTotalCount);

        //게시물 번호 넘버링을 위한 한페이지에 표시 갯수 변수인자 전달
        model.addAttribute("pageView", pageView);

        // html게시판 목록인자 전달
        model.addAttribute("articles", result);

//        if(pageNum>pageCount){
//            return "redirect:/list?pageNum=" + pageCount;
//        }

        return "BoardListTableVerJs";

    }


    @PostMapping("/searchArticle")  //게시물 검색 로직 실행
    public String searchArticle(HttpServletRequest request, HttpServletResponse response, Model model) {
        //검색  요소 전달 받음


        //검색 요소 맵에 저장
//        Map<String,String> searchArticle= new HashMap<String, String>();
//
//        searchArticle.put("searchKeyword",searchKeyword);
//        searchArticle.put("selectComponent",selectComponent);
//
//        List<Map<String,String>> searchReturn = new ArrayList<Map<String, String>>();
//
//        searchReturn = boardService.searchArticle(searchArticle);





        return "/borderlist";
    }


//    @GetMapping(value = "/listAjaxTest")  //게시물 목록 생성 로직 실행
//    public String listAjaxTest(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        int pageNum = 1;
//        if(!StringUtils.isEmpty(request.getParameter("pageNum"))){
//            pageNum = Integer.parseInt(request.getParameter("pageNum"));
//        }
//        model.addAttribute("pageNum", pageNum);
//
//        return "ajax/BoardListAjax";
//    }

    @PostMapping(value = "/articleWritePageAjaxView")  //게시물 등록 html 이동
    public String articleWritePageAjaxView(HttpServletRequest request, HttpServletResponse response, Model model) {

        return "ajax/boardWriteAjax";
    }
    @GetMapping("/modifyArticlePageAjax")   //게시물 수정 html 이동 로직 실행
    public String modifyArticlePageAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //게시물목록에서 조회할 게시물번호 전달받음
        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
//        Map<String, String> viewData = new HashMap<>();
//        viewData = boardService.getViewArticle(boardNo);
//        model.addAttribute("article", viewData);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
//        model.addAttribute("pageNum", rollBackPage);

        return "ajax/BoardModifyAjax";
    }
//    @GetMapping(value = "/userSignUpPageAjaxView")  //
//    public String userSignUpPageAjaxView(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        return "ajax/signUp";
//    }
//    @GetMapping(value = "/")  //
//    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
//
//        return "ajax/login";
//    }
//
}