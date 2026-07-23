import DividendsCompact from '@/features/dividends/DividendsCompact'
import TransactionsCompact from '@/features/transactions/TransactionsCompact'
import Overview from '@/features/overview/Overview'
import Portfolio from '@/features/portfolio/Portfolio'
import TaxOverview from '@/features/tax-overview/TaxOverview'

const Dashboard = () => {
    return (
        <>
            <Overview />
            <TaxOverview />
            <Portfolio />
            <TransactionsCompact />
            <DividendsCompact />
        </>
    )
}

export default Dashboard