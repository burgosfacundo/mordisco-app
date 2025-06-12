import { ReactNode, useState } from "react";
import { GlobalContext } from "./global.context";
import { UserResponse } from "../interfaces/UserResponse.model";

interface GlobalProviderProps {
  children: ReactNode;
}

export const GlobalProvider = ({ children }: GlobalProviderProps) => {
  const [value, setValue] = useState<number>(0);
  const [user, setUser] = useState<UserResponse | null>(null);

  return (
    <GlobalContext.Provider value={{ value, setValue, user, setUser }}>
      {children}
    </GlobalContext.Provider>
  );
};
