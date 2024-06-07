package com.example.record_redbag;// RedEnvelopeContract.java

import android.provider.BaseColumns;

public final class RedEnvelopeContract {

    private RedEnvelopeContract() {}

    public static class RedEnvelopeEntry implements BaseColumns {
        public static final String TABLE_NAME = "red_envelopes";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_TIME = "time";
    }
}
