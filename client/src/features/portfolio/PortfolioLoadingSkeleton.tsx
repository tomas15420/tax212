import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"

interface PortfolioLoadingSkeletonProps {
    rows: number;
}

const PortfolioLoadingSkeleton = ({ rows }: PortfolioLoadingSkeletonProps) => {
    return (
        <Card className="w-full">
            <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between p-4 md:p-6">
                <div className="space-y-2">
                    <Skeleton className="h-7 w-36" />
                    <Skeleton className="h-4 w-72" />
                </div>
                <Skeleton className="h-10 w-48 rounded-xl self-start sm:self-auto" />
            </CardHeader>
            <CardContent className="p-0">
                <div className="overflow-x-auto">
                    <Table>
                        <TableHeader>
                            <TableRow>
                                <TableHead className="w-[100px] pl-4 md:pl-6"><Skeleton className="h-4 w-12" /></TableHead>
                                <TableHead><Skeleton className="h-4 w-16" /></TableHead>
                                <TableHead className="text-right"><Skeleton className="h-4 w-20 ml-auto" /></TableHead>
                                <TableHead className="text-right"><Skeleton className="h-4 w-24 ml-auto" /></TableHead>
                                <TableHead className="text-center"><Skeleton className="h-4 w-28 mx-auto" /></TableHead>
                                <TableHead className="w-[280px] pr-4 md:pr-6"><Skeleton className="h-4 w-32 ml-auto" /></TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {Array.from({ length: rows }).map((_, index) => (
                                <TableRow key={index}>
                                    <TableCell className="pl-4 md:pl-6"><Skeleton className="h-5 w-16" /></TableCell>
                                    <TableCell><Skeleton className="h-5 w-28" /></TableCell>
                                    <TableCell><Skeleton className="h-5 w-20 ml-auto" /></TableCell>
                                    <TableCell><Skeleton className="h-5 w-24 ml-auto" /></TableCell>
                                    <TableCell>
                                        <div className="flex justify-center gap-1.5">
                                            <Skeleton className="h-5 w-20 rounded-full" />
                                            <Skeleton className="h-5 w-20 rounded-full" />
                                        </div>
                                    </TableCell>
                                    <TableCell className="pr-4 md:pr-6">
                                        <div className="space-y-2 py-1">
                                            <div className="flex justify-between">
                                                <Skeleton className="h-3 w-12" />
                                                <Skeleton className="h-3 w-8" />
                                            </div>
                                            <Skeleton className="h-3 w-full rounded-full" />
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

export default PortfolioLoadingSkeleton