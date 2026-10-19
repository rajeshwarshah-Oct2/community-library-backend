
package com.communitylibrary.community_library.controller;

import com.communitylibrary.community_library.entity.Item;
import com.communitylibrary.community_library.service.ItemService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:5173")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/owner/{ownerId}")
    public List<Item> getItemsByOwner(@PathVariable Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            itemService.deleteItem(itemId, reason);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{itemId}/availability")
    public ResponseEntity<?> toggleAvailability(@PathVariable Long itemId) {
        try {
            return ResponseEntity.ok(itemService.toggleAvailability(itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Long itemId,
            @RequestBody Item item) {
        try {
            return ResponseEntity.ok(itemService.updateItem(itemId, item));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}