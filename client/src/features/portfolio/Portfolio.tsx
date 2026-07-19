import { useState } from "react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";
import { formatCurrency } from "@/utils/utils";
import { cn } from "@/lib/utils";
import { useGetPortfolio } from "@/api/tax212";
import { ErrorCard } from "@/components/ErrorCard";
import PortfolioLoadingSkeleton from "./PortfolioLoadingSkeleton";

const currentDate = new Date().toISOString();
const INITIAL_LIMIT = 5;

export const Portfolio = () => {
  const [showSold, setShowSold] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);
  const { data, isLoading, isError, refetch } = useGetPortfolio({ "date": currentDate, "includeSold": showSold });

  const portfolio = data?.status === 200 ? data?.data : null;
  const items = portfolio?.items || [];

  if (isLoading && !portfolio) {
    return <PortfolioLoadingSkeleton rows={INITIAL_LIMIT} />;
  }

  if (isError) {
    return <ErrorCard onRetry={refetch} />;
  }

  const displayItems = isExpanded ? items : items.slice(0, INITIAL_LIMIT);

  return (
    <TooltipProvider>
      <Card className="w-full">
        <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between p-4 md:p-6">
          <div className="space-y-1">
            <CardTitle className="text-xl font-bold md:text-2xl">Stav portfolia</CardTitle>
            <CardDescription>
              Přehled otevřených pozic a plnění {portfolio?.holdingPeriodYears || 3}letého časového testu
            </CardDescription>
          </div>

          <div
            onClick={() => setShowSold(!showSold)}
            className="flex items-center space-x-3 rounded-xl border border-border/60 bg-muted/30 px-4 py-2.5 hover:bg-muted/60 transition-all duration-200 cursor-pointer select-none self-start sm:self-auto shadow-sm"
          >
            <Switch
              id="show-sold"
              checked={showSold}
              onCheckedChange={setShowSold}
              onClick={(e) => e.stopPropagation()}
            />
            <Label
              htmlFor="show-sold"
              className="text-xs font-medium text-foreground/80 cursor-pointer leading-none md:text-sm"
            >
              Zobrazit uzavřené pozice
            </Label>
          </div>
        </CardHeader>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="w-[100px] pl-4 md:pl-6">Ticker</TableHead>
                  <TableHead>Název</TableHead>
                  <TableHead className="text-right">Celkem kusů</TableHead>
                  <TableHead className="text-right">Průměrný nákup</TableHead>
                  <TableHead className="text-center">Splněno ({portfolio?.holdingPeriodYears || 3}y test)</TableHead>
                  <TableHead className="w-[280px] pr-4 md:pr-6">Detail časového testu</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {items.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={6} className="h-24 text-center text-muted-foreground">
                      Žádné aktivní pozice k zobrazení.
                    </TableCell>
                  </TableRow>
                ) : (
                  displayItems.map((item) => {
                    const totalQty = item.availableQuantity || 0;
                    const safeQty = item.taxFreeQuantity || 0;
                    const inTaxQty = item.inTaxQuantity || 0;

                    const safePercentage = totalQty > 0
                      ? Math.round((safeQty / totalQty) * 100)
                      : 0;

                    return (
                      <TableRow key={item.ticker}>
                        <TableCell className="font-bold text-foreground pl-4 md:pl-6">
                          {item?.instrument?.marketTicker || "-"}
                        </TableCell>

                        <TableCell className="max-w-[150px] truncate">
                          <span className="font-medium">{item?.instrument?.name || "-"}</span>
                        </TableCell>

                        <TableCell className="text-right font-mono">
                          {item.availableQuantity !== undefined ? item.availableQuantity.toLocaleString("cs-CZ", {
                            minimumFractionDigits: 3,
                            maximumFractionDigits: 3
                          }) : "-"}
                        </TableCell>

                        <TableCell className="text-right font-mono">
                          {formatCurrency(item.averageBuyPrice, "CZK")}
                        </TableCell>

                        <TableCell>
                          <div className="flex items-center justify-center gap-1.5">
                            <Tooltip>
                              <TooltipTrigger>
                                <Badge variant="secondary" className="bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-500/15 border-transparent cursor-help">
                                  {safeQty !== undefined ? safeQty.toLocaleString("cs-CZ", {
                                    minimumFractionDigits: 3,
                                    maximumFractionDigits: 3
                                  }) : "-"} safe
                                </Badge>
                              </TooltipTrigger>
                              <TooltipContent>
                                <p>Už držíte déle než {portfolio?.holdingPeriodYears || 3} roky (osvobozeno od daně)</p>
                              </TooltipContent>
                            </Tooltip>

                            <Tooltip>
                              <TooltipTrigger>
                                <Badge variant="outline" className={cn(
                                  "cursor-help",
                                  inTaxQty > 0
                                    ? "bg-amber-500/5 text-amber-600 dark:text-amber-400 border-amber-500/20"
                                    : "text-muted-foreground border-border"
                                )}>
                                  {inTaxQty !== undefined ? inTaxQty.toLocaleString("cs-CZ", {
                                    minimumFractionDigits: 3,
                                    maximumFractionDigits: 3
                                  }) : "-"} čeká
                                </Badge>
                              </TooltipTrigger>
                              <TooltipContent>
                                <p>Pozice zakoupené v posledních {portfolio?.holdingPeriodYears || 3} letech (podléhá dani)</p>
                              </TooltipContent>
                            </Tooltip>
                          </div>
                        </TableCell>

                        <TableCell className="pr-4 md:pr-6">
                          <div className="space-y-2 py-1">
                            <div className="flex items-center justify-between text-xs">
                              <span className="text-muted-foreground font-medium flex items-center gap-1">
                                Splněno:
                              </span>
                              <span className="font-bold text-foreground">{safePercentage} %</span>
                            </div>

                            <div className="w-full bg-amber-500/10 rounded-full h-3 overflow-hidden">
                              <div
                                className="h-full rounded-full transition-all duration-500 ease-out bg-emerald-500"
                                style={{ width: `${safePercentage}%` }}
                              />
                            </div>
                          </div>
                        </TableCell>
                      </TableRow>
                    );
                  })
                )}
                {items.length > INITIAL_LIMIT && (
                  <TableRow>
                    <TableCell colSpan={6} className="text-center py-4">
                      <button
                        onClick={() => setIsExpanded(!isExpanded)}
                        className="text-sm text-primary font-medium hover:underline focus:outline-none"
                      >
                        {isExpanded ? "Zobrazit méně" : `Zobrazit dalších ${items.length - INITIAL_LIMIT} položek`}
                      </button>
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </div>
        </CardContent>
      </Card>
    </TooltipProvider>
  );
};

export default Portfolio;