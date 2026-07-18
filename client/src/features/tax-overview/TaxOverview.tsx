import { useMemo } from "react";
import { Receipt } from "lucide-react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { ErrorCard } from "@/components/ErrorCard";
import { formatCurrency } from "@/utils/utils";
import { useGetYearlyReport } from "@/api/tax212";
import { StatCard } from "./StatCard";
import { LimitCard } from "./LimitCard";

const TaxOverview = () => {
    const currentYear = new Date().getFullYear();
    const { data, isLoading, isError, refetch } = useGetYearlyReport(currentYear);

    const taxData = data?.status === 200 ? data?.data : null;

    const salesPercentage = useMemo(() =>
        Math.min(Math.round(((taxData?.totalSoldTaxable ?? 0) / (taxData?.assetSaleAnnualCap ?? 1)) * 100), 100),
        [taxData?.totalSoldTaxable, taxData?.assetSaleAnnualCap]
    );

    const dividendsPercentage = useMemo(() =>
        Math.min(Math.round(((taxData?.totalDividendsPaid ?? 0) / (taxData?.incidentalIncomeCap ?? 1)) * 100), 100),
        [taxData?.totalDividendsPaid, taxData?.incidentalIncomeCap]
    );

    const totalSales = (taxData?.totalSoldTaxFree || 0) + (taxData?.totalSoldTaxable || 0);

    if (isLoading) {
        return (
            <section className="space-y-4 md:space-y-6">
                <div className="space-y-2">
                    <Skeleton className="h-7 w-64 md:h-8" />
                    <Skeleton className="h-4 w-96 max-w-full" />
                </div>
                <div className="grid gap-4 grid-cols-1 md:grid-cols-3">
                    {[...Array(3)].map((_, i) => (
                        <Card key={i} className="border-border/60 bg-background/50">
                            <CardHeader className="p-4 md:p-6 pb-3">
                                <div className="flex items-center justify-between">
                                    <Skeleton className="h-4 w-28" />
                                    <Skeleton className="h-8 w-8 rounded-md" />
                                </div>
                            </CardHeader>
                            <CardContent className="p-4 md:p-6 pt-0 space-y-4">
                                <Skeleton className="h-8 w-36" />
                                <Skeleton className="h-3 w-full rounded-full" />
                                <Skeleton className="h-10 w-full" />
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </section>
        );
    }

    if (isError) {
        return <ErrorCard title="Chyba daňového přehledu" onRetry={refetch} />;
    }

    return (
        <section className="space-y-4 md:space-y-6">
            <div className="space-y-1">
                <h2 className="text-lg font-bold tracking-tight text-foreground md:text-2xl">
                    Daňové limity pro rok {currentYear}
                </h2>
                <p className="text-xs text-muted-foreground md:text-sm">
                    Sledování legislativních limitů pro osvobození od daně z příjmů fyzických osob.
                </p>
            </div>

            <div className="grid gap-4 grid-cols-1 md:grid-cols-3">
                <StatCard
                    title="Celkem prodáno"
                    value={totalSales}
                    description="Celkový objem uzavřených pozic v tomto kalendářním roce."
                    icon={<Receipt className="h-4 w-4" />}
                />

                <LimitCard
                    title="Prodáno zdanitelné"
                    currentValue={taxData?.totalSoldTaxable || 0}
                    limitValue={taxData?.assetSaleAnnualCap || 0}
                    percentage={salesPercentage}
                    description={`Limit ${formatCurrency(taxData?.assetSaleAnnualCap || 0, "CZK")} ročních příjmů pro kompletní osvobození od daně z prodeje cenných papírů (tzv. hodnotový test).`}
                />

                <LimitCard
                    title="Vyplaceno dividend"
                    currentValue={taxData?.totalDividendsPaid || 0}
                    limitValue={taxData?.incidentalIncomeCap || 0}
                    percentage={dividendsPercentage}
                    description={`Limit ${formatCurrency(taxData?.incidentalIncomeCap || 0, "CZK")} pro ostatní příjmy.`}
                />
            </div>
        </section>
    );
};

export default TaxOverview;