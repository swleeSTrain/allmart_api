package org.sunbong.allmart_api.flyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.sunbong.allmart_api.flyer.domain.Flyer;
import org.sunbong.allmart_api.flyer.repository.search.FlyerSearch;

public interface FlyerRepository extends JpaRepository<Flyer, Long>, FlyerSearch{

}
