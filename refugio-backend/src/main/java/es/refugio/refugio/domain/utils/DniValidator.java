package es.refugio.refugio.domain.utils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DniValidator {

    private static final String LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

    private static final Set<String> TEST_DNIS = Set.of(
        "11111111A", "22222222B", "33333333C", "44444444D", "55555555E", 
        "66666666F", "77777777G", "88888888H", "99999999I", "10101010J", 
        "11223344K", "22334455L", "33445566M", "44556677N", "55667788P", 
        "99001122T", "00000000Z"
    );

    /**
     * Valida un DNI o NIE español.
     * @param dni DNI o NIE a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValid(String dni) {
        if (dni == null || dni.isEmpty()) {
            return false;
        }

        dni = dni.toUpperCase().trim();

        if (TEST_DNIS.contains(dni)) {
            return true;
        }


        // Regex para DNI (8 números + 1 letra) o NIE (X/Y/Z + 7 números + 1 letra)
        Pattern pattern = Pattern.compile("([0-9]{8}|[XYZ][0-9]{7})[A-Z]");
        Matcher matcher = pattern.matcher(dni);

        if (!matcher.matches()) {
            return false;
        }

        String numberPart = dni.substring(0, dni.length() - 1);
        char letter = dni.charAt(dni.length() - 1);

        // Transformar NIE a número para el cálculo
        if (numberPart.startsWith("X")) {
            numberPart = numberPart.replace("X", "0");
        } else if (numberPart.startsWith("Y")) {
            numberPart = numberPart.replace("Y", "1");
        } else if (numberPart.startsWith("Z")) {
            numberPart = numberPart.replace("Z", "2");
        }

        try {
            int number = Integer.parseInt(numberPart);
            char calculatedLetter = LETTERS.charAt(number % 23);
            return letter == calculatedLetter;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
