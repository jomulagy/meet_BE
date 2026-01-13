package com.example.meet.infrastructure.config.jpaAudit;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class JpaAuditingConfig {

    private final MemberRepository memberRepository;

    @Bean
    public AuditorAware<Member> auditorAware() {
        return new SecurityAuditorAware(memberRepository);
    }

    private static class SecurityAuditorAware implements AuditorAware<Member> {

        private final MemberRepository memberRepository;

        private SecurityAuditorAware(MemberRepository memberRepository) {
            this.memberRepository = memberRepository;
        }

        @Override
        public Optional<Member> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            Object principal = authentication.getPrincipal();
            String userId = null;

            if (principal instanceof UserDetails userDetails) {
                userId = userDetails.getUsername();
            } else if (principal instanceof String) {
                userId = (String) principal;
            }

            if (userId == null) {
                return Optional.empty();
            }

            try {
                Long memberId = Long.parseLong(userId);
                return memberRepository.findById(memberId);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
    }
}

