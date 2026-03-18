package com.career.plan.repository;

import com.career.plan.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 根据权限名查询权限
     */
    Optional<Permission> findByName(String name);
    
    /**
     * 查询所有权限
     */
    List<Permission> findAllByOrderByCreatedAtAsc();
}
