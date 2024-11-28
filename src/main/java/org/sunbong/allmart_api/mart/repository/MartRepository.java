package org.sunbong.allmart_api.mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.mart.domain.Mart;
import org.sunbong.allmart_api.mart.repository.search.MartSearch;

public interface MartRepository extends JpaRepository<Mart, Long>, MartSearch {

}
