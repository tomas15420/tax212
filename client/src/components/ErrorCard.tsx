import { AlertTriangle, RefreshCw } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface ErrorCardProps {
    title?: string;
    description?: string;
    onRetry?: () => void;
}

export const ErrorCard = ({
    title = "Chyba při načítání dat",
    description = "Nepodařilo se připojit k API. Zkontrolujte prosím připojení a zkuste to znovu.",
    onRetry,
}: ErrorCardProps) => {
    return (
        <Card className="border-destructive/20 bg-destructive/5 dark:bg-destructive/10">
            <CardContent className="flex flex-col items-center justify-center p-6 text-center md:p-10">
                <div className="rounded-full bg-destructive/10 p-3 text-destructive">
                    <AlertTriangle className="h-6 w-6" />
                </div>
                <h3 className="mt-4 text-lg font-semibold text-foreground">{title}</h3>
                <p className="mt-2 text-sm text-muted-foreground max-w-xs">{description}</p>

                {onRetry && (
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={onRetry}
                        className="mt-4 gap-2 border-destructive/20 hover:bg-destructive/10"
                    >
                        <RefreshCw className="h-3.5 w-3.5" />
                        Zkusit znovu
                    </Button>
                )}
            </CardContent>
        </Card>
    );
};