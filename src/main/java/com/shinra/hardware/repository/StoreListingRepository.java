package com.shinra.hardware.repository;

import com.shinra.hardware.model.StoreListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreListingRepository extends JpaRepository<StoreListing, Long> {

    List<StoreListing> findByProduct_IdOrderByCurrentPriceAsc(Long productId);

    Optional<StoreListing> findByProduct_IdAndStore_Id(Long productId, Integer storeId);

    Boolean existsByUrlSource(String urlSource);

    @Query("SELECT MIN(sl.currentPrice) FROM StoreListing sl "+" WHERE sl.product.id = :productId AND sl.isInStock = true")
    Optional<BigDecimal> findMinPriceByProductId(@Param("productId") Long productId);

    @Query("SELECT sl FROM StoreListing sl WHERE sl.lastCheckedAt < :cutoffDate OR sl.lastCheckedAt IS NULL")
    List<StoreListing> findOutdatedListings(@Param("cutoffDate") java.time.OffsetDateTime cutoffDate);
}