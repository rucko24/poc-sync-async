package com.simulation.syncvsasync.enumsizesfornumbers;

/**
 * @author rubn
 */
public enum EnumSizeForRandomNumbers {

    ERROR(-1L),
    FIVE_MILLION(5_000_000L),
    TEN_MILLION(10_000_000L);

    private Long size;

    EnumSizeForRandomNumbers(final Long size) {
        this.size = size;
    }

    public Long getSize() {
        return size;
    }

    public static String getItemLabel(final EnumSizeForRandomNumbers size) {
        StringBuilder stringBuilder = new StringBuilder();
        if(size.getSize().equals(FIVE_MILLION.getSize())) {
            stringBuilder.append("😐 5.000.000");
        } else if(size.getSize().equals(TEN_MILLION.getSize())) {
            stringBuilder.append("🔥 10.000.000");
        } else {
            stringBuilder.append("\uD83D\uDE2D Handle me! I'm a -1");
        }
        return stringBuilder.toString();
    }

}
