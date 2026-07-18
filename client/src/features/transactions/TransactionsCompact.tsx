import { useGetTransactions } from "@/api/tax212";
import { TransactionsTable } from "./TransactionTable";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";

const TransactionsCompact = () => {
  const { data, isLoading } = useGetTransactions({ size: 10, sort: ["filledAt.desc"] });

  const transactions = data?.status === 200 ? data?.data : undefined;

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
    </Card>
  )
}

export default TransactionsCompact