package com.ultraship.tms.repository;

import com.ultraship.tms.domain.ShipmentEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ShipmentRepository
        extends JpaRepository<ShipmentEntity, Long>, ShipmentRepositoryCustom {

//    Page<ShipmentEntity> findByCarrierNameContainingIgnoreCase(
//            String carrierName,
//            Pageable pageable
//    );
//
//    @Query("""
//        select s from ShipmentEntity s
//        where (:carrier is null or lower(s.carrierName) like lower(concat('%', :carrier, '%')))
//          and (:afterId is null or s.id > :afterId)
//        order by s.id asc
//    """)
//    List<ShipmentEntity> findShipmentsAfterId(
//            @Param("carrier") String carrier,
//            @Param("afterId") Long afterId,
//            Pageable pageable
//    );
//
//    @Query("""
//    select s from ShipmentEntity s
//    where (:carrier is null or lower(s.carrierName) like lower(concat('%', :carrier, '%')))
//      and (
//           :afterRate is null
//           or s.rate > :afterRate
//           or (s.rate = :afterRate and s.id > :afterId)
//      )
//    order by s.rate asc, s.id asc
//""")
//    List<ShipmentEntity> findAfterRateAsc(
//            String carrier,
//            BigDecimal afterRate,
//            Long afterId,
//            Pageable pageable
//    );

//    @Query("""
//        select s from ShipmentEntity s
//        where (:carrier is null or lower(s.carrierName) like lower(concat('%', :carrier, '%')))
//          and (
//               :afterRate is null
//               or s.rate > :afterRate
//               or (s.rate = :afterRate and s.id > :afterId)
//          )
//    """)
//    List<ShipmentEntity> findAfterCursor(
//            @Param("carrier") String carrier,
//            @Param("afterRate") BigDecimal afterRate,
//            @Param("afterId") Long afterId,
//            Pageable pageable
//    );

}