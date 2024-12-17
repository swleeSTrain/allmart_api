package org.sunbong.allmart_api.banner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunbong.allmart_api.banner.domain.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
}
