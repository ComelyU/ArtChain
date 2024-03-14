require("@nomiclabs/hardhat-waffle");
import ArtcoinAbi from "./artifacts/contracts/ArtCoin.sol/ArtcoinContract.json"
// 환경변수 설정
import dotenv from "dotenv";
dotenv.config();

task("check", "Check contract amounts", async () => {
  const [deployer] = await ethers.getSigners();
  const contract = "0xB80a551604E49a912590bBd1fb79Bb1dE27A263E";
  const ArtcoinABI = ArtcoinAbi.abi
  const fundrasing = new ethers.Contract(contract, ArtcoinABI, deployer);
  // await fundrasing.distributeFunds()
  await fundrasing.mintTokens(100);
  await fundrasing.transferToken(
    "0xa98152DE411B3C2ecBccAA199A7f1F855e7c8E90",
    100
  );
  // await fundrasing.distributeFunds().then((datass : any) => console.log(datass) ).catch((err : any) => console.log(err))
  // console.log(await fundrasing.targetAmount(), await fundrasing.raisedAmount(), await fundrasing.finishTime());
});

const privateKey =
  process.env.MAIN_WALLET_PRIVATE_KEY;

module.exports = {
  defaultNetwork: "sepolia",
  networks: {
    sepolia: {
      url: process.env.SEPOLIA_URL,
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
