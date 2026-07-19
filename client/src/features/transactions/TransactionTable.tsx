import { ArrowUpRight, ArrowDownLeft, Loader2 } from "lucide-react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { formatCurrency, formatDateTime } from "@/utils/utils";
import type { PagedResponseTransactionDto } from "@/api/model/pagedResponseTransactionDto";

interface TransactionsTableProps {
    pagedTransactions: PagedResponseTransactionDto | undefined;
}

export const TransactionsTable = ({ pagedTransactions }: TransactionsTableProps) => {
    const transactions = pagedTransactions?.items;
    const currentLength = transactions?.length ?? 0;

    if (!transactions || currentLength === 0) {
        return (
            <div className="flex h-32 w-full items-center justify-center rounded-xl border border-dashed bg-card text-sm text-muted-foreground">
                Ždáné transakce nebyly nalezeny.
            </div>
        );
    }

    return (
        <div className="overflow-x-auto">
            <Table>
                <TableHeader>
                    <TableRow>
                        <TableHead className="w-[160px]">Datum a čas</TableHead>
                        <TableHead className="w-[100px]">Směr</TableHead>
                        <TableHead>Instrument</TableHead>
                        <TableHead className="text-right">Množství</TableHead>
                        <TableHead className="text-right">Cena / ks</TableHead>
                        <TableHead className="text-right">Dopad na účet</TableHead>
                        <TableHead className="text-right">PnL</TableHead>
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {transactions.map((tx) => {
                        const rowKey = tx.id ?? tx.t212id ?? Math.random();
                        const isSell = tx.side === "SELL";
                        const { date, time } = formatDateTime(tx.filledAt);
                        const currency = tx.instrument?.currency;

                        return (
                            <TableRow key={rowKey} className="transition-colors hover:bg-muted/30">
                                <TableCell className="font-medium">
                                    <div className="flex flex-col sm:flex-row sm:items-center sm:gap-2">
                                        <span className="text-foreground whitespace-nowrap">{date}</span>
                                        <span className="text-xs text-muted-foreground">{time}</span>
                                    </div>
                                </TableCell>

                                <TableCell>
                                    {isSell ? (
                                        <Badge variant="secondary" className="bg-rose-500/10 text-rose-500 dark:bg-rose-500/20 hover:bg-rose-500/10 border-none gap-1 py-0.5 px-2 font-semibold">
                                            <ArrowUpRight className="h-3 w-3 shrink-0" />
                                            SELL
                                        </Badge>
                                    ) : (
                                        <Badge variant="secondary" className="bg-emerald-500/10 text-emerald-500 dark:bg-emerald-500/20 hover:bg-emerald-500/10 border-none gap-1 py-0.5 px-2 font-semibold">
                                            <ArrowDownLeft className="h-3 w-3 shrink-0" />
                                            BUY
                                        </Badge>
                                    )}
                                </TableCell>

                                <TableCell>
                                    <div className="flex flex-col min-w-0">
                                        <span className="font-medium text-foreground truncate max-w-[180px] sm:max-w-none">
                                            {tx.instrument?.name ?? "Neznámý instrument"}
                                        </span>
                                        <span className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                            {tx.instrument?.marketTicker ?? "—"}
                                        </span>
                                    </div>
                                </TableCell>

                                <TableCell className="text-right font-mono font-medium">
                                    {tx.quantity} <span className="text-xs text-muted-foreground font-sans">ks</span>
                                </TableCell>

                                <TableCell className="text-right font-mono font-medium">
                                    {formatCurrency(tx.price, currency)}
                                </TableCell>

                                <TableCell className="text-right font-mono font-medium">
                                    {tx.walletImpact !== undefined && tx.walletImpact !== null ? (
                                        <span className={tx.side === "BUY" ? "text-muted-foreground" : "text-emerald-500 dark:text-emerald-400"}>
                                            {tx.side === "BUY" ? "-" : "+"}
                                            {formatCurrency(Math.abs(tx.walletImpact), "CZK")}
                                        </span>
                                    ) : (
                                        <span className="text-muted-foreground font-sans font-normal">—</span>
                                    )}
                                </TableCell>

                                <TableCell className="text-right font-mono font-semibold">
                                    {isSell && tx.actualPnl !== undefined && tx.actualPnl !== null ? (
                                        <div className="flex flex-col items-end">
                                            <span className={tx.actualPnl >= 0 ? "text-emerald-500" : "text-rose-500"}>
                                                {tx.actualPnl >= 0 ? "+" : ""}
                                                {formatCurrency(tx.actualPnl, "CZK")}
                                            </span>

                                            {tx.tradingPnl !== undefined && tx.tradingPnl !== null && (
                                                <span className="text-[10px] text-muted-foreground font-normal">
                                                    {tx.tradingPnl >= 0 ? "+" : ""}
                                                    {formatCurrency(tx.tradingPnl, "CZK")}
                                                </span>
                                            )}
                                        </div>
                                    ) : (
                                        <span className="text-muted-foreground font-sans font-normal">—</span>
                                    )}
                                </TableCell>
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>
        </div>
    );
};