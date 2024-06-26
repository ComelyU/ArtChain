async function main10() {
  const [owner] = await ethers.getSigners();

  const contractAddress = '0xe9FdF6B6d31652863f384cB128D8F414a8023d9c'; // 배포된 컨트랙트의 주소를 여기에 입력하세요.
  const ContractFactory = await ethers.getContractFactory('TokenMarketplace'); // 컨트랙트 이름을 여기에 입력하세요.
  const contract = ContractFactory.attach(contractAddress);

  // PCC 상장

  // 거래 게시글 생성
  // 거래할 토큰을 게시글 형태로 올림, PCC 10개를 ART 100개로 파는 글
  // await contract.addTradePost("0x9d3d6467e6171849BB716CbC350C9835C1FcF1fA", 10, 20);
  console.log(await contract.getAllPosts());
  // 토큰 거래하기
  // await contract.buyToken(0);
  // console.log(await contract.getSpecificPost(0));
}

main10()
  .then(() => process.exit(0))
  .catch((error) => {
    console.error(error);
    process.exit(1);
  });
