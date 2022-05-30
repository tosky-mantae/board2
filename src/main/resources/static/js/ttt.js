function modifyCheck() {

//TODO 예외처리 alert 으로 일괄적용및 html 정리
    if(document.articleForm.title.value.trim() == ""){
        alert('제목을 입력해주세요');
        return;
    }
    if(document.articleForm.writer.value.trim() == ""){
        alert('작성자를 입력해주세요');
        return;
    }
    if(document.articleForm.content.value.trim() == ""){
        alert('본문을 입력해주세요');
        return;
    }
    if(confirm("수정 하시겠습니까?")) {
        document.articleForm.action="/modifyArticleDb";
        document.articleForm.submit();
        alert("수정 실행!");
    } else {
    }
}

function delCheck() {
    if(confirm("삭제 하시겠습니까?")) {
        document.articleForm.action='/deleteArticle';
        document.articleForm.submit();
        alert("삭제 완료!");
    } else {
    }
}
function rollBackCheck() {
    if(confirm("입력한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        let pageNum = document.articleForm.pageNum.value;
        return location.href="/list?pageNum="+pageNum;
    } else {
    }
}
function modifyRollBackCheck() {
    if(confirm("수정한 정보가 모두 지워집니다 정말 취소 하시겠습니까?")) {
        let pageNum = document.articleForm.pageNum.value;
        return location.href="/list?pageNum="+pageNum;
    } else {
    }
}
// function searchBtnClick() {
//     if(!document.searchArticleForm.searchKeyword.value == ""){
//         document.searchArticleForm.action="/list"
//         document.searchArticleForm.submit();
//     }
// }