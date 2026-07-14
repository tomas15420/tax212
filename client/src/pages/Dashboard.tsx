import Dividends from '@/components/Dividends'
import Portfolio from '@/components/Portfolio'
import TaxOverview from '@/components/TaxOverview'
import Transactions from '@/components/Transactions'
import Overview from '@/features/overview/Overview'

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