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

        // 페이지 n개씩 띄우기 지정 변수 계산을 위해 실수형 선언
        double pageViewSetting = 10;
        //정수형 변환
        int pageView = (int) pageViewSetting;

        //페이징그룹 올림 계산을 위한 실수형 선언
        double pageNumDouble = Double.parseDouble(pageNumStr);
        //페이지번호 올림 계산
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
        String secretCheck = request.getParameter("secretCheck");
        String articlePw = request.getParameter("articlePw");

        // 맵형식으로 저장
        Map<String, String> article = new HashMap<String, String>();

        article.put("title", title);
        article.put("writer", writer);
        article.put("content", content);
        article.put("secretCheck", secretCheck);
        article.put("articlePw", articlePw);

        Map<String, Object> result = new HashMap<>();

        if(articlePw.length() > 4){
            result.put("code","newPwError");
        }

        // 예외처리 문자열 공백 검사
        if(content.isBlank() || writer.isBlank() || title.isBlank()) {
            result.put("code","textError");     //공백이있을경우 텍스트에러 입력
        } else {
            // 서비스단으로 이동
            int articles = boardService.writeBoardArticle(article);
            if (articles == 1){
             result.put("code","success");      //db에 이상없이 저장되어 return 값이 1이온경우 성공 입력
            }
        }
        return result;
    }

    @PostMapping("/viewArticlePageAjax")   //게시물 조회 로직 실행
    public Map<String, Object> viewArticlePageAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

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

    @PostMapping("/secretCheckAjax")   //비밀글 체크 로직
    public Map<String, Object> secretCheckAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, String> pwCheckData = new HashMap<>();
        pwCheckData = boardService.getViewArticlePwCheck(boardNo);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        Map<String,Object>modifyResult = new HashMap<String,Object>();
        modifyResult.put("article", pwCheckData);
        modifyResult.put("pageNum", rollBackPage);

        return modifyResult;
    }

    @PostMapping("/pwCheckAjax")   //비밀글 비밀번호 조회 및 비교 로직
    public Map<String, Object> pwCheckAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int boardNo = Integer.parseInt(request.getParameter("boardNo"));
        String userPw = request.getParameter("userPw");

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, String> pwJoinData = new HashMap<>();
        pwJoinData = boardService.pwJoinData(boardNo);
        String articlePw = pwJoinData.get("articlePassword");
        String result;

        //db에 저장된 비번과 유저가 입력한 비밀번호 비교하여 결과 맵핑
        if(Objects.equals(articlePw, userPw)){
            result = "success";
        }else {
            result = "fail";
        }


        //기존페이지 저장
        int pageNum = Integer.parseInt(request.getParameter("pageNum"));

        Map<String,Object>pwJoinResult = new HashMap<String,Object>();

        pwJoinResult.put("pageNum", pageNum);
        pwJoinResult.put("result", result);

        return pwJoinResult;
    }

    @PostMapping("/modifyArticleDbAjax")  //게시물 수정 로직 실행
    public Map<String, Object> modifyArticleDb(HttpServletRequest request, HttpServletResponse response, Model model) {
        //html 내 정보 변수 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
        String boardNo = request.getParameter("boardNo");
        String title = request.getParameter("title");
        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        String secretCheck = request.getParameter("secretCheck");
        String userPw = request.getParameter("articlePw");      //공개글>비밀글 신규 pw
//        String reCheckPw = request.getParameter("reCheckPw");   //팝업창 비밀번호

        //기존 저장된 정보 불러옴
        Map<String, String> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(Integer.parseInt(boardNo));

        //기존 저장된 비밀글 확인 flag, pw 번수명 선언
        String dbSecretCheck =String.valueOf(viewData.get("isSecret"));
        String articlePw = viewData.get("articlePassword");

        // 클라이언트 result Map 선언 및 공통 전달인자 입력
        Map<String, Object> sendResult = new HashMap<String, Object>();
        sendResult.put("pageNum",rollBackPage);            //pageNum 정보 저장

        if(content.isBlank() || writer.isBlank() || title.isBlank()) {  //내용에 공백이 있을 경우

            sendResult.put("code", "textWrong");
            return sendResult;
            }

        // db 전달맵 선언 및 공통 전달인자 입력
        Map<String, String> modifyArticle = new HashMap<String, String>();
        modifyArticle.put("title", title);
        modifyArticle.put("writer", writer);
        modifyArticle.put("content", content);
        modifyArticle.put("boardNo", boardNo);

        System.out.println(dbSecretCheck);
        if(Objects.equals(dbSecretCheck, "true")) {              //기존 비밀글일 경우

            if(Objects.equals(articlePw, userPw)){               //입력 비밀번호 대조 통과시

                if(Objects.equals(secretCheck, "0")){            //비밀글>공개글 처리 로직
                    modifyArticle.put("secretCheck", secretCheck);  //flag 변경
                    modifyArticle.put("articlePw", "");             //비밀번호 삭제

                } else if(Objects.equals(secretCheck, "1")) {    //비밀글>비밀글

                }
            }else {                                                //입력비밀번호가 기존비밀번호와 다른경우
                sendResult.put("code","pwWrong");
                return sendResult;
            }
        } else if(Objects.equals(dbSecretCheck, "false")) {     //기존 공개글 일 경우

            if(Objects.equals(secretCheck, "1")){               //공개글>비밀글
                if(userPw.length() > 4) {
                    modifyArticle.put("secretCheck", secretCheck);     //flag 변경
                    modifyArticle.put("articlePw", userPw);             //비밀번호 신규추가
                }else if(userPw.length() > 9){
                    sendResult.put("code","newPwWrong");
                    return sendResult;
                }else{
                    sendResult.put("code","newPwWrong");
                    return sendResult;
                }
            }
        }

        //수정 정보 db 전달
        int modifyArticles = boardService.modifyArticle(modifyArticle);
        if(modifyArticles == 1) {               //처리결과가 이상이 없을 경우
            sendResult.put("code", "success");  //성공 코드
        }else{
            sendResult.put("code", "dbError");  //아닐경우 실패코드
        }
        return sendResult;
    }

    @PostMapping("/deleteArticleAjax")  //게시물 삭제 로직 실행
    public Map<String, Object> deleteArticle(HttpServletRequest request, HttpServletResponse response, Model model) {

        // boradNo, 비밀글 여부, 유저 입력 pw 전달받음
        String boardNo = request.getParameter("boardNo");
        String secretCheck = request.getParameter("secretCheck");
        String reCheckPw = request.getParameter("reCheckPw");

        Map<String, String> pwJoinData = new HashMap<>();

        //db삭제 인자 저장
        Map<String, String> delete = new HashMap<String, String>();
        delete.put("boardNo", boardNo);

        //db에서 기존 pw 조회
        pwJoinData = boardService.pwJoinData(Integer.parseInt(boardNo));
        String articlePw = pwJoinData.get("articlePassword");

        //결과값 저장 변수 선언언
        String result = null;
        int deleteArticles = 0;

        //비밀글일경우
        if(Objects.equals(secretCheck, "true")) {
            if(Objects.equals(articlePw, reCheckPw)) {                //db에 저장된 비번과 유저가 입력한 비밀번호 비교
                deleteArticles = boardService.deleteArticle(delete);  //db 삭제 실행
            } else {                //비밀번호가 다를경우
                result = "pwFail";  //code에 비밀번호 오류 입력
            }
        }else{                                                        //공개글일경우
            deleteArticles = boardService.deleteArticle(delete);      //db 삭제 실행
        }
        if(deleteArticles == 1) {   //서비스 실행 후 이상없이 return 값이 1인경우
            result = "success";     //code 에 성공 입력
        }

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        Map<String, Object> deleteResult = new HashMap<String, Object>();
        deleteResult.put("code", result);
        deleteResult.put("pageNum", rollBackPage);

        // 목록으로 redirect
        return deleteResult;
    }
    @PostMapping("/signUpCheck")  //회원가입 체크 로직
    public Map<String, Object> signUpCheck(HttpServletRequest request, HttpServletResponse response, Model model) {

        String userId = request.getParameter("userId");

        Map<String, Object> signUpCheck = new HashMap<>();

        signUpCheck.put("userId",userId);

        int result = boardService.signUpCheck(signUpCheck);
        String resultCode;

        if (result == 0){
            resultCode = "success";
        }else{
            resultCode = "fail";
        }

        Map<String, Object> signUpCheckResult = new HashMap<>();

        signUpCheckResult.put("code",resultCode);


        // 목록으로 redirect
        return signUpCheckResult;
    }
    @PostMapping("/signUp")  //회원가입
    public Map<String, Object> signUp(HttpServletRequest request, HttpServletResponse response, Model model) {

        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");
        String userTel = request.getParameter("userTel");

        Map<String, Object> signUpArticle = new HashMap<>();

        signUpArticle.put("userId",userId);
        signUpArticle.put("userPw",userPw);
        signUpArticle.put("userTel",userTel);

        int result = boardService.signUp(signUpArticle);

        Map<String, Object> signUpResult = new HashMap<>();

        return signUpResult;
    }
    @PostMapping("/login")  //로그인 로직 실행
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response, Model model) {

        //아이디와 비밀번호 입력 받음
        String userId = request.getParameter("userId");
        String userPw = request.getParameter("userPw");

        //db 전달 맵 생성
        Map<String, Object> loginArticle = new HashMap<>();
        loginArticle.put("userId",userId);

        //아이디로 비밀번호 조회해옴
        Map<String, Object> result = boardService.loginCheck(loginArticle);
        String dbPw = (String) result.get("userPassWord");
        String resultCode;

        //유저 입력 pw 와 아이디에 있는 비밀번호 비교
        if(Objects.equals(userPw , dbPw)){
            resultCode = "success";
        }else {
            resultCode = "fail";
        }

        Map<String, Object> loginResult = new HashMap<>();
        loginResult.put("code",resultCode);

        return loginResult;
    }

}