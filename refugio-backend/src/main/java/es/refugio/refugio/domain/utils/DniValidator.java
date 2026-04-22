package es.refugio.refugio.domain.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DniValidator {

    private static final String LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";

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
