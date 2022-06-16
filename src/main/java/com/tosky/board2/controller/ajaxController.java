package com.tosky.board2.controller;


import com.tosky.board2.service.BoardService;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static com.tosky.board2.util.Utility.strToInt;

@RestController
public class ajaxController {

    @Autowired
    BoardService boardService;

    @PostMapping(value = "/listAjax")  //게시물 목록 생성 로직 실행
    public Map<String, Object> articleList(Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) {
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

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if (principal != null) {
            sessionInfo = principal.getName();
            session = "use";
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
        listArticle.put("sessionInfo", sessionInfo);
        listArticle.put("session", session);

        return listArticle;
    }


    @PostMapping(value = "/articleWriteDbAjax")  //게시물 등록 로직 실행
    public Map<String, Object> articleWriteDb(Principal principal,HttpServletRequest request, HttpServletResponse response, Model model){

        // html에서 정보 받아와 변수 저장
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        int secretCheck = Integer.parseInt(request.getParameter("secretCheck"));
        String userPw = request.getParameter("articlePw");

        boolean isSecret;
        if(secretCheck == 1) {
            isSecret = true;
        } else {
            isSecret = false;
        }
        // ajax 리턴값 맵선언
        Map<String, Object> result = new HashMap<>();

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if (principal != null) {
            sessionInfo = principal.getName();
            session = "use";
        }
        if(session.equals("null")) {
            result.put("code","잘못된 접근입니다");
            return result;
        }

        // db전달 인자 맵형식으로 저장
        Map<String, Object> article = new HashMap<String, Object>();
        article.put("title", title);
        article.put("writer", sessionInfo);
        article.put("content", content);
        article.put("secretCheck", secretCheck);



        if(isSecret){                                           //비밀글 선택시
            if(userPw.length() >= 4 && userPw.length() <= 8){   //비밀번호 자릿수 검사
                article.put("articlePw", userPw);               //통과시 dbMap에 저장
            } else {
                result.put("code", "비밀번호는 4-8자리 만 가능.");  //비밀번호 오류
                return result;
            }
        }

        // 예외처리 문자열 공백 검사
        if(content.isBlank() || title.isBlank()) {
            result.put("code","빈칸없이 작성하시오.");     //공백이있을경우 텍스트에러 입력
        } else {
            // 서비스단으로 이동
            int articles = boardService.writeBoardArticle(article);
            if (articles > 0){
             result.put("code","success");      //db에 이상없이 저장되어 return 값이 1이온경우 성공 입력
            }
        }
        return result;
    }

    @PostMapping("/viewArticlePageAjax")   //게시물 조회 로직 실행
    public Map<String, Object> viewArticlePageAjax(Principal principal,HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, Object> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(boardNo);

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if (principal != null) {
            sessionInfo = principal.getName();
            session = "use";
        }

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        Map<String,Object>viweResult = new HashMap<String,Object>();
        viweResult.put("article", viewData);
        viweResult.put("pageNum", rollBackPage);
        viweResult.put("session", session);
        viweResult.put("sessionInfo", sessionInfo);

        return viweResult;
    }

    @PostMapping("/secretCheckAjax")   //비밀글 체크 로직
    public Map<String, Object> secretCheckAjax(Principal principal, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int boardNo = Integer.parseInt(request.getParameter("boardNo"));

        //게시물 번호를 인자로 담아 조회해온 게시글을 리스트<맵>형식으로 html에 전달
        Map<String, Object> viewData = new HashMap<>();
        viewData = boardService.getViewArticle(boardNo);

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if(principal != null) {
            sessionInfo = principal.getName();
            session = "use";
        }

        Boolean isSecret = (Boolean) viewData.get("isSecret");
        String writer = (String) viewData.get("writer");

        String result = "";
        if(isSecret) {
            if(writer.equals(sessionInfo)) {
                result = "공개";
            } else {
                result = "비밀";
            }
        } else {
            result = "공개";
        }

        Map<String,Object>modifyResult = new HashMap<String,Object>();
        modifyResult.put("article", result);
        modifyResult.put("pageNum", rollBackPage);

        return modifyResult;
    }

    @PostMapping("/pwCheckAjax")   //비밀글 비밀번호 조회 및 비교 로직
    public Map<String, Object> pwCheckAjax(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
        int boardNo = Integer.parseInt(request.getParameter("boardNo"));
        String userPw = request.getParameter("userPw");

        //boardNo로 기존 저장된 pw 조회
        Map<String, Object> pwCheckData = new HashMap<>();
        pwCheckData = boardService.getViewArticlePwCheck(boardNo);
        String articlePw = (String) pwCheckData.get("articlePassword");
        //결과 맵핑용 변수 선언
        String result;
        //db에 저장된 pw와 유저가 입력한 pw 비교하여 결과 맵핑
        if(Objects.equals(articlePw, userPw)) {
            result = "success";
        } else {
            result = "fail";
        }
        //ajax 전달용 Map
        Map<String,Object>pwJoinResult = new HashMap<String,Object>();

        pwJoinResult.put("pageNum", pageNum);
        pwJoinResult.put("result", result);

        return pwJoinResult;
    }

    @PostMapping("/modifyArticleDbAjax")  //게시물 수정 로직 실행
    public Map<String, Object> modifyArticleDb(Principal principal,HttpServletRequest request, HttpServletResponse response, Model model) {
        //html 내 정보 변수 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));
        String boardNo = request.getParameter("boardNo");
        String title = request.getParameter("title");
//        String writer = request.getParameter("writer");
        String content = request.getParameter("content");
        boolean userSecretCheck = Boolean.parseBoolean(request.getParameter("secretCheck"));
        String userPw = request.getParameter("articlePw");      //공개글>비밀글 신규 pw
//        String reCheckPw = request.getParameter("reCheckPw");   //팝업창 비밀번호

        int secretCheck;        //db저장용 인트형 변환
        if(userSecretCheck) {
            secretCheck = 1;
        } else {
            secretCheck = 0;
        }

        //기존 저장된 정보 불러옴
        Map<String, Object> pwCheckData = new HashMap<>();
        pwCheckData = boardService.getViewArticlePwCheck(Integer.parseInt(boardNo));
        //기존 저장된 비밀글 확인 flag, pw 번수명 선언
        boolean dbSecretCheck = (boolean) pwCheckData.get("isSecret");
        String articlePw = (String) pwCheckData.get("articlePassword");
        String writer = (String) pwCheckData.get("writer");


        // 클라이언트 result Map 선언 및 공통 전달인자 입력
        Map<String, Object> sendResult = new HashMap<String, Object>();
        sendResult.put("pageNum",rollBackPage);            //pageNum 정보 저장

        //내용에 공백검사
        if(content.isBlank() || title.isBlank()) {
            sendResult.put("code", "textWrong");
            return sendResult;
        }

        // db 전달맵 선언 및 공통 전달인자 입력
        Map<String, Object> modifyArticle = new HashMap<String, Object>();
        modifyArticle.put("title", title);
        modifyArticle.put("content", content);
        modifyArticle.put("boardNo", boardNo);

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if (principal != null) {
            sessionInfo = principal.getName();
            session = "use";
        }

        if(writer.equals(sessionInfo) && session.equals("use")){
            if(dbSecretCheck) {                                           //기존 비밀글일 경우
                if(articlePw.equals(userPw)) {                            //입력 비밀번호 대조 통과하면서 비밀글>공개글일 경우 처리 로직
                    if(!userSecretCheck) {
                        modifyArticle.put("secretCheck", secretCheck);    //flag 변경
                        modifyArticle.put("articlePw", "");               //비밀번호 삭제
                    }
                } else {                                                  //입력비밀번호가 기존비밀번호와 다른경우
                    sendResult.put("code","기존의 비밀번호를 입력해야 합니다,");
                    return sendResult;
                }
            } else {                                                     //기존 공개글 일 경우
                if(userSecretCheck) {                                    //공개글>비밀글
                    if(userPw.length() >= 4 && userPw.length() <= 8) {
                        modifyArticle.put("secretCheck", secretCheck);   //flag 변경
                        modifyArticle.put("articlePw", userPw);          //비밀번호 신규추가
                    } else {                                             //비밀번호 양식이 아닐경우
                        sendResult.put("code","비밀번호는 4-8자리 숫자만 가능합니다");
                        return sendResult;
                    }
                }
            }
        } else {
            sendResult.put("code","못된 사용자군요 이상한짓하지마세요");
            return sendResult;
        }

        //수정 정보 db 전달
        int modifyArticles = boardService.modifyArticle(modifyArticle);
        if(modifyArticles > 0) {                //처리결과가 이상이 없을 경우
            sendResult.put("code", "success");  //성공 코드
        } else {
            sendResult.put("code", "예기치 못한 에러가 생겼습니다.");  //아닐경우 실패코드
        }
        return sendResult;
    }

    @PostMapping("/deleteArticleAjax")  //게시물 삭제 로직 실행
    public Map<String, Object> deleteArticle(Principal principal,HttpServletRequest request, HttpServletResponse response, Model model) {

        // boradNo, 비밀글 여부, 유저 입력 pw 전달받음
        String boardNo = request.getParameter("boardNo");
        String reCheckPw = request.getParameter("reCheckPw");

        //db삭제 인자 저장
        Map<String, Object> delete = new HashMap<String, Object>();
        delete.put("boardNo", boardNo);

        //db에서 기존 pw 조회
        Map<String, Object> pwCheckData = new HashMap<>();
        pwCheckData = boardService.getViewArticlePwCheck(Integer.parseInt(boardNo));
        String articlePw = (String) pwCheckData.get("articlePassword");
        boolean dbSecretCheck = (boolean) pwCheckData.get("isSecret");
        String writer = (String) pwCheckData.get("writer");

        //결과값 저장 변수 선언언
        String result = "예기치 못한 에러가 발생했습니다.";
        int deleteArticles = 0;

        //세션 정보 확인
        String sessionInfo = "not login";
        String session = "null";
        if (principal != null) {
            sessionInfo = principal.getName();
            session = "use";
        }


        Map<String, Object> deleteResult = new HashMap<String, Object>();
        if(writer.equals(sessionInfo) && session.equals("use")) {         //기존등록자 접근인지 확인
            //비밀글일경우
            if(dbSecretCheck) {
                if(articlePw.equals(reCheckPw)) {                         //db에 저장된 비번과 유저가 입력한 비밀번호 비교
                    deleteArticles = boardService.deleteArticle(delete);  //db 삭제 실행
                } else {                                                  //비밀번호가 다를경우
                    result = "기존 입력 비밀번호를 입력해야 합니다.";           //code에 비밀번호 오류 입력
                }
            } else {                                                      //공개글일경우
                deleteArticles = boardService.deleteArticle(delete);      //db 삭제 실행
            }

            //서비스 실행 후 이상없이 return 값이 1인경우
            if(deleteArticles > 0) {
                result = "success";     //code 에 성공 입력
            }
        } else {
            deleteResult.put("code","못된 사용자군요 이상한짓하지마세요");
            return deleteResult;
        }

        //기존페이지 저장
        int rollBackPage = Integer.parseInt(request.getParameter("pageNum"));


        deleteResult.put("code", result);
        deleteResult.put("pageNum", rollBackPage);
        return deleteResult;
    }

    @PostMapping("/signUpCheck")  //회원가입 체크 로직
    public Map<String, Object> signUpCheck(HttpServletRequest request, HttpServletResponse response, Model model) {
        String userId = request.getParameter("userId");
        Map<String, Object> signUpCheck = new HashMap<>();
        signUpCheck.put("userId",userId);

        int result = boardService.signUpCheck(signUpCheck);
        String resultCode;

        if (result == 0) {
            resultCode = "success";
        } else {
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

//    @PostMapping("/login")  //로그인 로직 실행
//    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response, Model model) {
//        //아이디와 비밀번호 입력 받음
//        String userId = request.getParameter("userId");
//        String userPw = request.getParameter("userPw");
//
//        //db 전달 맵 생성
//        Map<String, Object> loginArticle = new HashMap<>();
//        loginArticle.put("userId",userId);
//
//        //아이디로 비밀번호 조회해옴
//        Map<String, Object> result = boardService.loginCheck(loginArticle);
//        String dbPw = (String) result.get("userPassWord");
//        String resultCode;
//
//        //유저 입력 pw 와 아이디에 있는 비밀번호 비교
//        if(Objects.equals(userPw , dbPw)) {
//            resultCode = "success";
//        } else {
//            resultCode = "fail";
//        }
//
//        Map<String, Object> loginResult = new HashMap<>();
//        loginResult.put("code",resultCode);
//
//        return loginResult;
//    }
}