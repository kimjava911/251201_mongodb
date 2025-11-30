package kr.java.mongodb.service;

import kr.java.mongodb.domain.Member;
import kr.java.mongodb.domain.Memo;
import kr.java.mongodb.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// 회원 및 메모 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
@Service
@RequiredArgsConstructor // Lombok을 이용한 생성자 주입 (Constructor Injection)
public class MainService {

    // final 필드에 대한 생성자 주입
    public final MemberRepository memberRepository;

    /**
     * 이름으로 회원을 검색하고, 없으면 새로 생성하여 저장합니다. (로그인 대체)
     * @param name 회원 이름
     * @return 찾거나 새로 생성된 Member 객체
     */
    @Transactional
    public Member getOrCreateMember(String name) {
        // 이름으로 기존 회원을 찾습니다.
        Optional<Member> existingMember = memberRepository.findByName(name);

        if (existingMember.isPresent()) {
            return existingMember.get(); // 이미 존재하면 반환 (Read)
        } else {
            // 존재하지 않으면 새 Member 도큐먼트를 생성하고 저장합니다. (Create)
            Member newMember = new Member(name);
            return memberRepository.save(newMember);
        }
    }

    /**
     * 특정 회원 도큐먼트에 새 메모를 추가합니다. (CRUD - Create)
     * SQL과의 차이점: 별도의 Memo 테이블에 INSERT 하는 것이 아니라,
     * Member 도큐먼트 내부의 리스트에 새 Memo 객체를 추가하고 Member 도큐먼트 전체를 업데이트합니다.
     *
     * @param memberId 메모를 추가할 회원의 ID
     * @param content 메모 내용
     * @return 메모가 추가된 Member 객체
     */
    @Transactional
    public Member addMemo(String memberId, String content) {
        // ID로 Member 도큐먼트를 조회합니다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // Instant.now()를 사용하여 UTC 기준 시점을 기록합니다.
        Memo newMemo = new Memo(UUID.randomUUID().toString(), content, Instant.now());

        // Member 도큐먼트 내부의 메모 리스트(List<Memo>)에 새 메모를 추가합니다.
        member.getMemos().add(newMemo);

        // 변경된 Member 도큐먼트를 DB에 저장(업데이트)합니다.
        return memberRepository.save(member);
    }

    /**
     * 특정 회원의 특정 메모를 수정합니다. (CRUD - Update)
     *
     * @param memberId 회원의 ID
     * @param memoId 수정할 메모의 ID
     * @param newContent 새로운 메모 내용
     * @return 메모가 수정된 Member 객체
     */
    @Transactional
    public Member updateMemo(String memberId, String memoId, String newContent) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 임베디드된 메모 리스트에서 해당 ID를 가진 메모를 찾아 내용을 수정합니다.
        member.getMemos().stream()
                .filter(memo -> memo.getId().equals(memoId))
                .findFirst()
                .ifPresent(memo -> {
                    memo.setContent(newContent);
                    // Instant.now()를 사용하여 수정 시간을 업데이트합니다.
                    memo.setTimestamp(Instant.now());
                });

        // 변경된 Member 도큐먼트를 저장합니다.
        return memberRepository.save(member);
    }

    /**
     * 특정 회원의 특정 메모를 삭제합니다. (CRUD - Delete)
     *
     * @param memberId 회원의 ID
     * @param memoId 삭제할 메모의 ID
     * @return 메모가 삭제된 Member 객체
     */
    @Transactional
    public Member deleteMemo(String memberId, String memoId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 임베디드된 메모 리스트에서 조건(ID)에 맞는 요소를 제거합니다.
        member.getMemos().removeIf(memo -> memo.getId().equals(memoId));

        // 변경된 Member 도큐먼트를 저장합니다.
        return memberRepository.save(member);
    }
}
