//package org.sunbong.allmart_api.customer;
//
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.sunbong.allmart_api.customer.domain.Customer;
//import org.sunbong.allmart_api.customer.dto.CustomerRequestDTO;
//import org.sunbong.allmart_api.customer.repository.CustomerRepository;
//import org.sunbong.allmart_api.customer.service.CustomerService;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@Log4j2
//class CustomerServiceTest {
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
//
//        int count = 0;
//        while(count < 10){
//            String number = "1" + String.valueOf(count);
//            Customer cus = Customer.builder()
//                    .name("석선진")
//                    .phoneNumber(number)
//                    .build();
//            customerRepository.save(cus);
//            count++;
//        }
//    }
//
//    @AfterEach
//    public void processAfterTest(){
//        customerService.deleteAllCustomers();
//    }
//
//    @Test
//    public void findCustomerByPhoneNumber() {
//        Optional<Customer> customerByPhoneNumber = customerService.findByPhoneNumber("01099848450");
//        assertThat( customerByPhoneNumber.get().getPhoneNumber()).isEqualTo("01099848450");
//    }
//
//    @Test
//    public void findCustomerByName(){
//        Optional<List<Customer>> customers = customerService.findByNames("석선진");
//        customers.stream().forEach(customer->{
//            for (Customer cust : customer){
//                log.info("------------findCustomerByName-----------");
//                log.info(cust.getCustomerID());
//                log.info(cust.getPhoneNumber());
//                log.info(cust.getName());
//            }
//
//        });
//
//    }
//
//    @Test
//    public void customListTest(){
//        Sort sort = Sort.by("name").ascending();
//        Pageable pageable = PageRequest.of(1,10,sort );
//        Page<Customer> page = customerService.list(pageable);
//
//        //검증
//        assertNotNull(page);
//        assertTrue(page.hasContent());
//        assertEquals(10, page.getSize());
//
//        // 결과 출력
//        page.getContent().forEach(customer -> {
//            log.info(customer);
//        });
//
//    }
//
//
//}