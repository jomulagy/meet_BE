package com.example.meet.auth.application.port.in;

import com.example.meet.member.application.domain.entity.Member;

public interface GetLogginedInfoUseCase {
    Member get();
}
