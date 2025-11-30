<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="kr.java.mongodb.domain.Member" %>
<%@ page import="kr.java.mongodb.domain.Memo" %>
<%@ page import="java.util.List" %>
<%
    // JSP 스크립트릿: Model에서 Member 객체를 가져옵니다.
    Member member = (Member) request.getAttribute("member");

    // 에러 메시지 가져오기
    String errorMessage = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Spring Boot MongoDB CRUD (순수 JSP/ES6)</title>
</head>
<body>
<div class="container">
    <h1>MongoDB Simple CRUD 예제 (Member/Memo)</h1>

    <%
        // 에러 메시지 표시 로직
        if (errorMessage != null) {
    %>
    <p><strong>[오류]</strong> <%= errorMessage %></p>
    <hr>
    <%
        }
    %>

    <%
        // member 객체 유무에 따라 화면 분기
        if (member == null) {
    %>
    <h2>접속하기 (세션 생성/회원 조회)</h2>
    <p>이름을 입력하여 도큐먼트를 생성하거나 불러옵니다.</p>
    <form action="<%= request.getContextPath() %>/login" method="post">
        <input type="text" name="name" placeholder="사용할 이름(ID)을 입력하세요" required>
        <button type="submit">접속/회원 정보 불러오기</button>
    </form>
    <%
    } else {
    %>
    <h2><%= member.getName() %> 님의 메모장</h2>
    <p><strong>DB Document ID:</strong> <%= member.getId() %></p>
    <p><a href="<%= request.getContextPath() %>/logout"><button>로그아웃</button></a></p>

    <hr>

    <h3>새 메모 작성 (Create)</h3>
    <form action="<%= request.getContextPath() %>/memo/add" method="post">
        <textarea name="content" rows="3" placeholder="새 메모 내용을 입력하세요" required></textarea>
        <button type="submit">메모 추가</button>
    </form>

    <hr>

    <h3>저장된 메모 목록 (<%= member.getMemos().size() %>개)</h3>

    <%
        List<Memo> memos = member.getMemos();

        if (memos.isEmpty()) {
    %>
    <p>저장된 메모가 없습니다.</p>
    <%
    } else {
    %>
    <ul>
        <%
            // 스크립트릿을 이용한 반복문
            for (Memo memo : memos) {
                // Instant 객체를 ISO-8601 문자열(UTC)로 변환하여 클라이언트에 전달합니다.
                String instantString = memo.getTimestamp().toString();
        %>
        <li>
            <p>
                [메모 ID: <%= memo.getId() %>] <br>
                <small>작성/수정: <span class="local-time" data-timestamp="<%= instantString %>">시간 변환 중...</span></small>
            </p>
            <p><strong>내용:</strong> <%= memo.getContent() %></p>

            <form action="<%= request.getContextPath() %>/memo/update" method="post">
                <input type="hidden" name="memoId" value="<%= memo.getId() %>">
                <textarea name="content" rows="1" placeholder="수정할 내용을 입력하세요" required><%= memo.getContent() %></textarea>
                <button type="submit">수정</button>
            </form>

            <form action="<%= request.getContextPath() %>/memo/delete" method="post">
                <input type="hidden" name="memoId" value="<%= memo.getId() %>">
                <button type="submit">삭제</button>
            </form>
            <hr>
        </li>
        <%
            } // end of for loop
        %>
    </ul>
    <%
            } // end of else (memos is not empty)
        } // end of else (member is not null)
    %>
</div>

<script>
    // const, let, 화살표 함수 등 ES6 문법 사용
    document.addEventListener('DOMContentLoaded', () => {
        const timeElements = document.querySelectorAll('.local-time');

        // 시간 포맷 옵션
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false // 24시간 형식
        };

        // forEach를 이용해 모든 시간 요소 처리
        timeElements.forEach((el) => {
            const utcString = el.getAttribute('data-timestamp');

            if (utcString) {
                try {
                    // Date 객체는 UTC 문자열을 파싱하여 브라우저의 로컬 시간대로 자동 변환합니다.
                    const date = new Date(utcString);

                    // toLocaleString()으로 사용자의 로케일 설정에 맞게 포맷팅합니다.
                    el.textContent = date.toLocaleString(navigator.language, options);
                } catch (e) {
                    el.textContent = '시간 변환 오류: ' + utcString;
                }
            }
        });
    });
</script>
</body>
</html>