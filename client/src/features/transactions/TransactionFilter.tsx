import { format, isSameDay } from "date-fns"
import { cs } from "date-fns/locale"
import { Calendar as CalendarIcon, X } from "lucide-react"
import type { DateRange } from "react-day-picker"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Input } from "@/components/ui/input"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
    Field,
    FieldGroup,
    FieldLabel,
} from "@/components/ui/field"
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectGroup,
    SelectLabel,
    SelectValue,
} from "@/components/ui/select"
import { useEffect, useState } from "react"

export interface TransactionFilters {
    name: string
    ticker: string
    isin: string
    side: "all" | "buy" | "sell"
    dateRange: DateRange | undefined
}

interface TransactionFilterProps {
    onFilterChange: (filters: TransactionFilters) => void
    defaultValues?: TransactionFilters
}

const SIDE_OPTIONS = [
    { label: "Vše", value: "all" },
    { label: "Nákup (BUY)", value: "buy" },
    { label: "Prodej (SELL)", value: "sell" },
]


const TransactionFilter = ({ onFilterChange, defaultValues }: TransactionFilterProps) => {
    const [name, setName] = useState(defaultValues?.name ?? "")
    const [ticker, setTicker] = useState(defaultValues?.ticker ?? "")
    const [isin, setIsin] = useState(defaultValues?.isin ?? "")
    const [side, setSide] = useState(defaultValues?.side ?? "all")
    const [dateRange, setDateRange] = useState(defaultValues?.dateRange)

    useEffect(() => {
        onFilterChange({ name, ticker, isin, side, dateRange })
    }, [name, ticker, isin, side, dateRange, onFilterChange])

    const handleReset = () => {
        setName("")
        setTicker("")
        setIsin("")
        setSide("all")
        setDateRange(undefined)
    }

    const isFiltered = name || ticker || isin || side !== "all" || dateRange?.from

    const handleDateSelect = (range: DateRange | undefined) => {
        if (range?.from && range?.to && isSameDay(range.from, range.to)) {
            setDateRange({ from: range.from, to: undefined })
        } else {
            setDateRange(range)
        }
    }

    return (
        <div className="rounded-xl border bg-card p-4 shadow-sm">
            <form onSubmit={(e) => e.preventDefault()} className="space-y-4">
                <FieldGroup className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
                    <Field>
                        <FieldLabel htmlFor="filter-name">Název</FieldLabel>
                        <Input
                            id="filter-name"
                            placeholder="Např. Apple"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            className="h-9"
                        />
                    </Field>

                    <Field>
                        <FieldLabel htmlFor="filter-ticker">Ticker</FieldLabel>
                        <Input
                            id="filter-ticker"
                            placeholder="AAPL"
                            value={ticker}
                            onChange={(e) => setTicker(e.target.value)}
                            className="h-9 uppercase"
                        />
                    </Field>

                    <Field>
                        <FieldLabel htmlFor="filter-isin">ISIN</FieldLabel>
                        <Input
                            id="filter-isin"
                            placeholder="US0378331005"
                            value={isin}
                            onChange={(e) => setIsin(e.target.value)}
                            className="h-9 uppercase"
                        />
                    </Field>

                    <Field>
                        <FieldLabel htmlFor="filter-side">Směr</FieldLabel>
                        <Select
                            items={SIDE_OPTIONS}
                            value={side}
                            onValueChange={(value) => {
                                setSide((value as "all" | "buy" | "sell") ?? "all")
                            }}
                        >
                            <SelectTrigger id="filter-side" className="h-9">
                                <SelectValue placeholder="Všechny" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectGroup>
                                    <SelectLabel>Směr</SelectLabel>
                                    {SIDE_OPTIONS.map((item) => (
                                        <SelectItem key={item.value} value={item.value}>
                                            {item.label}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    </Field>

                    <Field className="sm:col-span-2 lg:col-span-1 xl:col-span-1">
                        <FieldLabel htmlFor="filter-date">Časové období</FieldLabel>
                        <Popover>
                            <PopoverTrigger render={<Button
                                id="filter-date"
                                variant={"outline"}
                                className={cn(
                                    "w-full h-9 justify-start text-left font-normal",
                                    !dateRange?.from && "text-muted-foreground"
                                )}
                            >
                                <CalendarIcon className="mr-2 h-4 w-4 shrink-0" />
                                {dateRange?.from ? (
                                    dateRange.to ? (
                                        <>
                                            {format(dateRange.from, "d. M. yyyy", { locale: cs })} -{" "}
                                            {format(dateRange.to, "d. M. yyyy", { locale: cs })}
                                        </>
                                    ) : (
                                        <>
                                            {format(dateRange.from, "d. M. yyyy", { locale: cs })} - současnost
                                        </>
                                    )
                                ) : (
                                    <span>Vyberte rozmezí</span>
                                )}
                            </Button>}>

                            </PopoverTrigger>
                            <PopoverContent className="w-auto p-0" align="start">
                                <Calendar
                                    mode="range"
                                    defaultMonth={dateRange?.from}
                                    onSelect={handleDateSelect}
                                    selected={dateRange}
                                    numberOfMonths={2}
                                    locale={cs}
                                    disabled={(date) => date > new Date()}
                                />
                            </PopoverContent>
                        </Popover>
                    </Field>
                </FieldGroup>

                {isFiltered && (
                    <div className="flex justify-end pt-2">
                        <Button
                            type="button"
                            variant="ghost"
                            size="sm"
                            onClick={handleReset}
                            className="h-8 px-2 lg:px-3 text-muted-foreground hover:text-foreground text-xs gap-1"
                        >
                            <X className="h-3.5 w-3.5" />
                            Vyčistit filtry
                        </Button>
                    </div>
                )}
            </form>
        </div >
    )
}

export default TransactionFilter