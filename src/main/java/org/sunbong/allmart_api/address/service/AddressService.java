package org.sunbong.allmart_api.address.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sunbong.allmart_api.address.domain.Address;
import org.sunbong.allmart_api.address.dto.AddressDTO;
import org.sunbong.allmart_api.address.repository.AddressRepository;
import org.sunbong.allmart_api.customer.domain.Customer;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;

    // 주소 저장 (Customer와 연관 관계 처리)
    public Address saveAddress(AddressDTO addressDTO, Customer customer) {
        // AddressDTO -> Address Entity 변환
        Address address = Address.builder()
                .postcode(addressDTO.getPostcode())
                .roadAddress(addressDTO.getRoadAddress())
                .detailAddress(addressDTO.getDetailAddress())
                .fullAddress(addressDTO.getRoadAddress() + " " + addressDTO.getDetailAddress())
                .customer(customer) // Customer 연관 설정
                .build();

        // 데이터베이스에 저장
        return addressRepository.save(address);
    }

    // DTO 변환 메서드 (Entity -> DTO)
    public AddressDTO convertToDTO(Address address) {
        return AddressDTO.builder()
                .addressID(String.valueOf(address.getAddressID()))
                .postcode(address.getPostcode())
                .roadAddress(address.getRoadAddress())
                .detailAddress(address.getDetailAddress())
                .fullAddress(address.getFullAddress())
                .build();
    }

    // 모든 주소 조회 (DTO로 반환)
    public List<AddressDTO> getAllAddresses() {
        return addressRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
