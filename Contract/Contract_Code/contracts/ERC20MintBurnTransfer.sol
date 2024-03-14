// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

// 조각 코인 민팅
contract ERC20MintBurnTransferContract is ERC20 {

    // 현재 코인 소유자 목록을 관리합니다. (true=소유자)
    mapping (address => bool) private _owners;
    // 실제 소유자 주소들의 배열입니다.
    address[] private _ownerList;
    
    event Mint(address indexed _to, uint _amount);

    // constructor로 묶은 이유는 총 발행량은 정해져 있기 때문
    // 한 번만 민트
    constructor(
        string memory coinName, // 코인 이름 (ex. "ArtCoin")
        string memory ticker, // 토큰 이름 줄여서 (ex. "ART")
        uint256 initialSupply // 총 발행량
    ) ERC20(coinName, ticker) {
        _mint(msg.sender, initialSupply * 10 ** 18);
        // 생성자 주소를 소유자 목록에 추가합니다.
        _updateOwner(msg.sender, balanceOf(msg.sender));
        emit Mint(msg.sender, initialSupply);
    }

    // 나중에 컨트랙트 상에서 전송해야 하므로 이더 받는 함수
    receive() external payable {}
    
    // 토큰을 소각하는 함수입니다.
    function burnTokens(uint256 burnAmount) public {
        _burn(msg.sender, burnAmount * 10 ** 18);
        // 소각 후 소유자 상태를 업데이트합니다.
        _updateOwner(msg.sender, balanceOf(msg.sender) - burnAmount * 10 ** 18);
    }

    function transferToken(address to, uint256 amount) public {
        transfer(to, amount * 10 ** 18);
        // 전송 후, 송신자와 수신자의 소유자 상태를 업데이트합니다.
        _updateOwner(msg.sender, balanceOf(msg.sender) - amount * 10 ** 18);
        _updateOwner(to, balanceOf(to));
        emit Transfer(msg.sender, to, amount);
    }

    function _updateOwner(address user, uint256 newBalance) private {
        if(newBalance > 0 && !_owners[user]) {
            // 잔액이 있고, 기존에 목록에 없던 사용자라면 추가합니다.
            _owners[user] = true;
            _ownerList.push(user);
        } else if(newBalance == 0 && _owners[user]) {
            // 잔액이 없고, 목록에 있던 사용자라면 제거합니다.
            _owners[user] = false;
            for(uint256 i = 0; i < _ownerList.length; i++) {
                if(_ownerList[i] == user) {
                    _ownerList[i] = _ownerList[_ownerList.length - 1];
                    _ownerList.pop();
                    break;
                }
            }
        }
    }

    // 현재 모든 토큰 소유자와 그들이 가진 토큰 수를 반환하는 함수
    function getOwnersAndBalances() public view returns (address[] memory, uint256[] memory) {
        uint256[] memory balances = new uint256[](_ownerList.length);
        for(uint256 i = 0; i < _ownerList.length; i++) {
            balances[i] = balanceOf(_ownerList[i]);
        }
        return (_ownerList, balances);
    }    
}
