
package com.communitylibrary.community_library.service;

import com.communitylibrary.community_library.entity.BorrowRequest;
import com.communitylibrary.community_library.entity.Item;
import com.communitylibrary.community_library.repository.BorrowRequestRepository;
import com.communitylibrary.community_library.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowRequestService {

    private final BorrowRequestRepository borrowRequestRepository;
    private final ItemRepository itemRepository;

    public BorrowRequestService(
            BorrowRequestRepository borrowRequestRepository,
            ItemRepository itemRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.itemRepository = itemRepository;
    }

    public List<BorrowRequest> getAllRequests() {
        return borrowRequestRepository.findAll();
    }

    public List<BorrowRequest> getRequestsByBorrower(Long borrowerId) {
        return borrowRequestRepository.findByBorrowerId(borrowerId);
    }

    public List<BorrowRequest> getRequestsByOwner(Long ownerId) {
        return borrowRequestRepository.findByOwnerId(ownerId);
    }

    public BorrowRequest createRequest(BorrowRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!"Available".equals(item.getAvailability())) {
            throw new RuntimeException("Item is not available");
        }

        request.setItemName(item.getName());
        request.setItemImage(item.getImageUrl());
        request.setOwnerId(item.getOwnerId());
        request.setOwnerName(item.getOwnerName());
        request.setStatus("Pending");
        request.setRating(null);
        request.setComment("");

        return borrowRequestRepository.save(request);
    }

    public BorrowRequest updateRequestStatus(Long requestId, String status) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"Approved".equals(status) && !"Rejected".equals(status)) {
            throw new RuntimeException("Invalid request status");
        }

        if ("Approved".equals(status)) {
            Item item = itemRepository.findById(request.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            if (!"Available".equals(item.getAvailability())) {
                throw new RuntimeException("Item is not available");
            }

            item.setAvailability("Borrowed");
            itemRepository.save(item);
        }

        request.setStatus(status);
        return borrowRequestRepository.save(request);
    }

    public BorrowRequest submitReturn(Long requestId, Integer rating, String comment) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"Approved".equals(request.getStatus())) {
            throw new RuntimeException("This request cannot be returned");
        }

        request.setStatus("Waiting Confirmation");
        request.setRating(rating);
        request.setComment(comment);

        return borrowRequestRepository.save(request);
    }

    public BorrowRequest confirmReturn(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"Waiting Confirmation".equals(request.getStatus())) {
            throw new RuntimeException("Return is not awaiting confirmation");
        }

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setAvailability("Available");
        itemRepository.save(item);

        request.setStatus("Returned");
        return borrowRequestRepository.save(request);
    }
}
