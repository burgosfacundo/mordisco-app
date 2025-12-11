package utn.back.mordiscoapi.common.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class Sanitize {

    // Palabras que van en minúscula en español (excepto al inicio)
    private static final Set<String> LOWERCASE_WORDS = Set.of(
            "de", "del", "la", "las", "los", "el", "y", "e", "o", "u", "a", "al"
    );

    // Abreviaciones comunes que mantienen su formato
    private static final Set<String> ABBREVIATIONS = Set.of(
            "av.", "av", "avda.", "avda", "dr.", "dr", "gral.", "gral",
            "pte.", "pte", "cnel.", "cnel", "tte.", "tte", "cap.", "cap",
            "brig.", "brig", "alte.", "alte", "cmte.", "cmte", "tte.cnel.",
            "tte.gral.", "vte.", "vte", "ing.", "ing", "arq.", "arq",
            "prof.", "prof", "lic.", "lic", "cda.", "cda", "pje.", "pje",
            "pasaje", "boulevard", "blvd.", "blvd"
    );

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

    /**
     * Convierte un texto a formato Title Case apropiado para nombres propios en español.
     * Maneja preposiciones, artículos y abreviaciones correctamente.
     *
     * @param s el texto a formatear
     * @return el texto formateado con mayúsculas y minúsculas apropiadas, o null si es vacío
     */
    public String toTitleCase(String s) {
        if (s == null) return null;

        // Primero colapsar espacios múltiples y trim
        String normalized = collapseSpaces(s);
        if (normalized == null) return null;

        // Dividir en palabras
        String[] words = normalized.toLowerCase().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (i > 0) {
                result.append(" ");
            }

            // Verificar si es una abreviación conocida
            boolean isAbbreviation = ABBREVIATIONS.contains(word.toLowerCase().replace(".", ""));

            // Primera palabra siempre va en title case
            // Abreviaciones mantienen su formato con punto
            // Palabras cortas (preposiciones/artículos) van en minúscula excepto al inicio
            if (i == 0 || isAbbreviation || !LOWERCASE_WORDS.contains(word)) {
                result.append(capitalize(word));
            } else {
                result.append(word);
            }
        }

        return result.toString();
    }

    /**
     * Capitaliza la primera letra de una palabra
     */
    private String capitalize(String word) {
        if (word == null || word.isEmpty()) return word;

        // Si tiene punto (abreviación), capitalizar cada parte
        if (word.contains(".")) {
            String[] parts = word.split("\\.");
            return Arrays.stream(parts)
                    .map(part -> part.isEmpty() ? part : part.substring(0, 1).toUpperCase() + part.substring(1))
                    .collect(Collectors.joining("."));
        }

        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}

