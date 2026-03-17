package com.career.plan.repository;

import com.career.plan.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    /**
     * 根据角色名查询角色
     */
    Optional<Role> findByName(String name);
    
    /**
     * 查询所有角色
     */
    List<Role> findAllByOrderByCreatedAtDesc();
}
