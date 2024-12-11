package org.sunbong.allmart_api.flyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sunbong.allmart_api.flyer.domain.ProducedVideo;
import org.sunbong.allmart_api.flyer.repository.search.ProducedVideoSearch;


public interface ProducedVideoRepository extends JpaRepository<ProducedVideo, Long>, ProducedVideoSearch {
}
