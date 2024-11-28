package org.sunbong.allmart_api.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.repository.MartRepository;
import org.sunbong.allmart_api.member.domain.MemberEntity;
import org.sunbong.allmart_api.member.domain.MemberRole;
import org.sunbong.allmart_api.member.dto.MemberDTO;
import org.sunbong.allmart_api.member.dto.MemberMartDTO;
import org.sunbong.allmart_api.member.exception.MemberExceptions;
import org.sunbong.allmart_api.member.repository.MemberRepository;

import java.util.Optional;

//사실 스프링은 인터페이스로 뽑는게좋음
@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final MartRepository martRepository;

    private final PasswordEncoder passwordEncoder;

    public MemberMartDTO martInfo(String email) {

        MemberMartDTO result = memberRepository.findMartInfo(email);

        return result;
    }

    public MemberDTO authenticate(String email, String password) {

        Optional<MemberEntity> result = memberRepository.findById(email);
        MemberEntity member = result.orElseThrow(() -> MemberExceptions.BAD_AUTH.get());

        String enPw = member.getPw();

        boolean match = passwordEncoder.matches(password, enPw);

        if (!match) {
            throw MemberExceptions.BAD_AUTH.get();
        }
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail(email);
        memberDTO.setPw(enPw);
        memberDTO.setRole(member.getRole().toString());

        return memberDTO;
    }

    public MemberDTO signUp(MemberDTO memberDTO) {

        Mart mart = martRepository.findById(memberDTO.getMartID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid mart ID"));

        MemberEntity memberEntity = MemberEntity.builder()
                .email(memberDTO.getEmail())
                .pw(passwordEncoder.encode(memberDTO.getPw()))
                .role(MemberRole.valueOf(memberDTO.getRole()))
                .mart(mart)
                .build();

        memberRepository.save(memberEntity);

        return memberDTO;
    }

    public MemberDTO updateMember(MemberDTO memberDTO) {
        Optional<MemberEntity> result = memberRepository.findById(memberDTO.getEmail());
        MemberEntity memberEntity = result.orElseThrow(() -> MemberExceptions.BAD_AUTH.get());
        memberEntity.setPw(passwordEncoder.encode(memberDTO.getPw()));
        memberRepository.save(memberEntity);
        return memberDTO;
    }

    public MemberDTO deleteMember(String email) {
        MemberDTO memberDTO = getMember(email);
        memberRepository.deleteById(email);

        return memberDTO;
    }

    public MemberDTO getMember(String email) {
        Optional<MemberEntity> result = memberRepository.findById(email);
        MemberEntity memberEntity = result.orElseThrow(() -> MemberExceptions.BAD_AUTH.get());
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail(email);
        memberDTO.setPw(memberEntity.getPw());
        memberDTO.setRole(memberEntity.getRole().toString());
        return memberDTO;
    }

}
