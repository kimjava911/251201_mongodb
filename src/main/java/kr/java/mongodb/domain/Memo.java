package kr.java.mongodb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant; // UTC 기준 시점을 저장하기 위해 Instant 사용

// Member 도큐먼트 내부에 임베디드될 메모 객체입니다.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {

    // 메모의 고유 ID. MongoDB의 _id와는 별개로, 임베디드 리스트 내에서 구분을 위해 사용됩니다.
    private String id;

    // 메모 내용
    private String content;

    // ⭐ 작성 또는 수정 시간. 시간대 정보가 없는 Instant(UTC)로 저장됩니다.
    private Instant timestamp;
}