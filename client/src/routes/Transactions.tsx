import TransactionsPage from '@/features/transactions/TransactionsPage'
import { createFileRoute } from '@tanstack/react-router'

export type TransactionSearchBlock = {
    name?: string
    ticker?: string
    isin?: string
    side?: "all" | "buy" | "sell"
    from?: string
    to?: string
}

export const Route = createFileRoute('/transactions')({
    validateSearch: (search: Record<string, unknown>): TransactionSearchBlock => {
        return {
            name: (search.name as string) || undefined,
            ticker: (search.ticker as string) || undefined,
            isin: (search.isin as string) || undefined,
            side: (search.side as "all" | "buy" | "sell") || undefined,
            from: (search.from as string) || undefined,
            to: (search.to as string) || undefined,
        }
    },
    component: RouteComponent,
})

function RouteComponent() {
    return <TransactionsPage />
}