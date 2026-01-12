// src/main/java/org/aibe4/dodeul/domain/board/model/enums/BoardPostStatusFilter.java
package org.aibe4.dodeul.domain.board.model.enums;

import java.util.Locale;

public enum BoardPostStatusFilter {
    OPEN,
    CLOSED,
    ALL;

    public static BoardPostStatusFilter fromNullable(String raw) {
        if (raw == null) {
            return ALL;
        }

        String v = raw.trim();
        if (v.isEmpty()) {
            return ALL;
        }

        String upper = v.toUpperCase(Locale.ROOT);
        if ("ALL".equals(upper) || "전체".equals(v)) {
            return ALL;
        }
        if ("OPEN".equals(upper)) {
            return OPEN;
        }
        if ("CLOSED".equals(upper)) {
            return CLOSED;
        }
        return null;
    }
}
