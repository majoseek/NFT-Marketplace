import { AuctionResponse } from '@/api/types/auction';
import { rest } from 'msw';

export const handlers = [
    rest.get('/auction', (req, res, ctx) => {
        const response: AuctionResponse = {
            auctions: [],
            count: 2,
            page: 1,
            size: 1,
        };

        return res(ctx.status(200), ctx.json(response));
    }),
];
