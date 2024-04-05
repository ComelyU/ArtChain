import { makeQuerystring } from "../utils/ApiUtils";
import { localAxios } from "./https";
import {
  GetMyPieceDropDown,
  GetMyPieceListResponse,
  GetMyPieceListParams,
  GetMyInvestmentHistoryResponse,
  GetMyInvestmentHistoryParams,
  GetMyPieceCountResponse,
} from "../type/mypage.interface";

// [개인] 나의 조각 드롭다운 조회
export const getMyPieceDropDown = async (): Promise<GetMyPieceDropDown[]> => {
  const url = `/member/myTrade/dropDown`;
  const response = await localAxios.get(url);
  return response.data.data;
};

// [개인] 나의 조각 리스트 조회
export const getMyPieceList = async (
  params: GetMyPieceListParams
): Promise<GetMyPieceListResponse[]> => {
  const { fundingId, filterFlag, page, size } = params;
  const url = `/member/myTrade${makeQuerystring({
    fundingId,
    filterFlag,
    page,
    size,
  })}`;
  const response = await localAxios.get(url);
  return response.data.data;
};

// 나의 투자(직접 투자 + 거래) 내역
export const getMyInvestmentHistory = async (
  params: GetMyInvestmentHistoryParams
): Promise<GetMyInvestmentHistoryResponse> => {
  const { status } = params;
  const url = `/funding/my-list/${status}`;

  const response = await localAxios.get(url);
  return response.data.data;
};

// 나의 조각 보유량 TOP3 투자 확인
export const getMyPieceCount = async (): Promise<GetMyPieceCountResponse> => {
  const url = `/pieceowner/podium`;

  const response = await localAxios.get(url);
  return response.data.data;
};
