import DividendsCompact from '@/components/DividendsCompact'
import TransactionsCompact from '@/components/TransactionsCompact'
import Overview from '@/features/overview/Overview'
import Portfolio from '@/features/portfolio/Portfolio'
import TaxOverview from '@/features/tax-overview/TaxOverview'
import { createFileRoute } from '@tanstack/react-router'

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

export const Route = createFileRoute('/Dashboard')({
  component: Dashboard,
});


export default Dashboard