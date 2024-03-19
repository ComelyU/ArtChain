import { Box, Flex } from "@chakra-ui/layout";
import { TopNavBar } from "../components/Common/Navigation/TopNavBar";
import { useState } from "react";

export default function Martet() {

    const isSelected = useState(false)

  return (
    <>
      <TopNavBar navType="logo" />
      <Flex maxW={"360px"} justifyContent={"center"} mt={"0.5rem"}>
        {isSelected ? <Box borderBottom={"2px"} color={"blue.300"} mr={"0.7rem"}>전체</Box> : <Box mr={"0.7rem"} color={"gray.400"}>전체</Box> }
        <Box mr={"0.7rem"} color={"gray.400"}>공연</Box>
        <Box mr={"0.7rem"} color={"gray.400"}>전시</Box>
        <Box color={"gray.400"}>영화</Box>
      </Flex>
      <Flex>
        <Box rounded='md' boxShadow='2xl'>ㅁㄴㅇㄹ</Box>
      </Flex>
    </>
  );
}
