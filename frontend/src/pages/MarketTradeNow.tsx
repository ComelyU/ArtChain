import { Box, Flex, Text } from "@chakra-ui/react";
import { getMarketSoldDetail } from "../api/market";
import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getMarketDetailDisplayInterface } from "../type/market.interface";
import { formatNumberWithComma } from "../components/Common/Comma";
import formatDate from "../components/Common/Datetime";

export default function MarketTradeNow() {
  const id = useParams() as { id: string };
  const [data, setDatas] = useState<getMarketDetailDisplayInterface>();

  useEffect(() => {
    getMarketSoldDetail(Number(id.id)).then((res) => setDatas(res.data.data));
  }, [id]);

  if (data?.pieceCount === undefined) {
    return;
  }

  return (
    <Box p={"1.5rem"}>
      <Flex direction={"column"}>
        <Text as={"b"} fontSize={"2rem"}>
          {data?.fundingName}
        </Text>
        <Text as={"b"} fontSize={"1.3rem"} mt={"1rem"}>
          {" "}
          {data?.companyName}
        </Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          거래 ID
        </Text>
        <Text fontSize={"1rem"}>#{data?.id}</Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          거래분류
        </Text>

        <Flex mt={"0.2rem"}>
          <Box
            px={"0.6rem"}
            py={"0.1rem"}
            rounded={"0.7rem"}
            fontSize={"sm"}
            bg={"gray.400"}
          >
            <Text color={"white.100"}>등록</Text>
          </Box>
        </Flex>

        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          조각수
        </Text>
        <Text fontSize={"1rem"}>
          {" "}
          {formatNumberWithComma(data?.pieceCount)} 조각
        </Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          총 가격
        </Text>
        <Text fontSize={"1rem"}>
          {" "}
          {formatNumberWithComma(data?.totalCoin)} 아트
        </Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          조각 당 가격
        </Text>
        <Text fontSize={"1rem"}> {data?.coinPerPiece} 아트</Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          판매자 지갑
        </Text>
        <Text fontSize={"1rem"}> {data?.sellerAddress}</Text>

        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          시간
        </Text>
        <Text fontSize={"1rem"}> {formatDate(data?.createdAt)}</Text>
        <Text as={"b"} fontSize={"1.2rem"} mt={"1rem"}>
          {" "}
          상세 링크 (클릭)
        </Text>
        <Box>
          <Text
            fontSize={"1rem"}
            onClick={() =>
              window.open(
                `https://sepolia.etherscan.io/tx/${data?.transactionHash}`
              )
            }
          >
            {" "}
            {data?.contractAddress}
          </Text>
        </Box>
      </Flex>
    </Box>
  );
}
