import axios from 'axios';
import { useEffect, useState } from 'react';
import CreateAuctionModal from './CreateAuctionModal';
import * as Styled from './Nfts.styles';
import { Button, Divider } from 'antd';
import { Nft } from '@/api/types/nft';
import { useAppSelector } from '@/hooks/useAppSelector';

const Nfts = () => {
    const [nfts, setNfts] = useState<Nft[]>([]);
    const [isCreateAuctionModalVisible, setIsCreateAuctionModalVisible] =
        useState(false);
    const [selectedNftId, setSelectedNftId] = useState<number | null>(null);
    const [selectedNftAddress, setSelectedNftAddress] = useState<string | null>(
        null
    );
    const wallets = useAppSelector((state) => state.app.wallets);

    useEffect(() => {
        if (!wallets || wallets.length < 1) return;

        const userAccountAddress = wallets[0];
        axios
            .get(`/nft/owner/${userAccountAddress}`)
            .then((res: { data: Nft[] }) => {
                setNfts(res.data);
            });
    }, []);

    const handleSelectNft = (nftId: number, nftAddress: string) => {
        setIsCreateAuctionModalVisible(true);
        setSelectedNftId(nftId);
        setSelectedNftAddress(nftAddress);
    };

    return (
        <Styled.NftsContainer>
            <h2>Owned NFTs</h2>
            {/* {nfts.map((nft) => (
                <div key={nft.tokenID}>
                    <Divider />
                    <Styled.NftWrapper>
                        <span>Title: {nft.name}</span>
                        <span>Description: {nft.description}</span>
                        {nft.type === 'Image' && (
                            <Styled.NftImage src={nft.url} alt="nft" />
                        )}
                        <Button
                            onClick={() =>
                                handleSelectNft(
                                    nft.tokenID,
                                    nft.contractAddress
                                )
                            }
                        >
                            Create auction
                        </Button>
                    </Styled.NftWrapper>
                </div>
            ))} */}
            {selectedNftId !== null && selectedNftAddress !== null && (
                <CreateAuctionModal
                    nftId={selectedNftId}
                    nftAddress={selectedNftAddress}
                    visible={isCreateAuctionModalVisible}
                    setVisible={setIsCreateAuctionModalVisible}
                />
            )}
        </Styled.NftsContainer>
    );
};

export default Nfts;
