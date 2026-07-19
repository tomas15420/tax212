import { useNavigate, useSearch } from "@tanstack/react-router"
import TransactionFilter, { type TransactionFilters } from "./TransactionFilter"
import { format } from "date-fns"
import type { TransactionSearchBlock } from "@/routes/transactions"
import { GetTransactionsSide } from "@/api/model/getTransactionsSide"
import { useGetTransactions } from "@/api/tax212"
import { TransactionsTable } from "./TransactionTable"
import { Button } from "@/components/ui/button"
import { Loader2 } from "lucide-react"
import { useCallback, useEffect, useMemo, useState } from "react"
import { ErrorCard } from "@/components/ErrorCard"
import { TransactionsLoadingSkeleton } from "./TransactionsLoadingSkeleton"

const parseLocalDate = (dateStr: string | undefined) => {
    if (!dateStr) return undefined
    const [year, month, day] = dateStr.split("-").map(Number)
    return new Date(year, month - 1, day)
}

export default function TransactionsPage() {
    const search = useSearch({ from: "/transactions" }) as TransactionSearchBlock
    const navigate = useNavigate({ from: "/transactions" })

    const currentFilters = useMemo<TransactionFilters>(() => {
        const fromDate = parseLocalDate(search.from)
        const toDate = parseLocalDate(search.to)

        return {
            name: search.name || "",
            ticker: search.ticker || "",
            isin: search.isin || "",
            side: search.side || "all",
            dateRange: fromDate ? { from: fromDate, to: toDate } : undefined
        }
    }, [search.name, search.ticker, search.isin, search.side, search.from, search.to])

    const [liveFilters, setLiveFilters] = useState<TransactionFilters>(currentFilters)
    const [accumulatedTransactions, setAccumulatedTransactions] = useState<any[]>([])
    const [currentPage, setCurrentPage] = useState(0)

    useEffect(() => {
        setLiveFilters((prevLive) => {
            const hasChanged =
                prevLive.name !== currentFilters.name ||
                prevLive.ticker !== currentFilters.ticker ||
                prevLive.isin !== currentFilters.isin ||
                prevLive.side !== currentFilters.side ||
                prevLive.dateRange?.from?.getTime() !== currentFilters.dateRange?.from?.getTime() ||
                prevLive.dateRange?.to?.getTime() !== currentFilters.dateRange?.to?.getTime()

            return hasChanged ? currentFilters : prevLive
        })
    }, [currentFilters])

    const [lastAppliedFilters, setLastAppliedFilters] = useState(liveFilters)


    useEffect(() => {
        const hasFiltersChanged =
            liveFilters.name !== lastAppliedFilters.name ||
            liveFilters.ticker !== lastAppliedFilters.ticker ||
            liveFilters.isin !== lastAppliedFilters.isin ||
            liveFilters.side !== lastAppliedFilters.side ||
            liveFilters.dateRange?.from?.getTime() !== lastAppliedFilters.dateRange?.from?.getTime() ||
            liveFilters.dateRange?.to?.getTime() !== lastAppliedFilters.dateRange?.to?.getTime()

        if (hasFiltersChanged) {
            setAccumulatedTransactions([])
            setCurrentPage(0)
            setLastAppliedFilters(liveFilters)
        }
    }, [liveFilters, lastAppliedFilters])

    const apiSide = liveFilters.side === "buy" ? GetTransactionsSide.BUY
        : liveFilters.side === "sell" ? GetTransactionsSide.SELL
            : undefined

    const formatDateForApi = (date: Date | undefined) => {
        if (!date) return undefined
        return format(date, "yyyy-MM-dd")
    }

    const { data, isLoading, isFetching, isError, refetch } = useGetTransactions({
        page: currentPage,
        size: 25,
        sort: ["filledAt.desc"],
        instrumentName: liveFilters.name || undefined,
        ticker: liveFilters.ticker || undefined,
        isin: liveFilters.isin || undefined,
        side: apiSide,
        dateFrom: formatDateForApi(liveFilters.dateRange?.from),
        dateTo: formatDateForApi(liveFilters.dateRange?.to),
    })

    const apiResponse = data?.status === 200 ? data?.data : undefined

    useEffect(() => {
        if (apiResponse?.items) {
            setAccumulatedTransactions((prev) => {
                const items = apiResponse.items ?? []
                if (currentPage === 0) return items

                const existingIds = new Set(prev.map((t) => t.id))
                const newItems = items.filter((t) => !existingIds.has(t.id))
                return [...prev, ...newItems]
            })
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
        setLiveFilters(newFilters)
    }, [])

    const handleUrlUpdate = useCallback((finalFilters: TransactionFilters) => {
        const formatDateForUrl = (date: Date | undefined) => {
            if (!date) return undefined
            return format(date, "yyyy-MM-dd")
        }

        const nextFrom = formatDateForUrl(finalFilters.dateRange?.from)
        const nextTo = formatDateForUrl(finalFilters.dateRange?.to)

        if (
            (search.name || "") === finalFilters.name &&
            (search.ticker || "") === finalFilters.ticker &&
            (search.isin || "") === finalFilters.isin &&
            (search.side || "all") === finalFilters.side &&
            search.from === nextFrom &&
            search.to === nextTo
        ) {
            return
        }

        navigate({
            search: (prev) => ({
                ...prev,
                name: finalFilters.name || undefined,
                ticker: finalFilters.ticker || undefined,
                isin: finalFilters.isin || undefined,
                side: finalFilters.side !== "all" ? finalFilters.side : undefined,
                from: nextFrom,
                to: nextTo,
            }),
            replace: true,
        })
    }, [navigate, search])

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

    if (isError) {
        return <ErrorCard onRetry={refetch} />;
    }

    return (
        <div className="container mx-auto space-y-6 p-6">
            <div>
                <h1 className="text-3xl font-bold tracking-tight">Transakce</h1>
                <p className="text-muted-foreground">Správa a přehled tvých obchodů.</p>
            </div>

            <TransactionFilter
                onFilterChange={handleFilterChange}
                onUrlUpdate={handleUrlUpdate}
                defaultValues={currentFilters}
            />

            {accumulatedTransactions.length === 0 && (isLoading || isFetching) ? (
                <TransactionsLoadingSkeleton rowsCount={10} />
            ) : (
                <>
                    <div className="min-h-[500px] w-full">
                        <TransactionsTable
                            pagedTransactions={pagedTransactionsForTable}
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
                </>
            )}
        </div>
    )
}