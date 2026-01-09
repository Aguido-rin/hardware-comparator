package com.shinra.hardware.service;

import com.shinra.hardware.dto.PriceHistoryPointDTO;
import java.util.List;

public interface PriceHistoryService {

    List<PriceHistoryPointDTO> getHistoryForListing(Long listingId);
}
