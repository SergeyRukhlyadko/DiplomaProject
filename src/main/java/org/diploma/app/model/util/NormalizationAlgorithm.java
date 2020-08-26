package org.diploma.app.model.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.math.RoundingMode;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NormalizationAlgorithm {

    static int DEFAULT_SCALE = 2;
    static RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.DOWN;

    public static float normalizeMinMax(double min, double max, double weight) {
        BigDecimal minDecimal = new BigDecimal(min).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        BigDecimal maxDecimal = new BigDecimal(max).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        BigDecimal divider = maxDecimal.subtract(minDecimal);

        BigDecimal weightDecimal = new BigDecimal(weight).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
        BigDecimal dividend = weightDecimal.subtract(minDecimal);

        return dividend.divide(divider, DEFAULT_ROUNDING_MODE).floatValue();
    }
}
