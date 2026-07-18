import { createFileRoute } from '@tanstack/react-router'
import Dashboard from './Dashboard'

export const Route = createFileRoute('/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <Dashboard />
}
