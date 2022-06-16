
function signUpAjax() {
    $.ajax({
        url :'http://localhost:8080/signUp',    // 요청 할 주소
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : {
            userId : $("#userId").val(),
            userPw : $("#userPw").val(),
            userTel : $("#userTel").val(),
        },      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() {

        },         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(json) {
            if(json.code == 33) {           // Result Code / 1 : 성공, 33 : 예외처리
                alert("빈칸없이 작성하시오.");
            } else if(json.code == 1) {     // Result Code / 1 : 성공, 33 : 예외처리
                alert("등록완료!");
                location.href = "/listAjaxTest";
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function signUpCheckAjax() {        //중복가입 체크
    $.ajax({
        url :'http://localhost:8080/signUpCheck',    // 요청 할 주소
        async : true,       // false 일 경우 동기 요청으로 변경
        type : "POST",      // GET, PUT
        data : {
            userId : $("#userId").val(),
        },      // 전송할 데이터
        dataType : "json",        // xml, json, script, html
        beforeSend : function() {},         // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(json) {
            if(json.code == "fail") {           // Result Code / success : 성공, fail : 중복방지
                alert("이미 가입된 아이디입니다.");
            } else if(json.code == "success") {     // Result Code / success : 성공, fail : 중복방지
                if($("#userId").val() != null && $("#userPw").val() != null && $("#userTel").val() != null) {
                    if($("#pwCheck").html() == "올바른 비밀번호 입니다." && $("#telCheck").html() == "올바른 전화번호 입니다.") {
                        signUpAjax()
                        alert($("#userId").val() + "님 회원가입을 축하합니다!");
                        location.href = "/listAjaxTest";
                    }
                }else {
                    alert("정보기입이 잘못되었습니다.");
                }
            }else {
                alert("관리자에게 문의 하세요 01038107945");
            }
        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
    });
}

function historyBack() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        history.go(-1);
    }
}

document.addEventListener("keyup", telCheck, false);
function telCheck() {
    let userTel = $("#userTel").val();

    let regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/;
    if (regPhone.test(userTel) === true) {
        $("#telCheck").html("올바른 전화번호 입니다.")
    }else{
        $("#telCheck").html("잘못된 양식입니다.")
    }
}



document.addEventListener("keyup", pwCheck, false);
function pwCheck() {
    let pw1 = $("#userPw").val();
    let pw2 = $("#rePw").val();
    if(pw1 != "" && pw2 != "") {
        if (pw1 == pw2) {
            $("#pwCheck").html("올바른 비밀번호 입니다.");
        } else {
            $("#pwCheck").html("비밀번호를 확인하세요.");
        }
    }
}