package com.shinra.hardware.service.impl;

import com.shinra.hardware.dto.PriceHistoryPointDTO;
import com.shinra.hardware.repository.PriceHistoryRepository;
import com.shinra.hardware.service.PriceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {
    
    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public PriceHistoryServiceImpl(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceHistoryPointDTO> getHistoryForListing(Long listingId) {
        return priceHistoryRepository.findByListingIdOrderByRecordedAtAsc(listingId)
                .stream()
                .map(history -> new PriceHistoryPointDTO(
                        history.getRecordedAt(),
                        history.getPrice()
                ))
                .collect(Collectors.toList());
    }
}
