
function loginAjax() {
    $.ajax({
        url :'http://localhost:8080/login',    // 요청 할 주소
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : {
            userId : $("#userId").val(),
            userPw : $("#userPw").val(),
        },      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(json) {
            if(json.code == "fail") {           // Result Code / 1 : 성공, 33 : 예외처리
                alert("아이디와 비밀번호를 확인하세요.");
            } else if(json.code == "success") {     // Result Code / 1 : 성공, 33 : 예외처리
                alert($("#userId").val()+"님 환영합니다!");
                location.href = "/listAjaxTest";
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function loginCheck() {
    if($("#userId").val() == "") {
        alert('id를 입력해주세요');
    }

    if($("#userPw").val() == "") {
        alert('비밀번호를 입력해주세요');
    }else {
        loginAjax();
    }
};