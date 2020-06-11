package school.attractor.payment_module.domain.config;

public class Utility {

    public static String maskCardNumber(long cardNumber, String mask) {
        String cardNumberString = String.valueOf(cardNumber);
        int index = 0;

        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumberString.charAt(index));
                index++;
            } else if (c == 'x') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }
        return maskedNumber.toString();
    }


    public static String maskCardNumber(String cardNumber, String mask) {

        int index = 0;

        StringBuilder maskedNumber = new StringBuilder();
        for (int i = 0; i < mask.length(); i++) {
            char c = mask.charAt(i);
            if (c == '#') {
                maskedNumber.append(cardNumber.charAt(index));
                index++;
            } else if (c == 'x') {
                maskedNumber.append(c);
                index++;
            } else {
                maskedNumber.append(c);
            }
        }
        return maskedNumber.toString();
    }
}
