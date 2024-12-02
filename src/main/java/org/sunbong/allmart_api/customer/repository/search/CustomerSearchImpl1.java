
// bean 문제 때문에 주석 처리함 제대로 못잡음

//    package org.sunbong.allmart_api.customer.repository.search;
//
//    import com.querydsl.core.Tuple;
//    import com.querydsl.jpa.JPQLQuery;
//    import com.querydsl.jpa.impl.JPAQueryFactory;
//    import lombok.extern.log4j.Log4j2;
//    import org.springframework.context.annotation.Primary;
//    import org.springframework.data.domain.*;
//    import org.springframework.stereotype.Repository;
//    import org.sunbong.allmart_api.common.dto.PageRequestDTO;
//    import org.sunbong.allmart_api.common.dto.PageResponseDTO;
//    import org.sunbong.allmart_api.customer.domain.Customer;
//    import org.sunbong.allmart_api.customer.domain.CustomerLoginType;
//    import org.sunbong.allmart_api.customer.domain.QCustomer;
//    import org.sunbong.allmart_api.customer.dto.CustomerListDTO;
//    import org.sunbong.allmart_api.customer.dto.CustomerResponseDTO;
//    import org.sunbong.allmart_api.qrcode.domain.QQrCode;
//
//    import java.util.List;
//    import java.util.stream.Collectors;
//
//    @Primary
//    @Repository
//    @Log4j2
//    public class CustomerSearchImpl1 implements CustomerSearch {
//
//        private final JPAQueryFactory queryFactory;
//
//        // JPAQueryFactory 주입
//        public CustomerSearchImpl1(JPAQueryFactory queryFactory) {
//            this.queryFactory = queryFactory;
//        }
//
//        @Override
//        public Page<Customer> list(Pageable pageable) {
//            QCustomer customer = QCustomer.customer;
//            QQrCode qrCode = QQrCode.qrCode;
//
//            JPQLQuery<Customer> query = queryFactory.selectFrom(customer)
//                    .leftJoin(qrCode).on(qrCode.customer.eq(customer))
//                    .groupBy(customer);
//
//            long total = query.fetchCount();
//
//            List<Customer> customers = query.offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch();
//
//            return new PageImpl<>(customers, pageable, total);
//        }
//
//
//        @Override
//        public PageResponseDTO<CustomerListDTO> listByName(String name, PageRequestDTO pageRequestDTO) {
//            Pageable pageable = PageRequest.of(
//                    pageRequestDTO.getPage() - 1,
//                    pageRequestDTO.getSize(),
//                    Sort.by("customerID").descending()
//            );
//
//            QCustomer customer = QCustomer.customer;
//            QQrCode qrCode = QQrCode.qrCode;
//
//            JPQLQuery<Tuple> query = queryFactory.select(customer, qrCode)
//                    .from(customer)
//                    .leftJoin(qrCode).on(qrCode.customer.eq(customer))
//                    .where(customer.name.eq(name))
//                    .groupBy(customer);
//
//            long total = query.fetchCount();
//
//            List<CustomerListDTO> dtoList = query.offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch()
//                    .stream()
//                    .map(tuple -> {
//                        Customer customerOne = tuple.get(customer);
//                        return CustomerListDTO.builder()
//                                .customerID(customerOne.getCustomerID())
//                                .name(customerOne.getName())
//                                .build();
//                    })
//                    .collect(Collectors.toList());
//
//            return PageResponseDTO.<CustomerListDTO>withAll()
//                    .dtoList(dtoList)
//                    .totalCount(total)
//                    .pageRequestDTO(pageRequestDTO)
//                    .build();
//        }
//
//        @Override
//        public PageResponseDTO<CustomerListDTO> listByPhoneNumber(String phoneNumber, PageRequestDTO pageRequestDTO) {
//            return null;
//        }
//
//        @Override
//        public CustomerResponseDTO findByPhoneNumberOrEmail(String customerData, CustomerLoginType loginType) {
//            QCustomer customer = QCustomer.customer;
//
//            Customer result = queryFactory.selectFrom(customer)
//                    .where(loginType == CustomerLoginType.PHONE
//                            ? customer.phoneNumber.eq(customerData)
//                            : customer.email.eq(customerData))
//                    .fetchOne();
//
//            if (result != null) {
//                return CustomerResponseDTO.builder()
//                        .email(result.getEmail())
//                        .phoneNumber(result.getPhoneNumber())
//                        .name(result.getName())
//                        .loyaltyPoint(result.getLoyaltyPoints())
//                        .build();
//            }
//
//
//            return null;
//        }
//    }
