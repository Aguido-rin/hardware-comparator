package com.shinra.hardware.repository;

import com.shinra.hardware.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByListingIdOrderByRecordedAtAsc(Long listingId);
}
