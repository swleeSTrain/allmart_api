package org.sunbong.allmart_api.customer.repository.search;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.customer.domain.*;
import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
import org.sunbong.allmart_api.customer.dto.CustomerMartDTO;
import org.sunbong.allmart_api.customer.dto.CustomerResponseDTO;
import org.sunbong.allmart_api.mart.domain.MartCustomer;
import org.sunbong.allmart_api.mart.domain.QMart;
import org.sunbong.allmart_api.mart.domain.QMartCustomer;
import org.sunbong.allmart_api.mart.domain.QMartLogo;
import org.sunbong.allmart_api.qrcode.domain.QQrCode;
import org.sunbong.allmart_api.qrcode.domain.QrCode;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Log4j2
public class CustomerSearchImpl extends QuerydslRepositorySupport  implements CustomerSearch {

    public CustomerSearchImpl() {
        super(Customer.class);

    }

    @Override
    public Optional<CustomerMartDTO> findMartInfo(String userData) {

        QMartCustomer martCustomer = QMartCustomer.martCustomer;
        QCustomer customer = QCustomer.customer;
        QMart mart = QMart.mart;
        QMartLogo martLogo = QMartLogo.martLogo;

        log.info("===================");

        // 이메일과 휴대폰 번호 구분
        BooleanExpression condition = userData.contains("@")
                ? customer.email.eq(userData)
                : customer.phoneNumber.eq(userData);

        // Query 작성
        JPQLQuery<Tuple> query = from(martCustomer)
                .join(martCustomer.mart, mart)
                .join(martCustomer.customer, customer)
                .leftJoin(mart.attachLogo, martLogo)
                .where(condition)
                .groupBy(mart.martID, mart.martName, martLogo.logoURL) // 중복 제거
                .select(mart.martID, mart.martName, martLogo.logoURL);

        // 결과 조회
        Tuple result = query.fetchFirst();

        log.info(result);

        if (result == null) {
            return Optional.empty();
        }

        // DTO 생성 및 반환
        return Optional.of(
                CustomerMartDTO.builder()
                        .martID(result.get(mart.martID))
                        .martName(result.get(mart.martName))
                        .logoURL(result.get(martLogo.logoURL))
                        .build()
        );
    }

    @Override
    public Optional<CustomerResponseDTO> findCustomerWithMart(String userData, CustomerLoginType loginType) {
        QCustomer customer = QCustomer.customer;
        QMartCustomer martCustomer = QMartCustomer.martCustomer;

        // CustomerMart를 기준으로 고객 정보와 관련된 Mart를 가져오는 쿼리
        JPQLQuery<MartCustomer> query = from(martCustomer)
                .leftJoin(martCustomer.customer, customer)
                .where(
                        loginType == CustomerLoginType.PHONE
                                ? customer.phoneNumber.eq(userData)
                                : customer.email.eq(userData)
                );

        MartCustomer result = query.fetchOne();

        if (result == null) {
            return Optional.empty();
        }

        // 고객 정보 추출 (CustomerMart에서 직접 연관된 Customer 정보를 가져옴)
        Customer customerResult = result.getCustomer();
        Long martID = result.getMart().getMartID(); // CustomerMart에서 직접 마트 정보 추출

        // DTO 변환
        CustomerResponseDTO responseDTO = CustomerResponseDTO.builder()
                .customerID(customerResult.getCustomerID()) // Customer에서 ID만 가져옴
                .name(customerResult.getName())
                .phoneNumber(customerResult.getPhoneNumber())
                .email(customerResult.getEmail())
                .loyaltyPoint(customerResult.getLoyaltyPoints())
                .martID(martID)  // 해당 마트의 ID를 가져옴
                .build();

        return Optional.of(responseDTO);
    }



    @Override
    public Page<Customer> list(Pageable pageable) {
        QCustomer customer = QCustomer.customer;
        QQrCode qrCode = QQrCode.qrCode;
        JPQLQuery<Customer> query = from(customer);
        query.leftJoin(qrCode).on(qrCode.customer.eq(customer));
        query.groupBy(customer);

        this.getQuerydsl().applyPagination(pageable, query);
        JPQLQuery<Tuple> tupleQuery = query.select(customer,qrCode);
        List<Tuple> resultList = tupleQuery.fetch();

        //'Tuple' 데이터를 'Customer' 객체로 변환
        List<Customer> customers = resultList.stream().map(tuple -> {
            Customer cust = tuple.get(customer);
            QrCode code = tuple.get(qrCode);

            return cust;


        }).collect(Collectors.toList());

        // 전체 카운트 조회(페이징을 위해)
        long total = query.fetchCount();

        //'Page<Customer> ' 생성 및 변환
        return new PageImpl<>(customers, pageable, total);
    }

    @Override
    public PageResponseDTO<CustomerListDTO> listByName(String name, PageRequestDTO pageRequestDTO) {
        Pageable pageable =
                PageRequest.of(
                        pageRequestDTO.getPage() -1,
                        pageRequestDTO.getSize(),
                        Sort.by("customerID").descending()
                );

        QCustomer customer = QCustomer.customer;
        QQrCode qrCode = QQrCode.qrCode;

        JPQLQuery<Customer> query = from(customer);
        query.leftJoin(qrCode).on(qrCode.customer.eq(customer));
        query.where(qrCode.customer.name.eq(name));
        query.groupBy(customer);

        //페이징 처리 정렬처리
        this.getQuerydsl().applyPagination(pageable, query);

        JPQLQuery<Tuple> tupleQuery =
                query.select(
                        customer,
                        qrCode
                );

        List<Tuple> tupleList = tupleQuery.fetch();

        log.info(tupleList);

        if(tupleList.isEmpty()) {
            return null;
        }

        List<CustomerListDTO> dtoList = new ArrayList<>();

        tupleList.forEach(t -> {
            Customer customerOne = t.get(0, Customer.class);

            CustomerListDTO dto = CustomerListDTO.builder()
                    .customerID(customerOne.getCustomerID())
                    .name(customerOne.getName())
                    .build();

            dtoList.add(dto);

        });

        long total = tupleQuery.fetchCount();

        return PageResponseDTO.<CustomerListDTO>withAll()
                .dtoList(dtoList)
                .totalCount(total)
                .pageRequestDTO(pageRequestDTO)
                .build();
    }

    @Override
    public PageResponseDTO<CustomerListDTO> listByPhoneNumber(String phoneNumber, PageRequestDTO pageRequestDTO) {
        return null;
    }

    @Override
    public CustomerResponseDTO findByPhoneNumberOrEmail(String customerData, CustomerLoginType loginType) {

        QCustomer customer = QCustomer.customer;
        log.debug(this.getQuerydsl());
        JPQLQuery<Customer> query = from(customer);

        if (loginType == CustomerLoginType.PHONE) {
            query.where(customer.phoneNumber.eq(customerData));
        } else if (loginType == CustomerLoginType.SOCIAL) {
            query.where(customer.email.eq(customerData));
        } else {
            throw new IllegalArgumentException("Invalid login type: " + loginType);
        }

        Customer result = query.fetchOne();

        if (result != null) {
            // Map the Customer entity to CustomerResponseDTO (you can use a mapper here)
            return CustomerResponseDTO.builder()
                    .email(result.getEmail())
                    .phoneNumber(result.getPhoneNumber())
                    .name(result.getName())
                    .loyaltyPoint(result.getLoyaltyPoints())
                    .build();

        }

        return null; // Or throw a custom exception if no result is found
    }



}
