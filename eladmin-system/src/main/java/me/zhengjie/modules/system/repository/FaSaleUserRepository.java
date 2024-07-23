package me.zhengjie.modules.system.repository;

import me.zhengjie.modules.system.domain.FaSaleUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FaSaleUserRepository extends JpaRepository<FaSaleUser, Integer>, JpaSpecificationExecutor<FaSaleUser> {
}
