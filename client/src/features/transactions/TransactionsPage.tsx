"use client"

import { useNavigate, useSearch } from "@tanstack/react-router"
import TransactionFilter, { type TransactionFilters } from "./TransactionFilter"
import { parseISO } from "date-fns"
import type { TransactionSearchBlock } from "@/routes/transactions"
import { GetTransactionsSide } from "@/api/model/getTransactionsSide"
import { useGetTransactions } from "@/api/tax212"
import { TransactionsTable } from "./TransactionTable"
import { Button } from "@/components/ui/button"
import { Loader2 } from "lucide-react"
import { useCallback, useEffect, useMemo, useState } from "react"

export default function TransactionsPage() {
    const search = useSearch({ from: "/transactions" }) as TransactionSearchBlock
    const navigate = useNavigate({ from: "/transactions" })

    const [accumulatedTransactions, setAccumulatedTransactions] = useState<any[]>([])
    const [currentPage, setCurrentPage] = useState(0)

    useEffect(() => {
        setAccumulatedTransactions([])
        setCurrentPage(0)
    }, [search.name, search.ticker, search.isin, search.side, search.from, search.to])

    const currentFilters = useMemo<TransactionFilters>(() => {
        return {
            name: search.name || "",
            ticker: search.ticker || "",
            isin: search.isin || "",
            side: search.side || "all",
            dateRange: search.from ? {
                from: parseISO(search.from),
                to: search.to ? parseISO(search.to) : undefined
            } : undefined
        }
    }, [search])

    const apiSide = useMemo(() => {
        if (search.side === "buy") return GetTransactionsSide.BUY
        if (search.side === "sell") return GetTransactionsSide.SELL
        return undefined
    }, [search.side])

    const { data, isLoading, isFetching } = useGetTransactions({
        page: currentPage,
        size: 25,
        sort: ["filledAt.desc"],
        instrumentName: search.name || undefined,
        ticker: search.ticker || undefined,
        isin: search.isin || undefined,
        side: apiSide,
        dateFrom: search.from || undefined,
        dateTo: search.to || undefined,
    });

    const apiResponse = data?.status === 200 ? data?.data : undefined;

    useEffect(() => {
        if (apiResponse?.items) {
            setAccumulatedTransactions((prev) => {
                const items = apiResponse.items ?? []

                if (currentPage === 0) {
                    return items;
                }

                const existingIds = new Set(prev.map((t) => t.id));
                const newItems = items.filter((t) => !existingIds.has(t.id));

                return [...prev, ...newItems];
            });
        }
    }, [apiResponse, currentPage])

    const hasNextPage =
        apiResponse?.totalPages !== undefined &&
        apiResponse.page !== undefined &&
        apiResponse.page < apiResponse.totalPages - 1

    const handleLoadMore = () => {
        if (hasNextPage && !isFetching) {
            setCurrentPage((prev) => prev + 1)
        }
    }

    const handleFilterChange = useCallback((newFilters: TransactionFilters) => {
        const searchParams: TransactionSearchBlock = {
            name: newFilters.name || undefined,
            ticker: newFilters.ticker || undefined,
            isin: newFilters.isin || undefined,
            side: newFilters.side !== "all" ? newFilters.side : undefined,
            from: newFilters.dateRange?.from ? newFilters.dateRange.from.toISOString().split("T")[0] : undefined,
            to: newFilters.dateRange?.to ? newFilters.dateRange.to.toISOString().split("T")[0] : undefined,
        }

        navigate({
            search: () => searchParams,
            replace: true,
        })
    }, [navigate])

    const pagedTransactionsForTable = useMemo(() => {
        if (apiResponse) {
            return {
                ...apiResponse,
                items: accumulatedTransactions
            };
        }


        if (accumulatedTransactions.length > 0) {
            return {
                items: accumulatedTransactions,
                page: currentPage,
                size: 25,
                totalItems: accumulatedTransactions.length,
                totalPages: currentPage + 1
            };
        }

        return undefined;
    }, [apiResponse, accumulatedTransactions, currentPage]);

    return (
        <div className="container mx-auto space-y-6 p-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Transakce</h1>
                <p className="text-muted-foreground">Správa a přehled tvých obchodů.</p>
            </div>

            <TransactionFilter
                onFilterChange={handleFilterChange}
                defaultValues={currentFilters}
            />

            <div className="min-h-[500px] w-full">
                <TransactionsTable
                    pagedTransactions={pagedTransactionsForTable}
                    isLoading={accumulatedTransactions.length === 0 && (isLoading || isFetching)}
                />
            </div>

            {hasNextPage && (
                <div className="flex justify-center pt-4">
                    <Button
                        onClick={handleLoadMore}
                        disabled={isFetching}
                        variant="outline"
                        className="w-full max-w-xs"
                    >
                        {isFetching ? (
                            <>
                                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                Načítám...
                            </>
                        ) : (
                            "Načíst další"
                        )}
                    </Button>
                </div>
            )}
        </div>
    )
}