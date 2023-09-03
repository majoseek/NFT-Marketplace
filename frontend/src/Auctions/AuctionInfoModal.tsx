import { useEffect, useState } from 'react';
import { AuctionDetails } from '../types/auction';
import axios from 'axios';
import { Input, Modal, Spin } from 'antd';
import * as Styled from './AuctionInfoModal.styles';
import { abi, address } from '../config';

type Props = {
    auctionId: number;
    visible: boolean;
    setVisible: (visible: boolean) => void;
};

const AuctionInfoModal = ({ auctionId, visible, setVisible }: Props) => {
    const [auctionInfo, setAuctionInfo] = useState<AuctionDetails>();
    const [bidAmount, setBidAmount] = useState<number>();
    const [isBidding, setIsBidding] = useState<boolean>(false);

    useEffect(() => {
        axios.get(`/auction/${auctionId}`).then((res: any) => {
            setAuctionInfo(res.data);
        });
    }, []);

    const handleCancelModal = () => {
        setVisible(false);
    };

    const handlePlaceBid = async () => {
        if (!bidAmount) return;

        setIsBidding(true);
        const bidAmountInWei = window.web3.utils.toWei(
            bidAmount.toString(),
            'ether'
        );
        const accounts = await window.web3.eth.getAccounts();
        const userAccountAddress = accounts[0];
        const auctionContract = new window.web3.eth.Contract(abi, address);
        (auctionContract.methods.placeBid as any)(auctionId, bidAmountInWei)
            .send({
                from: userAccountAddress,
                gas: 3000000,
                value: bidAmountInWei,
            })
            .on('transactionHash', (hash: string) => {
                console.log('transactionHash', hash);
            })
            .on('receipt', (receipt: string) => {
                console.log('receipt', receipt);
            })
            .on('confirmation', (confirmationNumber: string) => {
                console.log('confirmation', confirmationNumber);
                setIsBidding(false);
            })
            .on('error', console.error);
    };

    const handleChangeBid = (e: React.ChangeEvent<HTMLInputElement>) => {
        setBidAmount(Number(e.target.value));
    };

    return (
        <Modal
            open={visible}
            onCancel={handleCancelModal}
            onOk={handlePlaceBid}
            okButtonProps={{
                disabled: bidAmount === undefined || bidAmount === 0,
            }}
        >
            <Styled.AuctionInfoWrapper>
                {auctionInfo ? (
                    isBidding ? (
                        <>
                            <h2>Bidding auction</h2>
                            <Spin />
                        </>
                    ) : (
                        <>
                            <h2>{auctionInfo.nft.name}</h2>
                            <span>{auctionInfo.nft.description}</span>
                            {auctionInfo.nft.type === 'Image' && (
                                <Styled.NftImage
                                    src={auctionInfo.nft.url}
                                    alt={auctionInfo.nft.name}
                                />
                            )}
                            <span>
                                Current bid:
                                {Math.max(
                                    ...auctionInfo.bids.map((bid) => bid.amount)
                                )}
                            </span>
                            <span>
                                Expiration time: {auctionInfo.expiryTime}
                            </span>
                            <Input
                                placeholder="Enter your bid"
                                onChange={handleChangeBid}
                            />
                        </>
                    )
                ) : (
                    <Spin />
                )}
            </Styled.AuctionInfoWrapper>
        </Modal>
    );
};

export default AuctionInfoModal;
