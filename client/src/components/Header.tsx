import { useState } from "react";
import { ShieldCheck, Sun, Moon } from "lucide-react";

const Header = () => {
    const [isDark, setIsDark] = useState(false);

    return (
        <header className="border-b bg-background px-4 py-3 md:px-6">
            <div className="mx-auto flex max-w-7xl items-center justify-between gap-4">
                <div className="flex items-center gap-2 md:gap-3">
                    <div className="rounded-lg bg-primary/10 p-2 text-primary shrink-0">
                        <ShieldCheck className="h-5 w-5 md:h-6 md:w-6" />
                    </div>
                    <div className="min-w-0">
                        <h1 className="text-lg font-bold tracking-tight text-foreground md:text-xl leading-none">
                            Tax212
                        </h1>
                        <p className="mt-0.5 text-xs text-muted-foreground md:text-sm truncate">
                            Trading212 Portfolio &amp; Tax guard
                        </p>
                    </div>
                </div>

                <div className="flex items-center gap-2 md:gap-4 shrink-0">
                    <button className="inline-flex h-9 items-center justify-center rounded-md bg-primary px-3 text-xs font-medium text-primary-foreground shadow transition-colors hover:bg-primary/90 focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:pointer-events-none disabled:opacity-50 sm:px-4 sm:text-sm">
                        Aktualizovat
                    </button>

                    <button
                        onClick={() => setIsDark(!isDark)}
                        aria-label="Přepnout tmavý režim"
                        className="inline-flex h-9 w-9 items-center justify-center rounded-md border border-input bg-background text-sm font-medium transition-colors hover:bg-accent hover:text-accent-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                    >
                        {isDark ? (
                            <Moon className="h-[1.2rem] w-[1.2rem] text-foreground" />
                        ) : (
                            <Sun className="h-[1.2rem] w-[1.2rem] text-foreground" />
                        )}
                    </button>
                </div>
            </div>
        </header>
    );
};

export default Header;