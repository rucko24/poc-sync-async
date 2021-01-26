package com.simualation.syncvsasync.enumsizesfornumbers;

/**
 *
 */
public enum EnumSizeForRandomNumbers {

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
            stringBuilder.append("5.000.000");
        } else if(size.getSize().equals(TEN_MILLION.getSize())) {
            stringBuilder.append("10.000.000");
        }
        return stringBuilder.toString();
    }

}
