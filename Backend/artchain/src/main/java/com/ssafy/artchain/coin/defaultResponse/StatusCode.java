package com.ssafy.artchain.coin.defaultResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {

	// 200 OK : 성공
	SUCCESS_COIN_MAIN_VIEW(200, "메인 화면 정보 조회 성공"),
	SUCCESS_COIN_HISTORY_LIST_VIEW(200, "충전, 환전 내역 리스트 조회 성공"),
	SUCCESS_NEW_COMPANY_USER(200, "회사 유저 등록 성공"),
	SUCCESS_USER_VIEW(200, "일반 유저 조회 성공"),
	SUCCESS_COMPANY_VIEW(200, "회사 유저 조회 성공"),
	SUCCESS_USER_MAIN_VIEW(200, "메인 화면 유저 정보 조회 성공"),
	// 201 CREATED : 새로운 리소스 생성
	SUCCESS_CREATE_COIN_CHARGE(201, "코인 충전 내역 저장 성공"),
	SUCCESS_CREATE_COIN_EXCHANGE(201, "코인 환전 내역 저장 성공"),

	// 204 NO CONTENT : 성공하였으나, 반환할 값이 없음
//	NO_CONTENT_IN_LIBRARY_VIEW(204, "서재에 도서가 없음"),

	// 400 BAD REQUEST : 잘못된 요청 - 요청 구문이 잘못되었음
	FAIL_CREATE_COIN_HISTORY(400, "코인 내역 저장 실패!");

	// 404 NOT FOUND

	private final int status;
	private final String message;
}