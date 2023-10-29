package com.qiniu.video.service.impl;

import com.qiniu.video.gpt.billing.CreditGrantsResponse;
import com.qiniu.video.gpt.billing.SubscriptionData;
import com.qiniu.video.gpt.billing.UseageResponse;
import com.qiniu.video.gpt.chat.ChatCompletion;
import com.qiniu.video.gpt.chat.ChatCompletionResponse;
import com.qiniu.video.service.ChatGPTStreamService;
import io.reactivex.Single;
import org.springframework.stereotype.Service;

/**
 * <p>
 *
 * </p>
 *
 * @author king
 * @since 2023/10/30 15:24
 */
@Service
public class ChatGPTStreamServiceImpl implements ChatGPTStreamService {


    @Override
    public Single<ChatCompletionResponse> chatCompletion(ChatCompletion chatCompletion) {
        return null;
    }

    @Override
    public Single<CreditGrantsResponse> creditGrants() {
        return null;
    }

    @Override
    public Single<SubscriptionData> subscription() {
        return null;
    }

    @Override
    public Single<UseageResponse> usage(String startDate, String endDate) {
        return null;
    }
}
