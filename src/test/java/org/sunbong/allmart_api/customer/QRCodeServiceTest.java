//package org.sunbong.allmart_api.customer;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.sunbong.allmart_api.customer.domain.Customer;
//import org.sunbong.allmart_api.customer.dto.CustomerRequestDTO;
//import org.sunbong.allmart_api.customer.repository.CustomerRepository;
//import org.sunbong.allmart_api.customer.service.CustomerService;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@SpringBootTest
//class QRCodeServiceTest {
//
//    @Autowired
//    private QrSerivce qrSerivce;
//
//    @Autowired
//    private CustomerService customerService;
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    public CustomerRequestDTO customerRequestDTO;
//
//   @BeforeEach
//    public void setUp(){
//        //saveCustomerWithPhoneNumber 테스트
//        customerRequestDTO = CustomerRequestDTO.builder()
//                .phoneNumber("01099848450")
//                .createdDate(LocalDateTime.now())
//                .build();
//        Optional<Customer> customer = customerService.addMemberWithPhoneNumber(customerRequestDTO);
//        assertThat(customer.get().getPhoneNumber()).isEqualTo("01099848450");
//    }
//
//   @AfterEach
//    public void processAfterTest(){
//        customerService.deleteCustomerByPhoneNumber("01099848450");
//    }
//
//    @Test
//    public void findCustomerByPhoneNumber() {
//        Optional<Customer> customerByPhoneNumber = customerService.findByPhoneNumber("01099848450");
//        assertThat( customerByPhoneNumber.get().getPhoneNumber()).isEqualTo("01099848450");
//
//
//
//    }
//
//}
