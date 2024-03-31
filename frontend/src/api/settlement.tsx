import {} from "../type/settlement.interface";
import { makeQuerystring } from "../utils/ApiUtils";
import { localAxios } from "./https";

//투자 리스트 보기
//   export const getFunddingList = async (
//     params: GetFunddingListParams
//   ): Promise<GetFunddingListResponse | string> => {
//     const { category, status, allowStat, page, size } = params;
//     const url = `/funding/list${makeQuerystring({
//       category,
//       status,
//       allowStat,
//       page,
//       size,
//     })}`;
//     console.log(url);
//     const response = await localAxios.get(url);

//     if (response.data.data && response.data.data.fundingList) {
//       return response.data.data;
//     } else {
//       return response.data.message;
//     }
//   };

//투자 공지사항 상세보기
//   export const getNotice = async (params: GetNoticeParams): Promise<Notice> => {
//     const { fundingId, fundingNoticeId } = params;
//     const url = `/funding/${fundingId}/notice/${fundingNoticeId}`;

//     const response = await localAxios.get(url);
//     return response.data.data;
//   };

//   //투자하기 (개인)
//   export const PostInvest = async (
//     params: PostFundingParams
//   ): Promise<PostFundingResponse> => {
//     const { fundingId, fundingRequest } = params;
//     const url = `/funding/${fundingId}/invest`;

//     const response = await localAxios.post(url, fundingRequest);

//     return response.data.data.id;
//   };
