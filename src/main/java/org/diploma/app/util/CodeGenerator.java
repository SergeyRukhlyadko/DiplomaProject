package org.diploma.app.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Random;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeGenerator {

    static String SYMBOLS = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public char[] generate(int codeLength) {
        Random random = new Random();
        char[] code = new char[codeLength];
        for(byte i = 0; i < codeLength; i++)
            code[i] = SYMBOLS.charAt(random.nextInt(SYMBOLS.length() - 1));
        return code;
    }
}
