package com.communitylibrary.community_library.repository;

import com.communitylibrary.community_library.entity.BorrowRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {

    List<BorrowRequest> findByBorrowerId(Long borrowerId);

    List<BorrowRequest> findByOwnerId(Long ownerId);

    List<BorrowRequest> findByItemId(Long itemId);
} 