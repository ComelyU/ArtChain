package com.ssafy.artchain.funding.controller;

import com.ssafy.artchain.funding.dto.FundingCreateRequestDto;
import com.ssafy.artchain.funding.response.StatusCode;
import com.ssafy.artchain.funding.service.FundingService;
import com.ssafy.artchain.funding.response.DefaultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/funding")
@RequiredArgsConstructor
@Slf4j
public class FundingController {

    private final FundingService fundingService;

    @PostMapping
    public ResponseEntity<DefaultResponse<Void>> createFunding(@RequestBody FundingCreateRequestDto dto){
        int result = fundingService.createFunding(dto);
        if(result == -1){
            return DefaultResponse.emptyResponse(HttpStatus.OK, StatusCode.FAIL_CREATE_FUNDING);
        } else {
            return DefaultResponse.emptyResponse(HttpStatus.OK, StatusCode.SUCCESS_CREATE_FUNDING);
        }
    }

}
