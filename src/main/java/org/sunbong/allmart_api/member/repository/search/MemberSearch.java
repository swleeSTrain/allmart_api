package org.sunbong.allmart_api.member.repository.search;

import org.sunbong.allmart_api.member.domain.MemberEntity;
import org.sunbong.allmart_api.member.dto.MemberMartDTO;

public interface MemberSearch {

    MemberEntity findByEmail(String email);

    MemberMartDTO findMartInfo(String email);

}
