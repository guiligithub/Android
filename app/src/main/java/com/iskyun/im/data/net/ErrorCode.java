package com.iskyun.im.data.net;


import com.hyphenate.EMError;

public class ErrorCode extends EMError {

    /**
     * 网络问题请稍后重试
     */
    public static final int EM_ERR_UNKNOWN = -20;

    public enum Error {

        UNKNOWN_ERROR(-9999, 0);

        private int code;
        private int messageId;

        private Error(int code, int messageId) {
            this.code = code;
            this.messageId = messageId;
        }

        public static Error parseMessage(int errorCode) {
            for (Error error: Error.values()) {
                if(error.code == errorCode) {
                    return error;
                }
            }
            return UNKNOWN_ERROR;
        }


        public int getMessageId() {
            return messageId;
        }
    }
}
