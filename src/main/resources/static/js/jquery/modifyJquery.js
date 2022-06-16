window.onload = function() {
    //페이지로딩시 조회페이지
    secretCheckAjax();
};

function secretCheckAjax() {
//여기부터
    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");

    let checkArticle = new FormData();
    checkArticle.append("boardNo", boardNo);
    checkArticle.append("pageNum", pageNum);

    $.ajax( {
        url :"http://localhost:8080/secretCheckAjax",    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : 'POST',      // GET, PUT
        data : checkArticle,      // 전송할 데이터
        dataType : 'json',        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {

            $(".container").css("display" , "none");        //비밀번호 입력시 html 숨김 처리

            if(jsonData.article.isSecret == 1) {                        //비밀글일경우
                $("#pwCheckBtn").attr("onclick","pwCheckAjax()")//팝업창 확인버튼 온클릭 이벤트 변경
                $(".modal").fadeIn();                                   //비밀번호 입력 모달창 띄우기
                $("#pageNum").val(pageNum)                              //취소 버튼 입력시 롤백을 위한 페이지번호 배치

            }else if(jsonData.article.isSecret == 0){                 //공개글일경우 조회로직 실행
                viewAjax();
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function pwCheckAjax() {
//여기부터
    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");
    let userPw = $("#pwInput").val();

    let checkArticle = new FormData();
    checkArticle.append("boardNo", boardNo);
    checkArticle.append("pageNum", pageNum);
    checkArticle.append("userPw", userPw);

    $.ajax( {
        url :"http://localhost:8080/pwCheckAjax",    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : 'POST',      // GET, PUT
        data : checkArticle,      // 전송할 데이터
        dataType : 'json',        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
            if(jsonData.result == "success"){
                viewAjax();
            }else if(jsonData.result == "fail"){
                alert("잘못된 비밀번호 입니다.");
            }

        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function viewAjax() {

    //url에서 게시글 번호 및 페이지번호 추출
    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");

    let viewArticle = new FormData();
    viewArticle.append("boardNo", boardNo);
    viewArticle.append("pageNum", pageNum);

    $.ajax({
        url :'http://localhost:8080/viewArticlePageAjax',    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : 'POST',      // GET, PUT
        data : viewArticle,      // 전송할 데이터
        dataType : 'json',        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
            //html 숨김처리 해제
            $(".container").css("display","");
            $(".modal").fadeOut();

            //서버에서 갖고온 내용 변수할당
            let writer = jsonData.article.writer;
            let title = jsonData.article.title;
            let content = jsonData.article.content;
            let boardNo = jsonData.article.boardNo;
            let isSecret = jsonData.article.isSecret;
            let pageNum = jsonData.pageNum;

            //내용 할당 및 수정불가
            $("#writer").val(writer)
            $("#title").val(title)
            $("#boardNo").val(boardNo)
            $("#pageNum").val(pageNum)
            $("#content").html(content)
            $("#isSecretNum").val(isSecret)

            //비밀글일경우 체크박스 체크
            if(isSecret == 1) {
                $('input:checkbox[id="secretCheck"]').attr("checked", true);
                // $("#articlePw").css("display" , "none");
            }

            $(".viewJquery").attr("disabled" , true);

            //경고태그 히든처리
            $(".exception").css("display" , "none");

            //삭제 버튼 숨김
            $(".modifyShow").css("display","none")
            $(".viewShow").css("display","")
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function delAjax() {
    let delArticle = new FormData();
    delArticle.append("boardNo", $("#boardNo").val());
    delArticle.append("pageNum", $("#pageNum").val());
    delArticle.append("secretCheck", $("#isSecretNum").val());  //기존 비밀글여부
    // delArticle.append("reCheckPw", $("#pwInput").val());        //팝업창 pw
    delArticle.append("reCheckPw", $("#articlePw").val());        //팝업창 pw

    $.ajax( {
        url :"http://localhost:8080/deleteArticleAjax",    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : 'POST',      // GET, PUT
        data : delArticle,      // 전송할 데이터
        dataType : 'json',        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
            if(jsonData.code == "success") {
                alert("삭제완료!");
                location.href = "/listAjaxTest?pageNum="+jsonData.pageNum;  //이전 페이지로
            }else if(jsonData.code == "pwFail"){
                alert("잘못된 비밀번호 입니다.");
            }else {
                alert("관리자에게 문의 하세요.01038107945");
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function modifyAjax() {
    //비밀글 체크여부에따른 값 할당
    let isSecretCheck;
    if($('input:checkbox[id="secretCheck"]').is(":checked") == true) {
        isSecretCheck = true;
    }else{
        isSecretCheck = false;
    }
    let modifyArticle = new FormData();
    modifyArticle.append("boardNo", $("#boardNo").val());
    modifyArticle.append("pageNum", $("#pageNum").val());
    modifyArticle.append("title", $("#title").val());
    modifyArticle.append("writer", $("#writer").val());
    modifyArticle.append("content", $("#content").html());
    modifyArticle.append("articlePw", $("#articlePw").val());   //비밀번호 설정시 pw 값
    modifyArticle.append("secretCheck", isSecretCheck);         //비밀글 설정 체크박스
    modifyArticle.append("reCheckPw", $("#pwInput").val());     //팝업창 입력 pw


    $.ajax( {
        url :"http://localhost:8080/modifyArticleDbAjax",    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : modifyArticle,      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() { },         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
             if(jsonData.code == "success") {               //이상없이 성공시
                 alert("수정완료");
                 viewAjax();                                //조회창으로 이동
                 $("#titleHead").html("조회페이지");    //타이틀 변경
                 $("#articlePw").val("");
                 $(".modal").fadeOut();                     //팝업 닫음
             }else if(jsonData.code == "dbError") {
                 alert("관리자에게 문의하세요 01038107945");
             }else if(jsonData.code == "textWrong") {
                 alert("빈칸없이 입력하시오.");
             }else if(jsonData.code == "pwWrong"){
                 alert("기존의 비밀번호를 입력해야 합니다.");
             }else if(jsonData.code == "newPwWrong"){
                 alert("비밀번호는 4-8글자의 숫자만 입력 가능합니다.");
             }

        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}
//조회창에서 수정창 이동시 html 변환
function modifyBtnClick() {
    $("#titleHead").html("수정페이지")                // 타이틀 변경
    $(".viewJquery").attr("disabled" , false);// 텍스트 창 disabled 해제
        // 버튼 변경
    $(".modifyShow").css("display","")        // 수정창에 필요한 정보 소환
    $(".viewShow").css("display","none")      // 조회창에 필요한 정보 감춤
    if($('input:checkbox[id="secretCheck"]').is(":checked") == false){  // 공개글일경우
        $("#articlePw").attr("disabled" , true);                    //비밀번호 입력창 비활성화
    }

}
//수정창에서 뒤로가기시 확인
function historyBack() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        viewAjax();
    } else {
    }
}
//조회창에서 뒤로가기버튼
function viewBack() {
    location.href = "/listAjaxTest?pageNum="+ $("#pageNum").val();
}
//삭제확인
function delCheck(){
    if(confirm("정말 삭제 하시겠습니까?")) {
        delAjax();
        // if($("#isSecretNum").val() == "true") {                         //기존에 비밀글이였는지 체크
        //     $("#pwCheckBtn").attr("onclick","delAjax()");   //팝업창 확인버튼 온클릭이벤트 변경
        //     $("#pwInput").val("");                                //팝업창 비밀번호 입력값 초기화
        //     $(".modal").fadeIn();                                       //팝업창 소환
        // }else{              //일반글일경우
        //     delAjax();      //삭제 실행
        // }
    } else {

    }
}
//수정완료시 공백및 비밀번호 체크
function modifyCheck() {

    $(".exception").css("display" , "none");

    if($.trim($("#title").val()) == "") {
        alert('제목을 입력해주세요');
        $("#titleException").css("display" , "");
    }

    if($.trim($("#writer").val()) == "") {
        alert('작성자를 입력해주세요');
        $("#writerException").css("display" , "");
    }

    if($.trim($("#content").val()) == "") {
        alert('본문을 입력해주세요');
        $("#contentException").css("display" , "");
    }

    if(confirm("수정 하시겠습니까?")) {
        modifyAjax();
        // if($("#isSecretNum").val() == "true"){                              //기존에 비밀글이였을경우
        //     // $("#pwCheckBtn").attr("onclick","modifyAjax()");    //팝업창 확인버튼 온클릭이벤트 변경
        //     // $("#pwInput").val("");                                    //기존 팝업창 인풋 value 초기화
        //     // $(".modal").fadeIn();                                           //팝업창 소환
        // }else{
        //     // modifyAjax();
        // }
    } else {

    }
}
//비밀글 체크시 비밀번호 입력창 활성화, 비활성화
function secretSecretArticle() {
    if($('input:checkbox[id="secretCheck"]').is(":checked") == true) {
        $("#articlePw").attr("disabled" , false);
    } else{
        $("#articlePw").val("");
        $("#articlePw").attr("disabled" , true);
    }
    if($("#isSecretNum").val() == "true"){
        $("#articlePw").attr("disabled" , false);
    }
};