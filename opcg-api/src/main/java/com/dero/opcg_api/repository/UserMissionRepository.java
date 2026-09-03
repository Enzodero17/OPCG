package com.dero.opcg_api.repository;

import com.dero.opcg_api.model.MissionCategory;
import com.dero.opcg_api.model.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    List<UserMission> findByUserId(UUID userId);

    Optional<UserMission> findByUserIdAndMissionIdAndPeriodKey(UUID userId, Long missionId, String periodKey);

    @Query("SELECT um FROM UserMission um WHERE um.user.id = :userId and um.mission.category = :category")
    List<UserMission> findByUserIdAndMissionCategory(UUID userId, MissionCategory category);
}
