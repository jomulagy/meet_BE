package com.example.meet.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TemplateArgs {
    @JsonProperty("title")
    private String title;
    @JsonProperty("scheduleType")
    private String scheduleType;
    @JsonProperty("BUT")
    private String but;
    @JsonProperty("year")
    private String year;
    @JsonProperty("nextYear")
    private String nextYear;
}
