package io.github.matbia.translate;

/**
 * Miscellaneous utility class containing a single static helper method for unescaping Java strings.
 */
class Unescaper {
    /**
     * Escapes the characters in a String using Java String rules.
     * Replicates the behaviour of StringEscapeUtils.unescapeJava() from Apache Commons.
     * @param input String to escape values in
     * @return String with escaped values
     */
    static String unescapeJava(String input) {
        final var sb = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == '\\') {
                char nextChar = (i == input.length() - 1) ? '\\' : input.charAt(i + 1);
                if (nextChar >= '0' && nextChar <= '7') {
                    final var code = new StringBuilder();
                    code.append(nextChar);
                    i++;
                    if ((i < input.length() - 1) && input.charAt(i + 1) >= '0'
                            && input.charAt(i + 1) <= '7') {
                        code.append(input.charAt(i + 1));
                        i++;
                        if ((i < input.length() - 1) && input.charAt(i + 1) >= '0'
                                && input.charAt(i + 1) <= '7') {
                            code.append(input.charAt(i + 1));
                            i++;
                        }
                    }
                    sb.append(Integer.parseInt(code.toString(), 8));
                    continue;
                }
                switch (nextChar) {
                    case 'b' -> ch = '\b';
                    case 'f' -> ch = '\f';
                    case 'n' -> ch = '\n';
                    case 'r' -> ch = '\r';
                    case 't' -> ch = '\t';
                    case '\"' -> ch = '\"';
                    case '\'' -> ch = '\'';
                    case 'u' -> {
                        if (i >= input.length() - 5) {
                            ch = 'u';
                            break;
                        }
                        int code = Integer.parseInt(String.valueOf(input.charAt(i + 2)) + input.charAt(i + 3) + input.charAt(i + 4) + input.charAt(i + 5), 16);
                        sb.append(Character.toChars(code));
                        i += 5;
                        continue;
                    }
                }
                i++;
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
