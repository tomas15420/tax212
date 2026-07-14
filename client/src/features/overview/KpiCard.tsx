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
    className,
    valueClassName,
}: KpiCardProps) => {
    return (
        <Card className={cn("bg-muted/50 border-none shadow-none")}>
            <CardContent className="p-4 md:p-6">
                <div className="flex items-center justify-between gap-3 md:flex-col md:items-start md:justify-between md:gap-4">

                    <div className="flex items-center gap-3 md:w-full md:justify-between">
                        <span className="truncate text-xs font-medium text-muted-foreground md:text-sm">
                            {title}
                        </span>
                        <div className={cn("shrink-0 text-muted-foreground", className)}>
                            <Icon className="h-4 w-4" />
                        </div>
                    </div>

                    <strong
                        className={cn(
                            "text-lg font-bold tracking-tight text-foreground md:text-2xl lg:text-3xl",
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