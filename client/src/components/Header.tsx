import { useState, useEffect } from "react";
import { ShieldCheck, Sun, Moon, RefreshCw, Loader2 } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { getGetSummaryQueryKey, getGetYearlyReportQueryKey, usePerformFullSync } from "@/api/tax212";
import { Button } from "@/components/ui/button";

const Header = () => {
    const queryClient = useQueryClient();

    const [isDark, setIsDark] = useState(() => {
        if (typeof window !== "undefined") {
            return (
                localStorage.getItem("theme") === "dark" ||
                (!("theme" in localStorage) &&
                    window.matchMedia("(prefers-color-scheme: dark)").matches)
            );
        }
        return false;
    });

    useEffect(() => {
        const root = window.document.documentElement;
        if (isDark) {
            root.classList.add("dark");
            localStorage.setItem("theme", "dark");
        } else {
            root.classList.remove("dark");
            localStorage.setItem("theme", "light");
        }
    }, [isDark]);

    const { mutate: startSync, isPending } = usePerformFullSync({
        mutation: {
            onSuccess: () => {
                queryClient.invalidateQueries({ queryKey: getGetSummaryQueryKey() });
                queryClient.invalidateQueries({
                    queryKey: getGetYearlyReportQueryKey(new Date().getFullYear()),
                });
            },
            onError: (error) => {
                console.error("Sync failed:", error);
            }
        }
    });

    return (
        <header className="border-b bg-background px-4 py-3 md:px-6">
            <div className="mx-auto flex max-w-7xl items-center justify-between gap-4">
                <div className="flex min-w-0 items-center gap-2 md:gap-3">
                    <div className="rounded-lg bg-primary/10 p-2 text-primary shrink-0">
                        <ShieldCheck className="h-5 w-5 md:h-6 md:w-6" />
                    </div>
                    <div className="min-w-0">
                        <h1 className="text-base font-bold tracking-tight text-foreground md:text-xl leading-none">
                            Tax212
                        </h1>
                        <p className="mt-1 text-[10px] text-muted-foreground sm:text-xs md:text-sm truncate">
                            Trading212 Portfolio &amp; Tax guard
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                    {/* Synchronizační tlačítko využívající shadcn Button */}
                    <Button
                        onClick={() => startSync()}
                        disabled={isPending}
                        variant="default"
                        size="sm"
                        className="h-9 w-9 p-0 sm:w-auto sm:px-4 sm:py-2 gap-1.5"
                        aria-label="Aktualizovat data"
                    >
                        {isPending ? (
                            <>
                                <Loader2 className="h-4 w-4 animate-spin shrink-0" />
                                <span className="hidden sm:inline">Aktualizuji...</span>
                            </>
                        ) : (
                            <>
                                <RefreshCw className="h-4 w-4 shrink-0" />
                                <span className="hidden sm:inline">Aktualizovat</span>
                            </>
                        )}
                    </Button>

                    {/* Tlačítko pro přepínání témat s využitím shadcn Button */}
                    <Button
                        onClick={() => setIsDark(!isDark)}
                        variant="outline"
                        size="icon"
                        className="h-9 w-9"
                        aria-label="Přepnout tmavý režim"
                    >
                        {isDark ? (
                            <Moon className="h-[1.2rem] w-[1.2rem]" />
                        ) : (
                            <Sun className="h-[1.2rem] w-[1.2rem]" />
                        )}
                    </Button>
                </div>
            </div>
        </header>
    );
};

export default Header;