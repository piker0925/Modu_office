import { Routes, Route } from "react-router-dom";

function Login() {
  return <h1>Login Page</h1>;
}

function Rooms() {
  return <h1>Rooms Page</h1>;
}

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/rooms" element={<Rooms />} />
    </Routes>
  );
}
