package com.sellit.api.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewProviderReviewEvent extends ApplicationEvent {
    private String proverReviewLogUuid;
    public NewProviderReviewEvent(String proverReviewLogUuid) {
        super(proverReviewLogUuid);
        this.proverReviewLogUuid = proverReviewLogUuid;
    }
}
