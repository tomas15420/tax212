import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/Transactions')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/Transactions"!</div>
}
