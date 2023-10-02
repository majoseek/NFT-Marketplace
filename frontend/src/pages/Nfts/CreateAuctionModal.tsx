import { useEffect, useState } from 'react';
import axios from 'axios';
import { Input, Modal, Spin } from 'antd';
import * as Styled from './CreateAuctionModal.styles';
import { Nft } from '@/api/types/nft';
import { abi, address, nftAbi } from '../../config';

type Props = {
    nftId: number;
    nftAddress: string;
    visible: boolean;
    setVisible: (visible: boolean) => void;
};

const CreateAuctionModal = ({
    nftId,
    nftAddress,
    visible,
    setVisible,
}: Props) => {
    const [nftDetails, setNftDetails] = useState<Nft | null>(null);
    const [startingPrice, setStartingPrice] = useState<number>();
    const [minimumIncrement, setMinimumIncrement] = useState<number>();
    const [auctionTitle, setAuctionTitle] = useState<string>();
    const [auctionDescription, setAuctionDescription] = useState<string>();
    const [duration, setDuration] = useState<number>();

    const [auctionCreateState, setAuctionCreateState] = useState<
        'START' | 'APPROVE' | 'CREATE'
    >('START');

    useEffect(() => {
        axios.get(`/nft/contract/${nftAddress}/token/${nftId}`).then((res) => {
            setNftDetails(res.data);
        });
    }, []);

    const handleCancelModal = () => {
        setVisible(false);
    };

    const handleChangeStartingPrice = (
        e: React.ChangeEvent<HTMLInputElement>
    ) => {
        setStartingPrice(Number(e.target.value));
    };

    const handleChangeMinimumIncrement = (
        e: React.ChangeEvent<HTMLInputElement>
    ) => {
        setMinimumIncrement(Number(e.target.value));
    };

    const handleChangeDuration = (e: React.ChangeEvent<HTMLInputElement>) => {
        setDuration(Number(e.target.value));
    };

    const handleChangeAuctionTitle = (
        e: React.ChangeEvent<HTMLInputElement>
    ) => {
        setAuctionTitle(e.target.value);
    };

    const handleChangeAuctionDescription = (
        e: React.ChangeEvent<HTMLInputElement>
    ) => {
        setAuctionDescription(e.target.value);
    };

    const handleCreateAuction = async () => {
        if (!duration || !startingPrice || !minimumIncrement) return;

        setAuctionCreateState('APPROVE');

        const startingPriceInWei = window.web3.utils.toWei(
            startingPrice.toString(),
            'ether'
        );

        const minimumIncrementInWei = window.web3.utils.toWei(
            minimumIncrement.toString(),
            'ether'
        );

        const accounts = await window.web3.eth.getAccounts();
        const userAccountAddress = accounts[0];
        const auctionContract = new window.web3.eth.Contract(abi, address);
        const nftContract = new window.web3.eth.Contract(nftAbi, nftAddress);

        (nftContract.methods.setApprovalForAll as any)(address, true)
            .send({ from: userAccountAddress, gas: 3000000 })
            .on('transactionHash', (hash: string) => {
                console.log('transactionHash approval', hash);
            })
            .on('receipt', (receipt: string) => {
                console.log('receipt approval', receipt);
            })
            .on('confirmation', (confirmationNumber: string) => {
                console.log('confirmation approval', confirmationNumber);
                setAuctionCreateState('CREATE');
                (auctionContract.methods.createAuction as any)(
                    auctionTitle,
                    auctionDescription,
                    nftAddress,
                    nftId,
                    startingPriceInWei,
                    startingPriceInWei,
                    minimumIncrementInWei,
                    duration,
                    0
                )
                    .send({ from: userAccountAddress, gas: 3000000 })
                    .on('transactionHash', (hash: string) => {
                        console.log('transactionHash', hash);
                    })
                    .on('receipt', (receipt: string) => {
                        console.log('receipt', receipt);
                    })
                    .on('confirmation', (confirmationNumber: string) => {
                        console.log('confirmation', confirmationNumber);
                        setAuctionDescription(undefined);
                        setAuctionTitle(undefined);
                        setDuration(undefined);
                        setMinimumIncrement(undefined);
                        setStartingPrice(undefined);
                        setAuctionCreateState('START');
                        setVisible(false);
                    })
                    .on('error', console.error);
            })
            .on('error approval', console.error);
    };

    return (
        <Modal
            open={visible}
            onCancel={handleCancelModal}
            onOk={handleCreateAuction}
        >
            {nftDetails ? (
                <Styled.CreateAuctionWrapper>
                    {/* {auctionCreateState === 'APPROVE' ? (
                        <>
                            <h2>Approving auction...</h2>
                            <Spin />
                        </>
                    ) : auctionCreateState === 'CREATE' ? (
                        <>
                            <h2>Creating auction...</h2>
                            <Spin />
                        </>
                    ) : (
                        <>
                            <h2>{nftDetails.name}</h2>
                            <span>{nftDetails.description}</span>
                            {nftDetails.type === 'Image' && (
                                <Styled.NftImage
                                    src={nftDetails.url}
                                    alt="nft"
                                />
                            )}
                            <Input
                                placeholder="Enter auction title"
                                onChange={handleChangeAuctionTitle}
                            />
                            <Input
                                placeholder="Enter auction description"
                                onChange={handleChangeAuctionDescription}
                            />
                            <Input
                                placeholder="Enter starting price"
                                onChange={handleChangeStartingPrice}
                            />
                            <Input
                                placeholder="Enter minimum increment"
                                onChange={handleChangeMinimumIncrement}
                            />
                            <Input
                                placeholder="Enter auction duration in seconds"
                                onChange={handleChangeDuration}
                            />
                        </>
                    )} */}
                </Styled.CreateAuctionWrapper>
            ) : (
                <Spin />
            )}
        </Modal>
    );
};

export default CreateAuctionModal;
