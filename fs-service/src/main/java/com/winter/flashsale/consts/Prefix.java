package com.winter.flashsale.consts;

public final class Prefix {
    private Prefix() {
    }

    public static final String ZK_GOODS_ROOT_PATH = "/flashsale_goods";

    public static final String RED_GOODS_KEY_PREFIX = "fs:stock:";
    public static final String RED_ORDER_KEY_PREFIX = "fs:order:";

    public static final String RED_SESSION_KEY_PREFIX = "fs:session:";

    public static final String REDISSON_UPLOAD_STOCK = "fs:upload:lock";
}
