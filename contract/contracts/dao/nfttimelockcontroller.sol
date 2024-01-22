// SPDX-License-Identifier: MIT
pragma solidity ^0.8.22;

import "@openzeppelin/contracts-upgradeable/governance/TimelockControllerUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/proxy/utils/UUPSUpgradeable.sol";

contract NFTAuctionTimelockController is TimelockControllerUpgradeable, UUPSUpgradeable {

    function initialize(
        uint256 minDelay,
        address admin,
        address[] memory proposers,
        address[] memory executors
    ) public initializer {
        __TimelockController_init(minDelay, proposers, executors, admin);
    }

    function _authorizeUpgrade(address newImplementation)
        internal
        onlyRoleOrOpenRole(DEFAULT_ADMIN_ROLE)
        override
    {}
}
