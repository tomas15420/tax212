import DividendPage from '@/features/dividends/DividendPage'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dividends')({
  component: RouteComponent,
})

function RouteComponent() {
  return <DividendPage />
}
