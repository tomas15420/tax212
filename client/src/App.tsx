import { Toaster } from "sonner";
import Header from "./components/Header";
import Dashboard from "./pages/Dashboard";

const App = () => {
  return (
    <div className="flex min-h-screen flex-col bg-background">
      <Header />
      <Toaster />
      <main className="flex-1 w-full px-4 py-6 md:px-6 md:py-8">
        <div className="mx-auto max-w-7xl w-full min-w-0 flex flex-col gap-10 md:gap-16">
          <Dashboard />
        </div>
      </main>
    </div>
  );
};

export default App;