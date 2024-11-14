// src/store/types.ts
export interface User {
    userName: string;
    role: string;
}

export interface State {
    user: User | null;
}
