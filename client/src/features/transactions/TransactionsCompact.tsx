import { Link } from "@tanstack/react-router"
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter } from "@/components/ui/card"
import { useGetTransactions } from "@/api/tax212";
import { TransactionsTable } from "./TransactionTable";

const TransactionsCompact = () => {
  const { data, isLoading } = useGetTransactions({ size: 10, sort: ["filledAt.desc"] });

  const transactions = data?.status === 200 ? data?.data : undefined;

  const totalItems = transactions?.totalItems || 0;
  const currentLength = transactions?.items?.length || 0;

  return (
    <Card className="w-full">
      <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between p-4 md:p-6">
        <div className="space-y-1">
          <CardTitle className="text-xl font-bold md:text-2xl">Historie transakcí</CardTitle>
          <CardDescription>
            Přehled posledních 10 transakcí z Trading212
          </CardDescription>
        </div>
      </CardHeader>

      <CardContent className="p-0">
        <TransactionsTable pagedTransactions={transactions} isLoading={isLoading} />
      </CardContent>

      {totalItems > currentLength && (
        <CardFooter className="border-t p-4 flex justify-center bg-muted/20">
          <Link
            to="/transactions"
            className="text-sm text-primary font-medium hover:underline focus:outline-none"
          >
            Zobrazit dalších {totalItems - currentLength} položek
          </Link>
        </CardFooter>
      )}
    </Card>
  )
}

export default TransactionsCompact;