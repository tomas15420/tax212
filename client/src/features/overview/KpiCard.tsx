import type { ComponentType } from "react";
import type { LucideProps } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface KpiCardProps {
    title: string;
    value: string;
    icon: ComponentType<LucideProps>;
    className?: string;
    valueClassName?: string;
}

const KpiCard = ({
    title,
    value,
    icon: Icon,
    className = "bg-primary/10 text-primary",
    valueClassName = "text-foreground",
}: KpiCardProps) => {
    return (
        <Card
            className={cn(
                "group relative overflow-hidden transition-all duration-300",
                "border-border/50 bg-background/50 backdrop-blur-sm",
                "hover:-translate-y-0.5 hover:border-primary/15 hover:shadow-lg hover:shadow-primary/5"
            )}
        >
            <div className="pointer-events-none absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 transition-opacity duration-500 group-hover:opacity-100" />

            <CardContent className="relative z-10 p-3.5 md:p-6">
                <div className="flex items-center justify-between gap-3 md:flex-col md:items-start md:justify-between md:gap-4">

                    <div className="flex items-center gap-3 md:w-full md:justify-between">
                        <span className="truncate text-xs font-medium tracking-tight text-muted-foreground md:text-sm">
                            {title}
                        </span>
                        <div
                            className={cn(
                                "shrink-0 rounded-lg p-1.5 transition-all duration-300 md:p-2",
                                "group-hover:scale-105 group-hover:shadow-sm",
                                className
                            )}
                        >
                            <Icon className="h-4 w-4 transition-transform duration-300 group-hover:rotate-3" />
                        </div>
                    </div>

                    <strong
                        className={cn(
                            "text-lg font-bold tracking-tight transition-colors duration-300 md:text-2xl lg:text-3xl",
                            valueClassName
                        )}
                    >
                        {value}
                    </strong>

                </div>
            </CardContent>
        </Card>
    );
};

export default KpiCard;