require("@nomiclabs/hardhat-waffle");
// import ArtCoinAbi from "./artifacts/contracts/ArtCoin.sol/ArtcoinContract.json"
import IronHoCoinAbi from "./artifacts/contracts/ERC20MintBurnTransfer.sol/ERC20MintBurnTransferContract.json"
import '@nomiclabs/hardhat-ethers';
import '@nomiclabs/hardhat-waffle';
// 환경변수 설정
// import 'dotenv/config'
import dotenv from "dotenv";
dotenv.config();

task("check", "Check contract amounts", async () => {
  // 실행시키는놈
  const [deployer] = await ethers.getSigners();
  // const contract = "0xB80a551604E49a912590bBd1fb79Bb1dE27A263E";
  // 컨트랙트 주소
  const contract = "0x6c7E51FB4b2fe16bA640d6860AA05C11C17DFe66";
  // const ArtcoinABI = ArtCoinAbi.abi
  // ABI값
  const IronHoCoinABI = IronHoCoinAbi.abi
  // 컨트랙트 객체 불러오기
  // const fundrasing = new ethers.Contract(contract, ArtcoinABI, deployer);
  const givePieceCoin = new ethers.Contract(contract, IronHoCoinABI, deployer);
  // await fundrasing.distributeFunds()
  // await givePieceCoin.mintTokens(100);
  await givePieceCoin.transferToken(
    "0xa98152DE411B3C2ecBccAA199A7f1F855e7c8E90",
    10
  );
  console.log(await givePieceCoin.getOwnersAndBalances());
  // await fundrasing.distributeFunds().then((datass : any) => console.log(datass) ).catch((err : any) => console.log(err))
  // console.log(await fundrasing.targetAmount(), await fundrasing.raisedAmount(), await fundrasing.finishTime());
});

const privateKey =
  // process.env.MAIN_WALLET_PRIVATE_KEY as string;
  "b4c1f0efd30a4bac98df9a4d9ab1d6545f528c5d55e49d9ef751daf7a5e8980a";

module.exports = {
  defaultNetwork: "sepolia",
  networks: {
    sepolia: {
      // url: process.env.SEPOLIA_URL as string,
      url: "https://sepolia.infura.io/v3/fcf1b848b122474ba6b758aec7c7f725",
      accounts: [privateKey],
    },
  },
  solidity: {
    version: "0.8.20",
    settings: {
      optimizer: {
        enabled: true,
        runs: 200,
      },
    },
  },
};

const config = {
  solidity: "0.8.20",
  defaultNetwork: "hardhat",
  networks: {
    hardhat: {
      chainId: 1337,
      allowUnlimitedContractSize: true // 로컬 테스트 및 개발을 위한 설정
    },
    localhost: {
      url: "http://127.0.0.1:8545",
      allowUnlimitedContractSize: true // 로컬 네트워크 테스트를 위한 설정
    }
  }
};

export default config;
