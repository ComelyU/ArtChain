import { makeQuerystring } from "../utils/ApiUtils";
import { localAxios } from "./https";
import {
  MainPageMarketTop4ResponseInterface,
  getMarketHistoryDisplayListAxiosInterface,
  getMarketMainDisplayListParams,
  getMarketSellingDisplayListAxiosInterface,
  postMarketEnrollInterface,
} from "../type/market.interface";

async function getMarketMainDisplayList(
  params: getMarketMainDisplayListParams
) {
  const { category, status, page, size } = params;
  const url = `/market${makeQuerystring({
    category,
    status,
    page,
    size,
  })}`;
  return await localAxios.get(url);
}

async function getMarketSellingDisplayList(
  params: getMarketSellingDisplayListAxiosInterface
) {
  const { fundingId, sortFlag, page, size } = params;
  const url = `/market/sellList${makeQuerystring({
    fundingId,
    sortFlag,
    page,
    size,
  })}`;
  return await localAxios.get(url);
}

async function getMarketHistoryDisplayList(
  params: getMarketHistoryDisplayListAxiosInterface
) {
  const { fundingId, page, size } = params;
  const url = `/market/pieceHistory${makeQuerystring({
    fundingId,
    page,
    size,
  })}`;
  return await localAxios.get(url);
}

async function postMarketEnroll(data: postMarketEnrollInterface) {
  const url = "/market";
  return await localAxios.post(url, data);
}

async function getMarketSoldDetail(marketId: number) {
  const url = `/market/detail${makeQuerystring({
    marketId,
  })}`;
  return await localAxios.get(url);
}

async function getMarketChart(fundingId: number) {
  const url = `/market/graph${makeQuerystring({
    fundingId,
  })}`;
  return await localAxios.get(url);
}

async function getMarketMyToken() {
  return await localAxios.get("/market/registForm");
}

async function putMarketToken(marketId: number, transactionHash: string) {
  return await localAxios.put(`/market/buy/${marketId}/${transactionHash}`);
}

const getMainPageMarketTop4 = async (): Promise<
  MainPageMarketTop4ResponseInterface[]
> => {
  const res = await localAxios.get("/market/mainPageMarket");
  return res.data.data;
};

export {
  getMarketMainDisplayList,
  getMarketSellingDisplayList,
  getMarketHistoryDisplayList,
  postMarketEnroll,
  getMarketSoldDetail,
  getMarketChart,
  getMarketMyToken,
  putMarketToken,
  getMainPageMarketTop4,
};
