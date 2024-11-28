package org.sunbong.allmart_api.member.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.domain.QMart;
import org.sunbong.allmart_api.mart.domain.QMartLogo;
import org.sunbong.allmart_api.member.domain.MemberEntity;
import org.sunbong.allmart_api.member.domain.QMemberEntity;
import org.sunbong.allmart_api.member.dto.MemberMartDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MemberSearchImpl extends QuerydslRepositorySupport implements MemberSearch {
    public MemberSearchImpl() {
        super(MemberEntity.class);
    }

    @Override
    public MemberEntity findByEmail(String email) {
        return from(QMemberEntity.memberEntity)
                .where(QMemberEntity.memberEntity.email.eq(email))
                .fetchOne();
    }

    @Override
    public MemberMartDTO findMartInfo(String email) {

        QMemberEntity member = QMemberEntity.memberEntity;
        QMart mart = QMart.mart;
        QMartLogo martLogo = QMartLogo.martLogo;

        JPQLQuery<Tuple> query = from(member)
                .join(member.mart, mart)
                .leftJoin(mart.attachLogo, martLogo)
                .where(member.email.eq(email)
                        .and(martLogo.ord.eq(0)))
                .select(mart.martName, mart.martID,martLogo.logoURL);

        Tuple result = query.fetchOne();

        if (result == null) {
            return null;
        }

        // DTO 생성 및 반환
        return MemberMartDTO.builder()
                .martID(result.get(mart.martID))
                .martName(result.get(mart.martName))
                .logoURL(result.get(martLogo.logoURL))
                .build();
    }


}
