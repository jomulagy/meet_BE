package com.example.meet.api.auth.application.port.in;

import com.example.meet.api.member.application.domain.entity.Member;

public interface GetLogginedInfoUseCase {
    Member get();
}
