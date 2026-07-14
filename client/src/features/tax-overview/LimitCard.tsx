import { useMemo } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { formatCurrency } from "@/utils/utils";
import { cn } from "@/lib/utils";

interface LimitCardProps {
    title: string;
    currentValue: number;
    limitValue: number;
    percentage: number;
    description: string;
}

export const LimitCard = ({ title, currentValue, limitValue, percentage, description }: LimitCardProps) => {
    const status = useMemo(() => {
        if (percentage >= 100) {
            return {
                barColor: "bg-destructive",
                cardBg: "bg-destructive/5 dark:bg-destructive/10 border-destructive/20"
            };
        }
        if (percentage >= 80) {
            return {
                barColor: "bg-amber-500",
                cardBg: "bg-amber-500/5 dark:bg-amber-500/10 border-amber-500/20"
            };
        }
        return {
            barColor: "bg-emerald-500",
            cardBg: "bg-emerald-500/5 dark:bg-emerald-500/10 border-emerald-500/10"
        };
    }, [percentage]);

    return (
        <Card className={cn(status.cardBg)}>
            <CardHeader className="p-4 md:p-6 pb-3">
                <div className="flex items-start justify-between gap-2">
                    <span className="text-xs font-medium text-muted-foreground md:text-sm block">{title}</span>
                    <span className="text-xl font-bold text-foreground md:text-2xl shrink-0">
                        {percentage} %
                    </span>
                </div>
            </CardHeader>
            <CardContent className="p-4 md:p-6 pt-0 space-y-4">
                <div className="text-base font-semibold text-foreground md:text-lg">
                    {formatCurrency(currentValue, "CZK")}{" "}
                    <span className="text-muted-foreground font-normal text-xs md:text-sm">
                        / {formatCurrency(limitValue, "CZK")}
                    </span>
                </div>

                <div className="w-full bg-muted rounded-full h-3 overflow-hidden">
                    <div
                        className={cn("h-full rounded-full transition-all duration-500 ease-out", status.barColor)}
                        style={{ width: `${percentage}%` }}
                    />
                </div>

                <p className="text-xs text-muted-foreground leading-normal">
                    {description}
                </p>
            </CardContent>
        </Card>
    );
};