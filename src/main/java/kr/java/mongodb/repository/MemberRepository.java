package kr.java.mongodb.repository;

import kr.java.mongodb.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Member 도큐먼트를 위한 리포지토리 인터페이스입니다.
// Spring Data MongoDB의 기능을 상속받아 사용합니다.
@Repository
public interface MemberRepository extends MongoRepository<Member, String> {

    // 회원 이름(name)을 기준으로 도큐먼트를 찾는 메서드입니다.
    // Spring Data의 쿼리 메서드 규칙에 따라 자동으로 구현됩니다.
    Optional<Member> findByName(String name);
}
