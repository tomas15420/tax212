import type { ReactNode } from "react";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { formatCurrency } from "@/utils/utils";

interface StatCardProps {
    title: string;
    value: number;
    description: string;
    icon: ReactNode;
}

export const StatCard = ({ title, value, description, icon }: StatCardProps) => {
    return (
        <Card className="flex flex-col justify-between">
            <CardHeader className="p-4 md:p-6 pb-2">
                <div className="flex items-center justify-between">
                    <span className="text-xs font-medium text-muted-foreground md:text-sm">{title}</span>
                    <div className="rounded-md bg-primary/10 p-2 text-primary">
                        {icon}
                    </div>
                </div>
            </CardHeader>
            <CardContent className="p-4 md:p-6 pt-0 space-y-2">
                <div className="text-2xl font-bold tracking-tight text-foreground md:text-3xl">
                    {formatCurrency(value, "CZK")}
                </div>
                <p className="text-xs text-muted-foreground leading-normal pt-1">
                    {description}
                </p>
            </CardContent>
        </Card>
    );
};