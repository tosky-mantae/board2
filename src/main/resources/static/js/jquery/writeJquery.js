function hiddenException() {
    $(".exception").css("display" , "none");
};

window.onload = function() {
    // 경고태그 히든처리
    hiddenException();
}

function writeAjax() {

    let isSecretCheck;
    if($('input:checkbox[id="secretCheck"]').is(":checked") == true) {
        isSecretCheck = 1;
    }else{
        isSecretCheck = 0;
    }
    $.ajax({
        url :'http://localhost:8080/articleWriteDbAjax',    // 요청 할 주소
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : {
            // writer : $("#writer").val(),
            title : $("#title").val(),
            content : $("#content").val(),
            secretCheck : isSecretCheck,
            articlePw : $("#articlePw").val()
        },      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(json) {
            if(json.code == "success") {
                alert("등록완료!");
                location.href = "/listAjaxTest";
            } else {
                alert(json.code)
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function writeCheck() {
    hiddenException()

    if($.trim($("#title").val()) == "") {
        alert('제목을 입력해주세요');
        $("#titleException").css("display" , "");
        return false;
    }

    if($.trim($("#writer").val()) == "") {
        alert('작성자를 입력해주세요');
        $("#writerException").css("display" , "");
        return false;
    }

    if($.trim($("#content").val()) == "") {
        alert('본문을 입력해주세요');
        $("#contentException").css("display" , "");
        return false;
    }

    let isSecretCheck;
    if($('input:checkbox[id="secretCheck"]').is(":checked") == true) {
        isSecretCheck = 1;
    }else{
        isSecretCheck = 0;
    }

    if(isSecretCheck == 1) {
        if($("#articlePw").val().length > 8 || $("#articlePw").val().length < 4) {
            alert("비밀번호는 4-8글자의 숫자만 입력 가능합니다")
            return false;
        }
    }

    if(confirm("등록 하시겠습니까?")) {
        writeAjax();
    } else {

    }
};

function historyBack() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        history.go(-1);
    } else {

    }
};
    //라디오일경우 사용 함수
// function secretOpenArticle() {       //공개글 선택시 비밀번호입력창 비운후 비활성화
//     $("#articlePw").val("");
//     $("#articlePw").attr("disabled" , true);
// };
// function secretSecretArticle() {     //비밀글 선택시 비밀번호 입력창 활성화
//     $("#articlePw").attr("disabled" , false);
// };
function secretSecretArticle() {
    if($('input:checkbox[id="secretCheck"]').is(":checked") == true) {
        $("#articlePw").attr("disabled" , false);
    } else{
        $("#articlePw").val("");
        $("#articlePw").attr("disabled" , true);
    }
};

// $('input:checkbox[name="secretCheck"]').is(":checked") == true;