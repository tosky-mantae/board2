//
window.onload = function() {
    //
    viewAjax();
};

//
function viewAjax() {
    //
    let xhr = new XMLHttpRequest();

    //
    let nowUrl = new URL(location.href);
    let boardNo = nowUrl.searchParams.get("boardNo");
    let pageNum = nowUrl.searchParams.get("pageNum");

    //
    let viewArticle = new FormData();
    viewArticle.append("boardNo", boardNo);
    viewArticle.append("pageNum", pageNum);

    console.log(viewArticle);

    //
    xhr.onreadystatechange = function() {
        //
        if(xhr.readyState === 4) {
            //
            if(xhr.status === 200) {
                //json 형태로 파싱
                let jsonData = JSON.parse(xhr.response);
                console.log(jsonData);



                //서버에서 갖고온 내용 변수할당
                let writer = jsonData.article.writer;
                let title = jsonData.article.title;
                let content = jsonData.article.content;
                let boardNo = jsonData.article.boardNo;
                let pageNum = jsonData.pageNum;

                //html내의 작성태그 id 변수할당
                let writerText = document.getElementById("writer");
                let titleText = document.getElementById("title");
                let contentText = document.getElementById("content");
                let boardNoText = document.getElementById("boardNo");
                let pageNumText = document.getElementById("pageNum");

                //html내의 경고태그 id 변수할당
                let writerException = document.getElementById("writerException");
                let titleException = document.getElementById("titleException");
                let contentException = document.getElementById("contentException");

                let delBtn = document.getElementById("delBtn");

                //내용 할당 및 수정불가
                boardNoText.setAttribute('value', boardNo);
                pageNumText.setAttribute('value', pageNum);
                writerText.setAttribute('value', writer);
                titleText.setAttribute('value', title);
                contentText.innerHTML=content;
                writerText.disabled = true;
                titleText.disabled = true;
                contentText.disabled = true;

                //경고태그 히든처리
                writerException.style.display = 'none';
                titleException.style.display = 'none';
                contentException.style.display = 'none';

                //삭제 버튼 숨김
                delBtn.style.visibility = 'hidden';
                document.getElementById("modifyFinishBtn").style.display = 'none';
                document.getElementById("modifyBackBtn").style.display = 'none';
                document.getElementById("modifyBtn").style.display = '';
                document.getElementById("viewBackBtn").style.display = '';


            }
        }
    };

    //
    xhr.open("POST", "http://localhost:8080/modifyArticlePageAjax", true);
    xhr.send(viewArticle);
}

function modifyBtnClick(){
    document.getElementById("delBtn").style.visibility = 'visible';
    document.getElementById("writer").disabled = false;
    document.getElementById("title").disabled = false;
    document.getElementById("content").disabled = false;
    document.getElementById("modifyFinishBtn").style.display = '';
    document.getElementById("modifyBackBtn").style.display = '';
    document.getElementById("modifyBtn").style.display = 'none';
    document.getElementById("viewBackBtn").style.display = 'none';
    document.getElementById("titleHead").innerHTML='수정페이지';

}
function historyBack() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        location.href = "/listAjaxTest?pageNum="+ document.getElementById('pageNum').value;
    } else {
    }
}
function viewBack() {
    location.href = "/listAjaxTest?pageNum="+ document.getElementById('pageNum').value;
}
function delCheck(){
    if(confirm("정말 삭제 하시겠습니까?")) {
        delAjax();
    } else {
    }
}

function delAjax() {
    var xhr1 = new XMLHttpRequest();
    let boardNo = document.getElementById("boardNo").value;
    let pageNum = document.getElementById("pageNum").value;

    let delArticle = new FormData();
    delArticle.append("boardNo", boardNo);
    delArticle.append("pageNum", pageNum);


    xhr1.onreadystatechange = function() {
        if(xhr1.readyState === 4) {
            if(xhr1.status === 200) {
                let jsonData = JSON.parse(xhr1.response);
                console.log(jsonData);

                if(jsonData.code==1){
                    alert("삭제완료!")
                    location.href = "/listAjaxTest?pageNum="+jsonData.pageNum;
                }

                console.log("작업내용 작성");
            }
        }
    };
    xhr1.open("POST", "http://localhost:8080/deleteArticleAjax", true);
    xhr1.send(delArticle);
}

function modifyAjax() {
    var modifyxhr = new XMLHttpRequest();
    let boardNo = document.getElementById("boardNo").value;
    let pageNum = document.getElementById("pageNum").value;
    let title = document.getElementById("title").value;
    let writer = document.getElementById("writer").value;
    let content = document.getElementById("content").value;

    let modifyArticle = new FormData();
    modifyArticle.append("boardNo", boardNo);
    modifyArticle.append("pageNum", pageNum);
    modifyArticle.append("title", title);
    modifyArticle.append("writer", writer);
    modifyArticle.append("content", content);


    modifyxhr.onreadystatechange = function() {
        if(modifyxhr.readyState === 4) {
            if(modifyxhr.status === 200) {
                let jsonData = JSON.parse(modifyxhr.response);
                if(jsonData.code==33){
                    alert("빈칸없이 입력하시오");
                }else if(jsonData.code==1){
                    alert("수정완료");
                    viewAjax();
                    document.getElementById("titleHead").innerHTML='조회페이지';
                }
            }
        }
    };
    modifyxhr.open("POST", "http://localhost:8080/modifyArticleDbAjax", true);
    modifyxhr.send(modifyArticle);
}
function modifyCheck() {

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
    if(confirm("수정 하시겠습니까?")) {
        modifyAjax()
    } else {
    }
}