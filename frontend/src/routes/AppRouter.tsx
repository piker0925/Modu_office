import { Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "../contexts/AuthContext";
import MainLayout from "../layouts/MainLayout";
import AuthLayout from "../layouts/AuthLayout";

import LoginPage from "../features/auth/LoginPage";
import SignupPage from "../features/auth/SignupPage";
import RoomsListPage from "../features/rooms/RoomsListPage";
import RoomDetailPage from "../features/rooms/RoomDetailPage";

// Placeholder Components (to be moved to features later)
const MyBookings = () => <div className="card"><h1>My Bookings</h1><p>Your upcoming meetings</p></div>;
const AdminDashboard = () => <div className="card"><h1>Admin Dashboard</h1><p>Stats and logs</p></div>;

function ProtectedRoute({ children, role }: { children: React.ReactNode, role?: 'USER' | 'ADMIN' }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/" replace />;
  return children;
}

export default function AppRouter() {
  return (
    <AuthProvider>
      <Routes>
        {/* Public Routes (Auth) */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
        </Route>

        {/* Protected Application Routes */}
        <Route element={<MainLayout />}>
          <Route path="/" element={<Navigate to="/rooms" replace />} />
          <Route path="/rooms" element={
            <ProtectedRoute>
              <RoomsListPage />
            </ProtectedRoute>
          } />
          <Route path="/rooms/:id" element={
            <ProtectedRoute>
              <RoomDetailPage />
            </ProtectedRoute>
          } />
          <Route path="/my-bookings" element={
            <ProtectedRoute>
              <MyBookings />
            </ProtectedRoute>
          } />

          {/* Admin Routes */}
          <Route path="/admin" element={
            <ProtectedRoute role="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          } />
        </Route>
      </Routes>
    </AuthProvider>
  );
}
