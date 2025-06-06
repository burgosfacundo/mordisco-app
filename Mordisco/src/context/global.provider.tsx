/*import { ReactNode, useState } from "react"
import { GlobalContext } from "./global.context"

const EmptyGlobalState: number = 0

interface GlobalProps {
  children: ReactNode
}

export const GlobalProvider = ({ children }: GlobalProps) => {
  const [value, setValue] = useState<number>(EmptyGlobalState)

  return (
    <GlobalContext.Provider value={{ value, setValue }}>{children}</GlobalContext.Provider>
  )
}*/

import { ReactNode, useState } from "react";
import { GlobalContext } from "./global.context";

interface GlobalProviderProps {
  children: ReactNode;
}

export const GlobalProvider = ({ children }: GlobalProviderProps) => {
  const [value, setValue] = useState<number>(0);

  return (
    <GlobalContext.Provider value={{ value, setValue }}>
      {children}
    </GlobalContext.Provider>
  );
};
