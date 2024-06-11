package ru.mfti;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


//REST API
@RestController
class SummatorController {

    @GetMapping("/make")
    public String arithmeticExpression(String expression) {
        return fun(expression);
    }

    public static String fun(String str) {
        try {
            double result = evaluateExpression(str);
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error: проверьте корректность выражения и попробуйте еще раз";
        }
    }

    public static double evaluateExpression(String expression) {
        return new Object() {
            int indx = -1, ch;

            void nextChar() {
                if (++indx < expression.length()) {
                    ch = expression.charAt(indx);
                } else {
                    ch = -1;
                }
            }

            boolean checkChar(int expressionChar) {
                while (ch == ' ') nextChar();
                if (ch == expressionChar) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double startParse() {
                nextChar();
                double x = parseExpression();
                if (indx < expression.length()) throw new RuntimeException("Error: необработанный символ : " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseFirstPart();
                while (true) {
                    if (checkChar('+')) x += parseFirstPart();
                    else if (checkChar('-')) x -= parseFirstPart();
                    else return x;
                }
            }

            double parseFirstPart() {
                double x = parseTrigger();
                while (true) {
                    if (checkChar('*')) x *= parseTrigger();
                    else if (checkChar('/')) x /= parseTrigger();
                    else return x;
                }
            }

            double parseTrigger() {
                if (checkChar('+')) return parseTrigger();
                if (checkChar('-')) return -parseTrigger();
                double x;
                int startPos = indx;
                if (checkChar('(')) {
                    x = parseExpression();
                    checkChar(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, indx));
                } else {
                    throw new RuntimeException("Error: недопустимый символ: " + (char) ch);
                }
                return x;
            }
        }.startParse();
    }
}

