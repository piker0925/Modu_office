package com.modu.office.repository;

import com.modu.office.entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Office 엔티티에 대한 데이터 액세스 레포지토리
 */
@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {

    /**
     * 이름으로 지점 검색 (부분 일치)
     * 
     * @param name 검색할 지점 이름
     * @return 이름에 해당하는 지점 목록
     */
    List<Office> findByNameContaining(String name);

    /**
     * 정확한 이름으로 지점 찾기
     * 
     * @param name 지점 정확한 이름
     * @return 해당 지점 (Optional)
     */
    Optional<Office> findByName(String name);

    /**
     * 위치로 지점 검색 (부분 일치)
     * 
     * @param location 검색할 위치
     * @return 위치에 해당하는 지점 목록
     */
    List<Office> findByLocationContaining(String location);
}
