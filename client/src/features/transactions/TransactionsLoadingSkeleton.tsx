import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

interface TransactionsLoadingSkeletonProps {
    rowsCount?: number;
}

export const TransactionsLoadingSkeleton = ({ rowsCount = 5 }: TransactionsLoadingSkeletonProps) => {
    return (
        <Card className="w-full">
            <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between p-4 md:p-6">
                <div className="space-y-2">
                    <Skeleton className="h-7 w-44" />
                    <Skeleton className="h-4 w-64" />
                </div>
                <Skeleton className="h-10 w-36 rounded-xl self-start sm:self-auto" />
            </CardHeader>
            <CardContent className="p-0">
                <div className="overflow-x-auto">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-[160px] pl-4 md:pl-6"><Skeleton className="h-4 w-20" /></TableHead>
                                <TableHead className="w-[100px]"><Skeleton className="h-4 w-12" /></TableHead>
                                <TableHead><Skeleton className="h-4 w-24" /></TableHead>
                                <TableHead className="text-right"><Skeleton className="h-4 w-16 ml-auto" /></TableHead>
                                <TableHead className="text-right"><Skeleton className="h-4 w-20 ml-auto" /></TableHead>
                                <TableHead className="text-right"><Skeleton className="h-4 w-24 ml-auto" /></TableHead>
                                <TableHead className="text-right pr-4 md:pr-6"><Skeleton className="h-4 w-12 ml-auto" /></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {Array.from({ length: rowsCount }).map((_, index) => (
                                <TableRow key={index} className="hover:bg-transparent">
                                    <TableCell className="pl-4 md:pl-6">
                                        <div className="flex flex-col sm:flex-row sm:items-center sm:gap-2">
                                            <Skeleton className="h-4 w-16" />
                                            <Skeleton className="h-3 w-10 sm:h-4" />
                                        </div>
                                    </TableCell>
                                    <TableCell>
                                        <Skeleton className="h-5 w-14 rounded-full" />
                                    </TableCell>
                                    <TableCell>
                                        <div className="flex flex-col gap-1">
                                            <Skeleton className="h-4 w-32" />
                                            <Skeleton className="h-3 w-12" />
                                        </div>
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Skeleton className="h-4 w-16 ml-auto" />
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Skeleton className="h-4 w-20 ml-auto" />
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <Skeleton className="h-4 w-24 ml-auto" />
                                    </TableCell>
                                    <TableCell className="text-right pr-4 md:pr-6">
                                        <div className="flex flex-col items-end gap-1">
                                            <Skeleton className="h-4 w-20" />
                                            <Skeleton className="h-3 w-14" />
                                        </div>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </div>
            </CardContent>
        </Card>
    )
}

export default TransactionsLoadingSkeleton;