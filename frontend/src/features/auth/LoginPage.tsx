import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Input from '../../components/Input';
import './LoginPage.css';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await new Promise(resolve => setTimeout(resolve, 1000));

            if (email === 'admin@modu.com' && password === 'admin') {
                login({
                    id: 'admin-id',
                    name: '관리자',
                    role: 'ADMIN'
                }, 'mock-jwt-token-admin');
                navigate('/admin');
                return;
            }

            const users = JSON.parse(localStorage.getItem('users') || '[]');
            const user = users.find((u: any) => u.email === email && u.password === password);

            if (user) {
                login({
                    id: user.email, // using email as id for now
                    name: user.name,
                    role: user.role || 'USER'
                }, 'mock-jwt-token-user');
                navigate('/rooms');
            } else {
                throw new Error('Invalid credentials');
            }
        } catch (err) {
            setError('로그인에 실패했습니다. 계정 정보를 확인해주세요.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card login-card">
            <div className="text-center mb-lg">
                <p className="login-title font-bold mb-sm">환영합니다!</p>
                <p className="login-subtitle text-muted text-sm">서비스 이용을 위해 로그인해주세요.</p>
            </div>

            {error && (
                <div className="alert-error mb-md text-sm p-sm rounded-md">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <Input
                    label="이메일 주소"
                    type="email"
                    placeholder="이메일을 입력하세요"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                />

                <Input
                    label="비밀번호"
                    type="password"
                    placeholder="비밀번호를 입력하세요"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    fullWidth
                />

                <button
                    type="submit"
                    className="btn btn-primary login-btn w-full mt-md text-md py-3"
                    disabled={loading}
                >
                    {loading ? '로그인 중...' : '로그인'}
                </button>
            </form>

            <div className="text-center mt-lg text-sm login-footer">
                <span className="text-muted">계정이 없으신가요? </span>
                <Link to="/signup" className="text-primary hover:underline">
                    회원가입
                </Link>
                <div className="mt-sm">
                    <Link to="/admin/signup" className="text-xs text-muted hover:text-primary">
                        관리자 회원가입
                    </Link>
                </div>
            </div>
        </div>
    );
}
