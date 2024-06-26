package com.ssafy.artchain.connectentity.repository;

import com.ssafy.artchain.connectentity.entity.InvestmentLog;
import com.ssafy.artchain.funding.entity.Funding;
import com.ssafy.artchain.funding.entity.FundingProgressStatus;
import com.ssafy.artchain.market.dto.MarketRegistFundingNameResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentLogRepository extends JpaRepository<InvestmentLog, Long> {

    List<InvestmentLog> findAllByFunding(Funding funding);


    @Query("SELECT COUNT(DISTINCT il.member) FROM InvestmentLog il WHERE il.funding.id = :fundingId")
    Long countDistinctMemberByFundingId(@Param("fundingId") Long fundingId);

    Page<InvestmentLog> findAllByFundingIdAndMemberIdOrderByCreatedAt(Long fundingId, Long memberId, Pageable pageable);
    
    List<InvestmentLog> findAllByMemberIdAndFunding_progressStatus(Long memberId, FundingProgressStatus fundingProgressStatus);

    List<InvestmentLog> findAllByMemberIdAndFundingId(Long memberId, Long fundingId);
}
