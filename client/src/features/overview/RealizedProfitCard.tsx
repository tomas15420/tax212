import { TrendingUp, TrendingDown, ArrowUpRight, ArrowDownRight } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { formatCurrency } from "@/utils/utils";

interface RealizedProfitCardProps {
    actualValue: number | string | null | undefined;
    tradingValue: number | string | null | undefined;
    currency: string;
}

const RealizedProfitCard = ({ actualValue, tradingValue, currency }: RealizedProfitCardProps) => {
    const actualNum = typeof actualValue === "string" ? parseFloat(actualValue) : (actualValue ?? 0);
    const tradingNum = typeof tradingValue === "string" ? parseFloat(tradingValue) : (tradingValue ?? 0);

    const isActualPositive = actualNum >= 0;
    const isTradingPositive = tradingNum >= 0;

    return (
        <Card className="group relative overflow-hidden transition-all duration-300 border-border/50 bg-background/50 backdrop-blur-sm hover:-translate-y-0.5 hover:border-primary/15 hover:shadow-lg hover:shadow-primary/5 sm:col-span-2">
            <div className="pointer-events-none absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 transition-opacity duration-500 group-hover:opacity-100" />

            <CardContent className="relative z-10 p-3.5 md:p-6">
                <div className="flex flex-col gap-3 md:gap-4">

                    {/* Hlavička s dynamickou ikonou */}
                    <div className="flex items-center justify-between">
                        <span className="text-xs font-medium text-muted-foreground md:text-sm">
                            Realizovaný zisk
                        </span>
                        <div className={cn(
                            "rounded-md p-1.5 md:p-2 transition-all duration-300 group-hover:scale-105",
                            isActualPositive
                                ? "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
                                : "bg-rose-500/10 text-rose-600 dark:text-rose-400"
                        )}>
                            {isActualPositive ? (
                                <TrendingUp className="h-4 w-4 transition-transform group-hover:rotate-3" />
                            ) : (
                                <TrendingDown className="h-4 w-4 transition-transform group-hover:-rotate-3" />
                            )}
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-2 md:gap-4">
                        <div className="flex flex-col justify-between gap-1 md:block">
                            <div className="flex items-center gap-1">
                                {isActualPositive ? (
                                    <ArrowUpRight className="h-3.5 w-3.5 text-emerald-500 shrink-0" />
                                ) : (
                                    <ArrowDownRight className="h-3.5 w-3.5 text-rose-500 shrink-0" />
                                )}
                                <span className="text-[10px] font-semibold text-muted-foreground md:text-xs">
                                    Skutečný
                                </span>
                            </div>
                            <strong className={cn(
                                "text-sm font-bold tracking-tight md:text-xl md:mt-1 block transition-colors duration-300",
                                isActualPositive ? "text-emerald-600 dark:text-emerald-400" : "text-rose-600 dark:text-rose-400"
                            )}>
                                {formatCurrency(actualValue, currency, true)}
                            </strong>
                        </div>

                        <div className="relative flex flex-col justify-between gap-1 pl-3 md:block md:pl-4">
                            <div className="absolute left-0 top-0 bottom-0 w-[1px] bg-border" />
                            <div className="flex items-center gap-1.5">
                                {isTradingPositive ? (
                                    <ArrowUpRight className="h-3 w-3 text-emerald-500 shrink-0" />
                                ) : (
                                    <ArrowDownRight className="h-3 w-3 text-rose-500 shrink-0" />
                                )}
                                <span className="text-[10px] font-semibold text-muted-foreground md:text-xs">
                                    Trading212
                                </span>
                            </div>
                            <strong className={cn(
                                "text-sm font-bold tracking-tight md:text-xl md:mt-1 block transition-colors duration-300",
                                isTradingPositive ? "text-emerald-600 dark:text-emerald-400" : "text-rose-600 dark:text-rose-400"
                            )}>
                                {formatCurrency(tradingValue, currency, true)}
                            </strong>
                        </div>

                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default RealizedProfitCard;