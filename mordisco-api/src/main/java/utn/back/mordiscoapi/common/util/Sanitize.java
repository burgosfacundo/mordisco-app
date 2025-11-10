package utn.back.mordiscoapi.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Sanitize {
    public String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    public String collapseSpaces(String s) {
        if (s == null) return null;
        String t = s.trim().replaceAll("\\s{2,}", " ");
        return t.isEmpty() ? null : t;
    }
}

