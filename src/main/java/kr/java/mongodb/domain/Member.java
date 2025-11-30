package kr.java.mongodb.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

// MongoDB의 'members' 컬렉션에 저장될 도큐먼트입니다.
@Data
@NoArgsConstructor
@Document(collection = "members")
public class Member {

    // MongoDB 도큐먼트의 식별자 (_id).
    @Id
    private String id;

    // 회원의 이름. 여기서는 세션 구분을 위한 로그인 ID 역할을 대체합니다.
    private String name;

    // [SQL과의 차이점]:
    // Memo 객체들을 별도의 테이블/컬렉션으로 분리하지 않고,
    // Member 도큐먼트 내부에 'List' 형태로 임베딩(Embedding)합니다.
    // 이로 인해 회원 정보 조회 시 JOIN 없이 모든 메모를 한 번에 가져올 수 있습니다.
    private List<Memo> memos;

    // 생성자: 새로운 회원을 만들 때 이름과 빈 메모 리스트를 초기화합니다.
    public Member(String name) {
        this.name = name;
        this.memos = new ArrayList<>();
    }
}