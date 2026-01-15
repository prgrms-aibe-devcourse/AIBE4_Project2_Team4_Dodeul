package org.aibe4.dodeul.domain.matching;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.aibe4.dodeul.domain.consulting.model.entity.ConsultingApplication;
import org.aibe4.dodeul.domain.consulting.model.enums.ConsultingTag;
import org.aibe4.dodeul.domain.consulting.model.repository.ConsultingApplicationRepository;
import org.aibe4.dodeul.domain.matching.model.entity.Matching;
import org.aibe4.dodeul.domain.matching.model.enums.MatchingStatus;
import org.aibe4.dodeul.domain.matching.model.repository.MatchingRepository;
import org.aibe4.dodeul.domain.member.model.entity.Member;
import org.aibe4.dodeul.domain.member.model.entity.MenteeProfile;
import org.aibe4.dodeul.domain.member.model.entity.MentorProfile;
import org.aibe4.dodeul.domain.member.model.enums.Provider;
import org.aibe4.dodeul.domain.member.model.enums.Role;
import org.aibe4.dodeul.domain.member.model.repository.MemberRepository;
import org.aibe4.dodeul.domain.member.model.repository.MenteeProfileRepository;
import org.aibe4.dodeul.domain.member.model.repository.MentorProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("init")
public class MatchingDataInitializer implements CommandLineRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private final MemberRepository memberRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final ConsultingApplicationRepository applicationRepository;
    private final MatchingRepository matchingRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 테이블 강제 초기화
        truncateAllTables();

        String encodedPassword = passwordEncoder.encode("password");

        // Case 1. 깨끗한 상태
        Member mentee1 = createMemberWithProfile("mentee1@test.com", "MenteeClean", Role.MENTEE, encodedPassword, true);
        Member mentor1 = createMemberWithProfile("mentor1@test.com", "MentorClean", Role.MENTOR, encodedPassword, true);

        // Case 2. 과거 이력만 있는 상태
        Member mentee2 = createMemberWithProfile("mentee2@test.com", "MenteeHistory", Role.MENTEE, encodedPassword, true);
        Member mentor2 = createMemberWithProfile("mentor2@test.com", "MentorHistory", Role.MENTOR, encodedPassword, true);

        createMatching(mentee2, mentor2, MatchingStatus.CANCELED);
        createMatching(mentee2, mentor2, MatchingStatus.COMPLETED);
        createMatching(mentee2, mentor2, MatchingStatus.TIMEOUT);

        // Case 3. 멘티가 이미 3개 진행 중
        Member mentee3 = createMemberWithProfile("mentee3@test.com", "MenteeBusy", Role.MENTEE, encodedPassword, true);
        Member mentor3 = createMemberWithProfile("mentor3@test.com", "MentorForBusy", Role.MENTOR, encodedPassword, true);

        createMatching(mentee3, mentor3, MatchingStatus.WAITING);
        createMatching(mentee3, mentor3, MatchingStatus.WAITING);
        createMatching(mentee3, mentor3, MatchingStatus.MATCHED);

        // Case 4. 멘토가 상담 거부 상태
        createMemberWithProfile("mentee4@test.com", "MenteeForDisabled", Role.MENTEE, encodedPassword, true);
        createMemberWithProfile("mentor4@test.com", "MentorDisabled", Role.MENTOR, encodedPassword, false);

        // Case 5. 멘토가 이미 3개 진행 중
        Member mentee5 = createMemberWithProfile("mentee5@test.com", "MenteeNew", Role.MENTEE, encodedPassword, true);
        Member mentor5 = createMemberWithProfile("mentor5@test.com", "MentorBusy", Role.MENTOR, encodedPassword, true);

        Member other1 = createMemberWithProfile("other1@test.com", "MenteeOther1", Role.MENTEE, encodedPassword, true);
        Member other2 = createMemberWithProfile("other2@test.com", "MenteeOther2", Role.MENTEE, encodedPassword, true);
        Member other3 = createMemberWithProfile("other3@test.com", "MenteeOther3", Role.MENTEE, encodedPassword, true);

        createMatching(other1, mentor5, MatchingStatus.WAITING);
        createMatching(other2, mentor5, MatchingStatus.MATCHED);
        createMatching(other3, mentor5, MatchingStatus.WAITING);

        // 매칭 없는 상담 신청서 5개 추가
        createApplicationOnly(mentee1, "자소서 첨삭 부탁드립니다.", "급하게 제출해야 합니다. 도와주세요!", ConsultingTag.RESUME);
        createApplicationOnly(mentee1, "백엔드 개발자 커리어 고민", "비전공자인데 취업 준비 방향이 맞는지 궁금합니다.", ConsultingTag.CAREER);
        createApplicationOnly(mentee3, "모의 면접 요청드려요", "다음 주 네카라쿠배 면접입니다.", ConsultingTag.CAREER); // 혹은 INTERVIEW
        createApplicationOnly(mentee5, "포트폴리오 피드백 요청", "프로젝트 3개 정리했습니다.", ConsultingTag.RESUME);
        createApplicationOnly(mentee5, "이직 관련 상담 신청", "3년차 개발자인데 이직 타이밍이 고민입니다.", ConsultingTag.CAREER);

        System.out.println("=============================================");
        System.out.println("✅ 테스트 데이터 초기화 완료");
        System.out.println("✅ 접속 계정 비밀번호: password");
        System.out.println("=============================================");
    }

    // --- Helper Methods ---

    private void truncateAllTables() {
        // 삭제 순서 상관없이 지우기 위해 외래키 제약 조건 해제
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        // 초기화할 테이블 목록
        List<String> tableNames = List.of(
            "matchings",
            "consulting_applications",
            "mentee_profiles",
            "mentor_profiles",
            "members"
        );

        // 테이블 비우기
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }

        // 외래키 제약 조건 다시 설정
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    private Member createMemberWithProfile(String email, String nickname, Role role, String password, boolean consultationEnabled) {
        Member member = Member.builder()
            .email(email)
            .nickname(nickname)
            .role(role)
            .provider(Provider.GOOGLE)
            .passwordHash(password)
            .providerId("provider_" + nickname)
            .build();
        memberRepository.save(member);

        if (role == Role.MENTOR) {
            MentorProfile profile = MentorProfile.create(member);
            setField(profile, "consultationEnabled", consultationEnabled);
            setField(profile, "careerYears", 5);
            setField(profile, "intro", "안녕하세요 멘토 " + nickname + "입니다.");
            mentorProfileRepository.save(profile);
        } else {
            MenteeProfile profile = MenteeProfile.create(member);
            setField(profile, "intro", "안녕하세요 멘티 " + nickname + "입니다.");
            menteeProfileRepository.save(profile);
        }
        return member;
    }

    private void createMatching(Member mentee, Member mentor, MatchingStatus status) {
        ConsultingApplication application = createApplicationOnly(mentee, "상담 신청 - " + status, "상담 내용입니다.", ConsultingTag.CAREER);

        Matching matching = Matching.builder()
            .mentee(mentee)
            .mentor(mentor)
            .application(application)
            .build();

        setField(matching, "status", status);
        matchingRepository.save(matching);
    }

    private ConsultingApplication createApplicationOnly(Member mentee, String title, String content, ConsultingTag tag) {
        ConsultingApplication application = ConsultingApplication.builder()
            .menteeId(mentee.getId())
            .title(title)
            .content(content)
            .consultingTag(tag)
            .fileUrl("http://example.com/dummy.pdf")
            .build();
        return applicationRepository.save(application);
    }

    private void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 설정 실패: " + fieldName, e);
        }
    }
}
