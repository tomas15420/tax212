import { useState, useEffect } from "react";
import { ShieldCheck, Sun, Moon, RefreshCw, Loader2, Menu } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { Link } from "@tanstack/react-router";
import { getGetPortfolioQueryKey, getGetSummaryQueryKey, getGetYearlyReportQueryKey, usePerformFullSync } from "@/api/tax212";
import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { toast, Toaster } from "sonner";

const Header = () => {
    const queryClient = useQueryClient();
    const [isOpen, setIsOpen] = useState(false);

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
            onSuccess: (data) => {
                if (data.status > 299) {
                    toast.error("Chyba při aktualizaci dat", {
                        description: "Nepodařilo se aktualizovat data z Trading212 API.",
                        position: "top-center",
                    });
                    return;
                }
                queryClient.invalidateQueries({ queryKey: getGetSummaryQueryKey() });
                queryClient.invalidateQueries({
                    queryKey: getGetYearlyReportQueryKey(new Date().getFullYear()),
                });
                queryClient.invalidateQueries({ queryKey: getGetPortfolioQueryKey() });
                toast.success("Data byla úspěšně aktualizována", {
                    description: "Všechny informace byly úspěšně synchronizovány s Trading212 API.",
                    position: "top-center",
                });
            },
            onError: (error) => {
                console.error("Sync failed:", error);
                toast.error("Chyba při aktualizaci dat", {
                    description: "Nepodařilo se aktualizovat data z Trading212 API.",
                    position: "top-center",
                });
            }
        }
    });

    const navLinks = [
        { to: "/", label: "Dashboard" },
        { to: "/transactions", label: "Transakce" },
        { to: "/dividends", label: "Dividendy" },
    ];

    return (
        <>
            <Toaster closeButton />

            <header className="border-b bg-background px-4 py-3 md:px-6">
                <div className="mx-auto flex max-w-7xl items-center justify-between gap-4">
                    <div className="flex flex-1 items-center gap-6 min-w-0">
                        <div className="flex min-w-0 items-center gap-2 md:gap-3">
                            <div className="rounded-lg bg-primary/10 p-2 text-primary shrink-0">
                                <ShieldCheck className="h-5 w-5 md:h-6 md:w-6" />
                            </div>
                            <div className="min-w-0">
                                <h1 className="text-base font-bold tracking-tight text-foreground md:text-xl leading-none">
                                    Tax212
                                </h1>
                                <p className="mt-1 text-[10px] text-muted-foreground sm:text-xs md:text-sm truncate">
                                    Trading212 Portfolio &amp; Tax Guard
                                </p>
                            </div>
                        </div>

                        <nav className="hidden md:flex items-center gap-1">
                            {navLinks.map((item) => (
                                <Link
                                    key={item.to}
                                    to={item.to}
                                    className="px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground hover:bg-muted/50 rounded-md"
                                    activeProps={{ className: "text-foreground bg-muted font-semibold" }}
                                    activeOptions={item.to === "/" ? { exact: true } : undefined}
                                >
                                    {item.label}
                                </Link>
                            ))}
                        </nav>
                    </div>

                    <div className="flex items-center gap-2 shrink-0">
                        <Button
                            onClick={() => startSync()}
                            disabled={isPending}
                            variant="default"
                            size="sm"
                            className="h-9 w-9 p-0 sm:w-auto sm:px-4 sm:py-2 gap-1.5"
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

                        <Button
                            onClick={() => setIsDark(!isDark)}
                            variant="outline"
                            size="icon"
                            className="h-9 w-9"
                        >
                            {isDark ? (
                                <Moon className="h-[1.2rem] w-[1.2rem]" />
                            ) : (
                                <Sun className="h-[1.2rem] w-[1.2rem]" />
                            )}
                        </Button>

                        <Sheet open={isOpen} onOpenChange={setIsOpen}>
                            <SheetTrigger>
                                <Button
                                    variant="outline"
                                    size="icon"
                                    className="h-9 w-9 md:hidden"
                                >
                                    <Menu className="h-5 w-5" />
                                </Button>
                            </SheetTrigger>
                            <SheetContent side="right" className="w-[240px] sm:w-[300px] pt-12">
                                <nav className="flex flex-col gap-2">
                                    {navLinks.map((item) => (
                                        <Link
                                            key={item.to}
                                            to={item.to}
                                            onClick={() => setIsOpen(false)}
                                            className="px-3 py-2 text-base font-medium text-muted-foreground transition-colors hover:text-foreground hover:bg-muted/50 rounded-md"
                                            activeProps={{ className: "text-foreground bg-muted font-semibold" }}
                                            activeOptions={item.to === "/" ? { exact: true } : undefined}
                                        >
                                            {item.label}
                                        </Link>
                                    ))}
                                </nav>
                            </SheetContent>
                        </Sheet>
                    </div>
                </div>
            </header>
        </>
    );
};

export default Header;