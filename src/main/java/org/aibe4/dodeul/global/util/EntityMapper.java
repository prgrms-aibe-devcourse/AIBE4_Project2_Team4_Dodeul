package org.aibe4.dodeul.global.util;

import java.util.List;
import java.util.function.Function;

public class EntityMapper {

    private EntityMapper() {
        // 유틸 클래스는 인스턴스화 방지
    }

    /**
     * Entity 리스트를 특정 필드의 문자열 리스트로 변환
     *
     * @param entities Entity 리스트
     * @param nameExtractor 필드 추출 함수
     * @return 문자열 리스트
     */
    public static <T> List<String> toNameList(List<T> entities, Function<T, String> nameExtractor) {
        return entities.stream().map(nameExtractor).toList();
    }
}
