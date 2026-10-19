
package com.communitylibrary.community_library.controller;

import com.communitylibrary.community_library.entity.BorrowRequest;
import com.communitylibrary.community_library.service.BorrowRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "http://localhost:5173")
public class BorrowRequestController {

    private final BorrowRequestService borrowRequestService;

    public BorrowRequestController(BorrowRequestService borrowRequestService) {
        this.borrowRequestService = borrowRequestService;
    }

    @GetMapping
    public List<BorrowRequest> getAllRequests() {
        return borrowRequestService.getAllRequests();
    }

    @GetMapping("/borrower/{borrowerId}")
    public List<BorrowRequest> getRequestsByBorrower(@PathVariable Long borrowerId) {
        return borrowRequestService.getRequestsByBorrower(borrowerId);
    }

    @GetMapping("/owner/{ownerId}")
    public List<BorrowRequest> getRequestsByOwner(@PathVariable Long ownerId) {
        return borrowRequestService.getRequestsByOwner(ownerId);
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody BorrowRequest request) {
        try {
            return ResponseEntity.ok(borrowRequestService.createRequest(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long requestId,
            @RequestParam String status) {
        try {
            return ResponseEntity.ok(
                borrowRequestService.updateRequestStatus(requestId, status)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/return")
    public ResponseEntity<?> submitReturn(
            @PathVariable Long requestId,
            @RequestBody Map<String, Object> body) {
        try {
            Integer rating = Integer.valueOf(body.get("rating").toString());
            String comment = body.get("comment") == null
                    ? ""
                    : body.get("comment").toString();

            return ResponseEntity.ok(
                borrowRequestService.submitReturn(requestId, rating, comment)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/confirm-return")
    public ResponseEntity<?> confirmReturn(@PathVariable Long requestId) {
        try {
            return ResponseEntity.ok(
                borrowRequestService.confirmReturn(requestId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
