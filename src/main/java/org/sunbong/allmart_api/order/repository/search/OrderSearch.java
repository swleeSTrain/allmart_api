package org.sunbong.allmart_api.order.repository.search;

import org.sunbong.allmart_api.common.dto.PageRequestDTO;
import org.sunbong.allmart_api.common.dto.PageResponseDTO;
import org.sunbong.allmart_api.order.dto.OrderDTO;

public interface OrderSearch {
    PageResponseDTO<OrderDTO> searchOrders(String status, String customerName, PageRequestDTO pageRequestDTO);
}