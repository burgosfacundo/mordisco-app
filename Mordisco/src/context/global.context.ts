/*import { createContext, useContext } from "react";

interface GlobalContextType {
  value: number | null
  setValue: React.Dispatch<React.SetStateAction<number>>
}

export const GlobalContext = createContext<GlobalContextType>({
  value: null,
  setValue: () => { }
})

export const useGlobalContext = () => {
  const context = useContext(GlobalContext)

  if (!context.value && context.value !== 0) {
    throw new Error("GlobalContext must be used within a GlobalContextProvider")
  }

  return context;
}*/
import { createContext, useContext } from "react";

interface GlobalContextType {
  value: number;
  setValue: React.Dispatch<React.SetStateAction<number>>;
}

export const GlobalContext = createContext<GlobalContextType | undefined>(undefined);

export const useGlobalContext = () => {
  const context = useContext(GlobalContext);
  if (!context) throw new Error("useGlobalContext must be used within GlobalProvider");
  return context;
};

