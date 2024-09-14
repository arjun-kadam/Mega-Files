package com.megafiles.repository;

import com.megafiles.entity.UnblockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnblockRequestRepository extends JpaRepository<UnblockRequest,Long> {
}
