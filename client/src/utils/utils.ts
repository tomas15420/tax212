export const formatCurrency = (
  value: number | string | undefined | null,
  currencyCode = "CZK",
  showSign = false
): string => {
  if (value === undefined || value === null || value === "") return "-";

  const num = typeof value === "string" ? parseFloat(value) : value;
  if (isNaN(num)) return "-";

  return new Intl.NumberFormat("cs-CZ", {
    style: "currency",
    currency: currencyCode,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
    ...(showSign ? { signDisplay: "exceptZero" } : {}),
  }).format(num);
};

export const formatDateTime = (dateStr: string | undefined) => {
  if (!dateStr) return { date: "—", time: "" };

  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return { date: dateStr, time: "" };

  return {
    date: d.toLocaleDateString("cs-CZ"),
    time: d.toLocaleTimeString("cs-CZ", { hour: "2-digit", minute: "2-digit" }),
  };
};