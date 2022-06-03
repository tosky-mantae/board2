let pageNum = 1;

window.onload = function() {
        // 삭제후 기존 페이지로 롤백하기위해 url에 파라미터담아온걸 파싱
    let pageNum = new URLSearchParams(location.search).get("pageNum");
        // 파라미터가있다면 pageNum에 대입
    if(pageNum!=null) {
        $("#pageNum").val(pageNum);
        // 대입후 url 변경
        history.pushState(null, null, "listAjaxTest")
    }

    ajaxGetList();
}

function ajaxGetList() {
    let fD = new FormData($("#searchArticleForm")[0]);
    $.ajax( {
        url : "http://localhost:8080/listAjax",    // 요청 할 주소
        processData : false,    // 데이터 객체를 문자열로 바꿀지에 대한 값이다. true면 일반문자...
        contentType : false,    // 해당 타입을 true로 하면 일반 text로 구분되어 진다.
        async : true,           // false 일 경우 동기 요청으로 변경
        type : "POST",          // GET, PUT
        data : fD,              // 전송할 데이터
        dataType : "json",      // xml, json, script, html
        beforeSend : function() {},     // 서버 요청 전 호출 되는 함수 return false; 일 경우 요청 중단
        success : function(json) {
            // 게시글 뿌리기
            let list = json.articles;

            let articleTotalCount = json.articleTotalCount; // 총 게시물수
            let pageNum = json.pageNum;     // 현재 페이지 번호
            let pageView = json.pageView;   // 페이지당 게시물 수

            $("#listTbody").html("");
            let row = ""
            for(let i = 0; i < list.length; i++) {

                // <tr>내의 td 내용 저장
                row += "<td class='listArticle'>" + String((articleTotalCount - i) - (pageNum - 1) * pageView) + "</td>";
                row += "<td class='listArticle'>" + list[i].writer + "</td>";
                row += "<td class='listArticle'>" + "<a id='titleAnchor" + i + "'>" + list[i].title + "</a></td>";
                row += "<td class='listArticle'>" + formatDate(list[i].regDate) + "</td>";

                // 테이블 내에 tr 생성후 innerhtml로 td 추가
                $("#listTbody").append($("<tr>").attr("id","articleLink" + i).html(row));

                // 제목에 앵커 링크 추가
                $("#titleAnchor" + i ).attr("href","modifyArticlePageAjax?boardNo=" + list[i].boardNo + "&pageNum=" + pageNum);

                // td 내용 리셋
                row = ""
                };

            // 페이지그룹 뿌리기전 리셋
            $("#pageGroup").html("");
            let pageGroupList = json.pageGroupList;

            // 페이지 그룹 생성 버튼 반복문
            for(let i = 0; i < pageGroupList.length; i++) {

                $("#pageGroup").append($("<button>").attr({
                    id : "pageBtnNum"+ pageGroupList[i],
                    value : pageGroupList[i],
                    onclick : "movePage(document.getElementById('pageBtnNum"+pageGroupList[i]+"').value)"
                }).html(pageGroupList[i]));
            }

            // 다음페이지 버튼
            let pagingGroupStartNum = json.pagingGroupStartNum;
            let pagingGroupCountInt = json.pagingGroupCountInt;
            let pagingEndNum = json.pagingEndNum;
            let nextPage = pagingGroupStartNum + pagingGroupCountInt;
            if((pagingGroupStartNum-1+pagingGroupCountInt) != pagingEndNum) {
                $("#nextPageBtn").attr("value" , nextPage);
                $("#nextPageBtn").css("visibility" , "visible");
            } else {
                $("#nextPageBtn").css("visibility" , "hidden");
            }

            // 이전페이지 버튼
            let prevPage = pagingGroupStartNum - 1;
            if(pagingGroupStartNum>pagingGroupCountInt) {
                $("#prevPageBtn").attr("value" , prevPage);
                $("#prevPageBtn").css("visibility" , "visible");
            } else {
                $("#prevPageBtn").css("visibility" , "hidden");
            }

            // 마지막 페이지
            let pageCount = json.pageCount;
            if(pageNum != pageCount) {
                $("#lastPageBtn").attr("value" , pageCount);
                $("#lastPageBtn").css("visibility" , "visible");
            } else {
                $("#lastPageBtn").css("visibility" , "hidden");
            }

            // 첫페이지
            if(pageNum != 1) {
                $("#firstPageBtn").attr("value" , 1);
                $("#firstPageBtn").css("visibility" , "visible");
            } else {
                $("#firstPageBtn").css("visibility" , "hidden");
            }

            if(pageNum != 0) {
                $("#pageBtnNum" + pageNum).attr("disabled" , true);
            }

            $("#searchNull").css("display", "none");
            if(json.searchNull != 0) {
                $("#searchNull").css("display", "");
            }

        },      // 요청 완료 시
        error : function() {},      // 요청 실패
        complete : function() {}    // 요청의 실패, 성공과 상관 없이 완료 될 경우 호출
        });
}

function formatDate(date) {
    let d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if(month.length < 2) {
        month = '0' + month;
    }

    if(day.length < 2) {
        day = '0' + day;
    }

    return [year, month, day].join('-');
}

function searchReset() {
    document.getElementById('searchArticleForm').reset();
    document.getElementById('pageNum').value = 1;
    ajaxGetList();
}

function movePage(x) {
    document.getElementById('pageNum').value = x;
    ajaxGetList();
}

function searchSet() {
    document.getElementById('pageNum').value = 1;
    ajaxGetList();
}