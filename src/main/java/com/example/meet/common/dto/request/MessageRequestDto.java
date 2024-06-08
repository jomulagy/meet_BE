package com.example.meet.common.dto.request;

import com.example.meet.common.dto.TemplateArgs;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MessageRequestDto {
    @JsonProperty("receiver_uuids")
    private List<String> receiverUuids;

    @JsonProperty("request_url")
    private String requestUrl;

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("template_args")
    private TemplateArgs templateArgs;
}
