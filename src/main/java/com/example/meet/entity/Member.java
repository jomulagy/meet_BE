package com.example.meet.entity;

import com.example.meet.common.enumulation.MemberPrevillege;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "previllege",nullable = false)
    private MemberPrevillege previllege = MemberPrevillege.denied;

    public void updatePrevillege(MemberPrevillege previllege){
        this.previllege = previllege;
    }

    public void updateUUID(String uuid) {
        this.uuid = uuid;
    }
}
