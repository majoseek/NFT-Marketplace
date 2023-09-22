import { AuctionResponse } from '@/api/types/auction';
import moment from 'moment';
import { rest } from 'msw';

export const auctionHandlers = [
    rest.get('/auction', (req, res, ctx) => {
        const auctionExpiryTime = moment()
            .add(5, 'minutes')
            .format('YYYY-MM-DD HH:mm:ss');

        const response: AuctionResponse = {
            auctions: [
                {
                    auctionID: 1,
                    title: 'Some title',
                    description:
                        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',
                    nft: {
                        address: '0x57129ffasfy872f1y',
                        tokenID: 5,
                    },
                    startingPrice: 0,
                    reservePrice: 0,
                    minimumIncrement: 5,
                    expiryTime: auctionExpiryTime,
                    status: 'active',
                },
                {
                    auctionID: 2,
                    title: 'Other longer title',
                    description:
                        'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
                    nft: {
                        address: '0x57g42129ffasfy872f1y',
                        tokenID: 3,
                    },
                    startingPrice: 5,
                    reservePrice: 2,
                    minimumIncrement: 10,
                    expiryTime: auctionExpiryTime,
                    status: 'canceled',
                },
            ],
            count: 2,
            page: 1,
            size: 2,
        };

        return res(ctx.status(200), ctx.json(response));
    }),
];
