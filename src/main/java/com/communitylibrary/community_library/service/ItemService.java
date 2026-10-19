package com.communitylibrary.community_library.service;

import com.communitylibrary.community_library.entity.Item;
import com.communitylibrary.community_library.entity.BorrowRequest;
import com.communitylibrary.community_library.repository.ItemRepository;
import com.communitylibrary.community_library.repository.BorrowRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final BorrowRequestRepository borrowRequestRepository;

    public ItemService(
            ItemRepository itemRepository,
            BorrowRequestRepository borrowRequestRepository) {
        this.itemRepository = itemRepository;
        this.borrowRequestRepository = borrowRequestRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId);
    }

    public Item createItem(Item item) {
        item.setAvailability("Available");
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long itemId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException("Please provide a reason for deleting this item");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        List<BorrowRequest> requests =
                borrowRequestRepository.findByItemId(itemId);

        LocalDateTime deletedAt = LocalDateTime.now();
        String deletionReason = reason.trim();

        for (BorrowRequest request : requests) {
            request.setItemDeleted(true);
            request.setDeletionReason(deletionReason);
            request.setDeletedAt(deletedAt);
        }

        borrowRequestRepository.saveAll(requests);
        itemRepository.delete(item);
    }

    public Item toggleAvailability(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if ("Available".equals(item.getAvailability())) {
            item.setAvailability("Borrowed");
        } else {
            item.setAvailability("Available");
        }

        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item updatedItem) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(updatedItem.getName());
        item.setCategory(updatedItem.getCategory());
        item.setCondition(updatedItem.getCondition());
        item.setDescription(updatedItem.getDescription());
        item.setMaxDays(updatedItem.getMaxDays());
        item.setImageUrl(updatedItem.getImageUrl());

        return itemRepository.save(item);
    }
}