export type Nft = {
    address: string;
    tokenID: number;
};

export type NftDetails = Nft & {
    name: string;
    ownerAddress: string;
    url: string;
    description: string;
    type: 'Image' | 'Video' | 'Audio' | 'Text' | 'Other';
};
