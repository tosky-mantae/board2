function hiddenException(){
    document.getElementById("writerException").style.display = 'none';
    document.getElementById("titleException").style.display = 'none';
    document.getElementById("contentException").style.display = 'none';
}
window.onload = function (){
    //경고태그 히든처리
    hiddenException()
};

function writeAjax() {

    var xhr = new XMLHttpRequest();
    let articleForm = new FormData();

    let writer = document.getElementById("writer").value;
    let title = document.getElementById("title").value;
    let content = document.getElementById("content").value;

    articleForm.append("writer", writer);
    articleForm.append("title", title);
    articleForm.append("content", content);

    xhr.onreadystatechange = function() {
        console.log("!xhr.readyState : ", xhr.readyState);
        console.log(xhr.readyState);
        if(xhr.readyState === XMLHttpRequest.DONE) {
            if(xhr.status === 200) {
                console.log("xhr.response", xhr.response);
                var jsonData = JSON.parse(xhr.response);
                let code = jsonData.code;

                if(code == 33) {
                    alert("빈칸없이 작성하시오")
                } else if(code == 1) {
                    alert("등록완료!")
                    location.href = "/listAjaxTest"
                }


            }
        }
    };
    xhr.open("POST", "http://localhost:8080/articleWriteDbAjax", true);
    // xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    // xhr.setRequestHeader("Content-Type", "application/json");
    // xhr.send(articleForm);
    xhr.send(articleForm);
}

function writeCheck() {

    document.getElementById("titleException").style.display = 'none';
    document.getElementById("writerException").style.display = 'none';
    document.getElementById("contentException").style.display = 'none';

    if(document.articleForm.title.value.trim() == ""){
        alert('제목을 입력해주세요');
        document.getElementById("titleException").style.display = '';
        return;
    }
    if(document.articleForm.writer.value.trim() == ""){
        alert('작성자를 입력해주세요');
        document.getElementById("writerException").style.display = '';
        return;
    }
    if(document.articleForm.content.value.trim() == ""){
        alert('본문을 입력해주세요');
        document.getElementById("contentException").style.display = '';
        return;
    }
    if(confirm("등록 하시겠습니까?")) {
        writeAjax()
    } else {
    }
}

function historyBack(){
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        history.go(-1)
    } else {
    }
}