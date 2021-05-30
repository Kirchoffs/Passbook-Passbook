package com.syh.passbook.passbook.dao;

import com.syh.passbook.passbook.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// ORM
public interface MerchantDao extends JpaRepository<Merchant, Integer> {
    Optional<Merchant> findById(Integer id);
    Optional<Merchant> findByName(String name);
    Optional<List<Merchant>> findByIdIn(List<Integer> ids);
}
