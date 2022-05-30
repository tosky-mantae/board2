window.onload = function() {
    //페이지로딩시 조회페이지
    viewAjax();
};

function viewAjax() {

    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");

    let viewArticle = new FormData();
    viewArticle.append("boardNo", boardNo);
    viewArticle.append("pageNum", pageNum);

    $.ajax({
        url :'http://localhost:8080/modifyArticlePageAjax',    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : 'POST',      // GET, PUT
        data : viewArticle,      // 전송할 데이터
        dataType : 'json',        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
            //서버에서 갖고온 내용 변수할당
            let writer = jsonData.article.writer;
            let title = jsonData.article.title;
            let content = jsonData.article.content;
            let boardNo = jsonData.article.boardNo;
            let pageNum = jsonData.pageNum;

            //html내의 경고태그 id 변수할당
            let delBtn = document.getElementById("delBtn");

            //내용 할당 및 수정불가
            $("#writer").val(writer)
            $("#title").val(title)
            $("#boardNo").val(boardNo)
            $("#pageNum").val(pageNum)
            $("#content").html(content)
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

//여기부터
    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");

    let delArticle = new FormData();
    delArticle.append("boardNo", boardNo);
    delArticle.append("pageNum", pageNum);

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
            if(jsonData.code==1) {
                    alert("삭제완료!")
                    location.href = "/listAjaxTest?pageNum="+jsonData.pageNum;
                }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function modifyAjax() {

//여기부터
    let boardNo = new URLSearchParams(location.search).get("boardNo");
    let pageNum = new URLSearchParams(location.search).get("pageNum");

    let modifyArticle = new FormData();
    modifyArticle.append("boardNo", boardNo);
    modifyArticle.append("pageNum", pageNum);
    modifyArticle.append("title", $("#title").val());
    modifyArticle.append("writer", $("#writer").val());
    modifyArticle.append("content", $("#content").html());

    $.ajax( {
        url :"http://localhost:8080/modifyArticleDbAjax",    // 요청 할 주소
        contentType: false,
        processData: false,
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : modifyArticle,      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(jsonData) {
            if(jsonData.code==33) {
                    alert("빈칸없이 입력하시오");
                }else if(jsonData.code==1) {
                alert("수정완료");
                viewAjax();
                $("#titleHead").html("조회페이지");
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function modifyBtnClick() {
    $("#titleHead").html("수정페이지")                // 타이틀 변경
    $(".viewJquery").attr("disabled" , false);// 텍스트 창 disabled 해제
        // 버튼 변경
    $(".modifyShow").css("display","")
    $(".viewShow").css("display","none")

}
function historyBack() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        location.href = "/listAjaxTest?pageNum="+ $("#pageNum").val();
    } else {
    }
}
function viewBack() {
    location.href = "/listAjaxTest?pageNum="+ $("#pageNum").val();
}
function delCheck(){
    if(confirm("정말 삭제 하시겠습니까?")) {
        delAjax();
    } else {

    }
}
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
    } else {

    }
}