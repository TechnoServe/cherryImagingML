/**
 * Learn more about using TypeScript with React Navigation:
 * https://reactnavigation.org/docs/typescript/
 */

export type RootStackParamList = {
  Root: undefined;
  NotFound: undefined;
};

export type AppStackParamList = {
  Inference: undefined;
  SavedPredictions: undefined;
  Auth: undefined;
};

export type AuthParamList = {
  Login: undefined;
  ResetPassword: undefined;
  AuthStatus: undefined;
  Inference: undefined;
};

export type SavedPredictionsParamList = {
  SavedPredictions: undefined;
  SavedPrediction: { data: PredictionResult };
  Auth: undefined;
  Inference: undefined;
};

export type SavedPredictionParamList = {
  SavedPredictions: undefined;
};

export type InferenceParamList = {
  Inference: undefined;
  MainCamera: undefined;
  SavedPredictions: undefined;
  Auth: undefined;
  FinalPrediction: { data: PredictionInput };
};

export type PredictionInput = string;

export type PredictionResult = {
  original: typeof require;
  mask: typeof require;
  checked: boolean;
  scores: number[];
  synced: boolean;
  state: string;
  createdAt: number;
  syncedAt: number;
};
