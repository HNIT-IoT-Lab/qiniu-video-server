package com.qiniu.video.gpt.exception;

import cn.hnit.common.exception.base.AppException;

/**
 * Custom exception class for chat-related errors
 *
 * @author plexpt
 */
public class ChatException extends AppException {


    /**
     * Constructs a new ChatException with the specified detail message.
     *
     * @param msg the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public ChatException(String msg) {
        super(msg);
    }


}
