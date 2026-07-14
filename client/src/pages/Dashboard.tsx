import Dividends from '@/components/Dividends'
import Portfolio from '@/components/Portfolio'
import Transactions from '@/components/Transactions'
import Overview from '@/features/overview/Overview'
import TaxOverview from '@/features/tax-overview/TaxOverview'


const Dashboard = () => {
    return (
        <>
            <Overview />
            <TaxOverview />
            <Portfolio />
            <Transactions />
            <Dividends />
        </>
    )
}

export default Dashboard