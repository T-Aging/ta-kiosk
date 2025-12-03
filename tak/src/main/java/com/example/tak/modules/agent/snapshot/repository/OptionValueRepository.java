package com.example.tak.modules.agent.snapshot.repository;

import com.example.tak.common.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionValueRepository extends JpaRepository<OptionValue, Integer> {
    List<OptionValue> findByOptionGroupId(Integer groupId);
}
