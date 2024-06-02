package com.example.meet.common.dto.request;

import com.example.meet.common.variables.EditMemberPrevillegeOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EditMemberPrevillegeRequestDto {
    private Long userId;
    private Long memberId;
    private EditMemberPrevillegeOption option;
}
