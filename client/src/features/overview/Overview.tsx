import { Wallet, Layers, Coins, ArrowUpRight, ArrowDownRight, AlertTriangle } from "lucide-react";
import KpiCard from "./KpiCard";
import RealizedProfitCard from "./RealizedProfitCard";
import { useGetSummary } from "@/api/tax212";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent } from "@/components/ui/card";
import { formatCurrency } from "@/utils/utils";

const Overview = () => {
  const { data, isLoading, isError } = useGetSummary();

  if (isLoading) {
    return (
      <section className="space-y-4 md:space-y-6">
        <div className="flex items-center justify-between">
          <Skeleton className="h-7 w-40 md:h-8" />
        </div>
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4 md:gap-4">
          {[...Array(4)].map((_, i) => (
            <Card key={i} className="border-border/50 bg-background/50">
              <CardContent className="p-3.5 md:p-6 space-y-3">
                <div className="flex items-center justify-between">
                  <Skeleton className="h-4 w-24" />
                  <Skeleton className="h-8 w-8 rounded-md" />
                </div>
                <Skeleton className="h-7 w-28 md:h-9" />
              </CardContent>
            </Card>
          ))}
          <Card className="sm:col-span-2 border-border/50 bg-background/50">
            <CardContent className="p-3.5 md:p-6 space-y-4">
              <div className="flex items-center justify-between">
                <Skeleton className="h-4 w-28" />
                <Skeleton className="h-8 w-8 rounded-md" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Skeleton className="h-3.5 w-20" />
                  <Skeleton className="h-6 w-24" />
                </div>
                <div className="space-y-2 pl-3 border-l">
                  <Skeleton className="h-3.5 w-16" />
                  <Skeleton className="h-6 w-24" />
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>
    );
  }

  if (isError) {
    return (
      <Card className="border-destructive/20 bg-destructive/5 dark:bg-destructive/10">
        <CardContent className="flex flex-col items-center justify-center p-6 text-center md:p-10">
          <div className="rounded-full bg-destructive/10 p-3 text-destructive">
            <AlertTriangle className="h-6 w-6" />
          </div>
          <h3 className="mt-4 text-lg font-semibold text-foreground">
            Chyba při načítání dat
          </h3>
          <p className="mt-2 text-sm text-muted-foreground max-w-xs">
            Nepodařilo se připojit k API. Zkontrolujte prosím připojení k serveru a zkuste to znovu.
          </p>
        </CardContent>
      </Card>
    );
  }

  const summary = data?.status === 200 ? data?.data : null;
  const currency = summary?.currency || "CZK";

  const unrealizedVal = summary?.unrealizedProfitLoss ? summary.unrealizedProfitLoss : 0;
  const isUnrealizedPositive = unrealizedVal >= 0;

  return (
    <section className="space-y-4 md:space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-bold tracking-tight text-foreground md:text-2xl">
          Celkový přehled
        </h2>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4 md:gap-4">
        <KpiCard
          title="Celková hodnota"
          value={formatCurrency(summary?.totalValue, currency)}
          icon={Wallet}
        />
        <KpiCard
          title="Hodnota investic"
          value={formatCurrency(summary?.currentValue, currency)}
          icon={Layers}
        />
        <KpiCard
          title="Náklady"
          value={formatCurrency(summary?.totalCost, currency)}
          icon={Coins}
        />
        <KpiCard
          title="Nerealizovaný zisk"
          value={formatCurrency(summary?.unrealizedProfitLoss, currency, true)}
          icon={isUnrealizedPositive ? ArrowUpRight : ArrowDownRight}
          className={isUnrealizedPositive
            ? "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400"
            : "bg-rose-500/10 text-rose-600 dark:text-rose-400"
          }
          valueClassName={isUnrealizedPositive
            ? "text-emerald-600 dark:text-emerald-400"
            : "text-rose-600 dark:text-rose-400"
          }
        />
        <RealizedProfitCard
          actualValue={summary?.actualRealizedProfitLoss}
          tradingValue={summary?.realizedProfitLoss}
          currency={currency}
        />
      </div>
    </section>
  );
};

export default Overview;