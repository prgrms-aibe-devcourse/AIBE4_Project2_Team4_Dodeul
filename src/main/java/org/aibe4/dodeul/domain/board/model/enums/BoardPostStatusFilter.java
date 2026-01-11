package org.aibe4.dodeul.domain.board.model.enums;

import java.util.Locale;

public enum BoardPostStatusFilter {
    OPEN,
    CLOSED,
    ALL;

    public static BoardPostStatusFilter fromNullable(String raw) {
        if (raw == null) {
            return null;
        }
        String v = raw.trim();
        if (v.isEmpty()) {
            return null;
        }
        v = v.toUpperCase(Locale.ROOT);

        if (v.equals("전체") || v.equals("ALL")) {
            return ALL;
        }
        if (v.equals("OPEN")) {
            return OPEN;
        }
        if (v.equals("CLOSED")) {
            return CLOSED;
        }
        return null;
    }
}
