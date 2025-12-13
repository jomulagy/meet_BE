package com.example.meet.auth.application.port.in;

import com.example.meet.entity.Member;

public interface GetLogginedInfoUseCase {
    Member getCurrentMember();
}
