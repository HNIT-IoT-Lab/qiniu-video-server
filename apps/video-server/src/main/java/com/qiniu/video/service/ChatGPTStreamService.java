package com.qiniu.video.service;

import com.qiniu.video.gpt.billing.CreditGrantsResponse;
import com.qiniu.video.gpt.billing.SubscriptionData;
import com.qiniu.video.gpt.billing.UseageResponse;
import com.qiniu.video.gpt.chat.ChatCompletion;
import com.qiniu.video.gpt.chat.ChatCompletionResponse;
import io.reactivex.Single;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 15:24
 */

public interface ChatGPTStreamService {

    /**
     * chat
     */
    Single<ChatCompletionResponse> chatCompletion(ChatCompletion chatCompletion);


    /**
     * 余额查询
     */
    Single<CreditGrantsResponse> creditGrants();

    /**
     * 余额查询
     */
    Single<SubscriptionData> subscription();

    /**
     * 余额查询
     */
    Single<UseageResponse> usage(String startDate, String endDate);
}
