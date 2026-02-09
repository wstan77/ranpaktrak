export const useDateFormat = () => {
  const getISOFormat = (date: string, shortMonths?: boolean) => {
    const newDate = new Date(date);
    const month = newDate.toLocaleString("en-US", {
      month: shortMonths ? "short" : "long",
    });

    return `${month} ${newDate.getDate()}, ${newDate.getFullYear()}`;
  };

  return {
    getISOFormat,
  };
};
