import { createFileRoute } from '@tanstack/react-router'
import Dashboard from '../features/overview/Dashboard'

export const Route = createFileRoute('/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <Dashboard />
}
