let pageNum = 1;
// let searchForm = new FormData();
function searchArticle(){

    //let searchForm = new FormData(document.getElementById(searchArticleForm).value);
    var formData = new FormData(document.getElementById("searchArticleForm"));

    // document.getElementById("pageNum");
    // document.getElementById("selectComponent");
    // document.getElementById("searchKeyword");
    //
    // searchForm.append("pageNum", 1);
    // searchForm.append("selectComponent", selectComponent);
    // searchForm.append("searchKeyword", searchKeyword);
    //return false;

//            ajaxGetList(formData);
    ajaxGetList();


}

window.onload = function() {

    pageNum = document.getElementById("pageNum").value ?? 1;

    let testForm = new FormData();
    //
    testForm.append("pageNum", 1);
    testForm.append("selectComponent","all");
    testForm.append("searchKeyword","");
    ajaxGetList();

    let pageNumParam = pageNum;
    //ajaxGetList(testForm);
}

function ajaxGetList() {
    var xhr = new XMLHttpRequest();
    console.log("?xhr.readyState : ", xhr.readyState);
    xhr.onreadystatechange = function() {
        console.log("!xhr.readyState : ", xhr.readyState);
        if(xhr.readyState === XMLHttpRequest.DONE) {
            if(xhr.status === 200) {
                console.log("xhr.response", xhr.response);
                var jsonData = JSON.parse(xhr.response);

                // 게시글 뿌리기
                let list = jsonData.articles;

                let articleTotalCount = jsonData.articleTotalCount; // 총 게시물수
                let pageNum = jsonData.pageNum;     // 현재 페이지 번호
                let pageView = jsonData.pageView;   // 페이지당 게시물 수

                var tbody = document.getElementById("listTbody");
                tbody.innerHTML = "";

                function formatDate(date) {
                    let d = new Date(date),
                        month = '' + (d.getMonth() + 1),
                        day = '' + d.getDate(),
                        year = d.getFullYear();
                    if(month.length < 2)
                        month = '0' + month;
                    if(day.length < 2)
                        day = '0' + day;
                    return [year, month, day].join('-');
                }


                for(let i = 0; i < list.length; i++) {
                    var newTr = document.createElement("tr");
                    newTr.setAttribute('id' , 'articleLink' + i);
                    newTr.innerHTML =
                        "<td class=\"listArticle\">" + String((articleTotalCount - i) - (pageNum - 1) * pageView) + "</td>"+
                        "<td class=\"listArticle\">" + list[i].writer + "</td>" +
                        "<td class=\"listArticle\">" +
                        '<a style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"href="modifyArticlePageAjax?boardNo='+
                        list[i].boardNo + '&pageNum='+ pageNum + '" >'+
                        list[i].title +
                        "</a></td>" +
                        "<td class=\"listArticle\">" + formatDate(list[i].regDate) + "</td>";

                    tbody.appendChild(newTr);

                    // <a style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" href="modifyArticlePageAjax(boardNo=    ,pageNum=  )}" >title</a>
                }

                // 페이지그룹 뿌리기
                let pageGroup = document.getElementById("pageGroup");
                let pageGroupList = jsonData.pageGroupList;

                pageGroup.innerHTML="";

                for(let i = 0; i < pageGroupList.length; i++) {
                    let pageBtn = document.createElement("button");
                    pageBtn.setAttribute('id', 'pageBtnNum'+pageGroupList[i]);
                    pageBtn.setAttribute('value',  pageGroupList[i]);
                    pageBtn.innerHTML = pageGroupList[i];
                    pageGroup.appendChild(pageBtn);

                    pageBtn.setAttribute('onclick', "movePage(document.getElementById('pageBtnNum"+pageGroupList[i]+"').value)");

                }

                //다음페이지 버튼
                let pagingGroupStartNum = jsonData.pagingGroupStartNum;
                let pagingGroupCountInt = jsonData.pagingGroupCountInt;
                let pagingEndNum = jsonData.pagingEndNum
                let nextPage = pagingGroupStartNum +pagingGroupCountInt;
                let nextBtn = document.getElementById("nextPageBtn");
                if((pagingGroupStartNum-1+pagingGroupCountInt)!=pagingEndNum){
                    nextBtn.setAttribute('value',  nextPage);
                    nextBtn.style.visibility = 'visible';
                }else {
                    nextBtn.style.visibility = 'hidden';
                }

                //이전페이지 버튼
                let prevPageBtn = document.getElementById("prevPageBtn");
                let prevPage = pagingGroupStartNum-1;
                if(pagingGroupStartNum>pagingGroupCountInt){
                    prevPageBtn.style.visibility = 'visible';
                    prevPageBtn.setAttribute('value', prevPage);
                }else {
                    prevPageBtn.style.visibility = 'hidden';
                }

                //마지막 페이지
                let lastPageBtn = document.getElementById("lastPageBtn");
                let pageCount = jsonData.pageCount;
                if(pageNum!=pageCount){
                    lastPageBtn.style.visibility = 'visible';
                    lastPageBtn.setAttribute('value', pageCount);
                }else {
                    lastPageBtn.style.visibility = 'hidden';
                }

                //첫페이지
                let firstPageBtn = document.getElementById("firstPageBtn");
                if(pageNum!=1){
                    firstPageBtn.style.visibility = 'visible';
                    firstPageBtn.setAttribute('value', 1);
                }else {
                    firstPageBtn.style.visibility = 'hidden';
                }

                if(pageNum!=0) {
                    let xBtn = document.getElementById('pageBtnNum'+pageNum);
                    xBtn.disabled = true;
                }

                document.getElementById('searchNull').style.display = 'none';
                if(jsonData.searchNull!=0){
                    document.getElementById('searchNull').style.display = '';
                }
            }
        }
    };
    // let searchForm = document.searchArticleForm;
    let url = "http://localhost:8080/listAjax";
    xhr.open("POST", url, true);
    // xhr.setRequestHeader("Content-Type", "application.json");
    let articleForm = new FormData();


    articleForm.append("test", "test");
    var formData = new FormData(document.getElementById("searchArticleForm"));

    xhr.send(formData);
}

function searchReset(){
    document.getElementById('searchArticleForm').reset();
    document.getElementById('pageNum').value=1;
    ajaxGetList();
}

function movePage(x){
    document.getElementById('pageNum').value=x;
    ajaxGetList();
}
function searchSet(){
    document.getElementById('pageNum').value=1;
    ajaxGetList();
}