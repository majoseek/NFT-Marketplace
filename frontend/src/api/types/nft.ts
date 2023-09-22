export type Nft = {
    contractAddress: string;
    tokenID: number;
    name: string;
    ownerAddress: string;
    url: string;
    description: string;
    type: 'Image' | 'Video' | 'Audio' | 'Text' | 'Other';
};
